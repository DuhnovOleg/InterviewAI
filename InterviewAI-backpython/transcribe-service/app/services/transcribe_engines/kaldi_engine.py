import subprocess
from pathlib import Path

from app.config import settings


class KaldiEngine:
    name = "kaldi"

    def transcribe(self, wav_path: Path, language_hint: str | None = None) -> str:
        script_path = Path(settings.kaldi_decode_script)

        if not script_path.exists():
            raise RuntimeError(
                f"Kaldi decode script not found: {script_path}. "
                "Set KALDI_DECODE_SCRIPT to your .bat/.sh decoder script."
            )

        command = [
            str(script_path),
            str(wav_path),
        ]

        result = subprocess.run(
            command,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            shell=False,
        )

        if result.returncode != 0:
            raise RuntimeError(
                "Kaldi decode failed. "
                f"stdout={result.stdout}; stderr={result.stderr}"
            )

        return result.stdout.strip()