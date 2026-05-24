from pathlib import Path

from app.config import settings


class NemoEngine:
    name = "nemo"

    def __init__(self):
        try:
            import torch
            import nemo.collections.asr as nemo_asr
        except ImportError as exc:
            raise RuntimeError(
                "NVIDIA NeMo is not installed. Try installing PyTorch first, then: "
                "pip install nemo_toolkit[asr]. "
                "If native Windows fails, use WSL2/Docker for this engine."
            ) from exc

        self.torch = torch

        if settings.nemo_device == "cuda" and torch.cuda.is_available():
            self.device = "cuda"
        else:
            self.device = "cpu"

        self.model = nemo_asr.models.ASRModel.from_pretrained(
            model_name=settings.nemo_model_name_or_path,
        )

        self.model = self.model.to(self.device)
        self.model.eval()

    def transcribe(self, wav_path: Path, language_hint: str | None = None) -> str:
        result = self.model.transcribe([str(wav_path)])

        if not result:
            return ""

        first = result[0]

        if isinstance(first, str):
            return first.strip()

        if hasattr(first, "text"):
            return str(first.text).strip()

        return str(first).strip()