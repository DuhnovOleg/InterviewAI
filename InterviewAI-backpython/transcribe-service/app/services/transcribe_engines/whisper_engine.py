from pathlib import Path

from app.models.response import SegmentResponse
from app.services.transcribe_engines.base import (
    BaseTranscribeEngine,
    EngineTranscriptionResult,
)
from app.services.whisper_service import WhisperService


class WhisperEngine(BaseTranscribeEngine):
    name = "whisper"

    def __init__(self, whisper_service: WhisperService, model_name: str):
        self.whisper_service = whisper_service
        self.model_name = model_name

    def transcribe(
        self,
        wav_path: str | Path,
        language_hint: str | None = None,
    ) -> EngineTranscriptionResult:
        segments, info = self.whisper_service.transcribe(
            str(wav_path),
            language_hint=language_hint,
        )

        texts: list[str] = []
        segment_items: list[SegmentResponse] = []

        for seg in segments:
            text = seg.text.strip()

            if text:
                texts.append(text)

            segment_items.append(
                SegmentResponse(
                    start=round(float(seg.start), 3),
                    end=round(float(seg.end), 3),
                    text=text,
                )
            )

        language_probability_raw = getattr(info, "language_probability", None)

        return EngineTranscriptionResult(
            text=" ".join(texts).strip(),
            language=getattr(info, "language", None),
            language_probability=(
                round(float(language_probability_raw), 4)
                if language_probability_raw is not None
                else None
            ),
            segments=segment_items,
            model=f"whisper:{self.model_name}",
        )