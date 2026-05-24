import base64
import subprocess
import uuid
import wave
from pathlib import Path

import numpy as np

from app.config import settings


def ensure_temp_dir() -> Path:
    temp_dir = Path(settings.temp_dir)
    temp_dir.mkdir(parents=True, exist_ok=True)
    return temp_dir


def save_base64_audio(audio_base64: str) -> Path:
    temp_dir = ensure_temp_dir()

    if "," in audio_base64 and audio_base64.strip().startswith("data:"):
        audio_base64 = audio_base64.split(",", 1)[1]

    raw = base64.b64decode(audio_base64)

    input_path = temp_dir / f"{uuid.uuid4().hex}.input"
    input_path.write_bytes(raw)

    return input_path


def convert_to_wav_16k_mono(input_path: Path) -> Path:
    output_path = input_path.with_suffix(".wav")

    command = [
        settings.ffmpeg_path,
        "-y",
        "-i",
        str(input_path),
        "-ac",
        "1",
        "-ar",
        str(settings.audio_sample_rate),
        "-sample_fmt",
        "s16",
        str(output_path),
    ]

    result = subprocess.run(
        command,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
    )

    if result.returncode != 0:
        raise RuntimeError(f"ffmpeg conversion failed: {result.stderr}")

    return output_path


def base64_to_wav(audio_base64: str) -> tuple[Path, Path]:
    input_path = save_base64_audio(audio_base64)
    wav_path = convert_to_wav_16k_mono(input_path)
    return input_path, wav_path


def read_wav_int16(wav_path: Path) -> tuple[int, np.ndarray]:
    with wave.open(str(wav_path), "rb") as wf:
        sample_rate = wf.getframerate()
        channels = wf.getnchannels()
        sample_width = wf.getsampwidth()

        if channels != 1:
            raise ValueError(f"Expected mono WAV, got channels={channels}")

        if sample_width != 2:
            raise ValueError(f"Expected 16-bit WAV, got sample_width={sample_width}")

        frames = wf.readframes(wf.getnframes())

    audio = np.frombuffer(frames, dtype=np.int16)
    return sample_rate, audio


def cleanup_files(*paths: Path | None) -> None:
    for path in paths:
        try:
            if path and path.exists():
                path.unlink()
        except Exception:
            pass