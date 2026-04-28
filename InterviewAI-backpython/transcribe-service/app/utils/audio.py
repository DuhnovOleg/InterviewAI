import base64
import os
import re
import subprocess
import tempfile
import wave
from pathlib import Path


DATA_URL_RE = re.compile(r"^data:audio\/[a-zA-Z0-9.+-]+;base64,")


def strip_data_url_prefix(audio_base64: str) -> str:
    return DATA_URL_RE.sub("", audio_base64.strip())


def decode_base64_audio_to_webm(audio_base64: str) -> str:
    clean_base64 = strip_data_url_prefix(audio_base64)

    try:
        audio_bytes = base64.b64decode(clean_base64, validate=True)
    except Exception as exc:
        raise ValueError("Некорректный base64 аудиофайл") from exc

    tmp = tempfile.NamedTemporaryFile(delete=False, suffix=".webm")
    try:
        tmp.write(audio_bytes)
        tmp.flush()
        return tmp.name
    finally:
        tmp.close()


def convert_to_wav_16k_mono(input_path: str) -> str:
    output_file = tempfile.NamedTemporaryFile(delete=False, suffix=".wav")
    output_path = output_file.name
    output_file.close()

    cmd = [
        "ffmpeg",
        "-y",
        "-i",
        input_path,
        "-ac",
        "1",
        "-ar",
        "16000",
        "-f",
        "wav",
        output_path,
    ]

    try:
        subprocess.run(cmd, check=True, capture_output=True)
    except FileNotFoundError as exc:
        raise RuntimeError("ffmpeg не найден в системе") from exc
    except subprocess.CalledProcessError as exc:
        stderr = exc.stderr.decode("utf-8", errors="ignore")
        raise RuntimeError(f"ffmpeg не смог обработать аудио: {stderr}") from exc

    return output_path


def get_wav_duration_seconds(wav_path: str) -> float | None:
    try:
        with wave.open(wav_path, "rb") as wf:
            frames = wf.getnframes()
            rate = wf.getframerate()
            if rate <= 0:
                return None
            return round(frames / float(rate), 3)
    except Exception:
        return None


def get_file_size_mb(path: str) -> float:
    size_bytes = os.path.getsize(path)
    return size_bytes / (1024 * 1024)


def cleanup_files(*paths: str | None) -> None:
    for path in paths:
        if not path:
            continue
        try:
            Path(path).unlink(missing_ok=True)
        except Exception:
            pass