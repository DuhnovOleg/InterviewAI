from abc import ABC, abstractmethod
from dataclasses import dataclass
from pathlib import Path

from app.models.response import SegmentResponse


@dataclass
class EngineTranscriptionResult:
    text: str
    model: str
    language: str | None = None
    language_probability: float | None = None
    segments: list[SegmentResponse] | None = None


class BaseTranscribeEngine(ABC):
    name: str

    @abstractmethod
    def transcribe(
        self,
        wav_path: str | Path,
        language_hint: str | None = None,
    ) -> EngineTranscriptionResult:
        pass