import wave
from pathlib import Path

import numpy as np

from app.core.config_transcribe import Settings
from app.services.transcribe_engines.base import (
    BaseTranscribeEngine,
    EngineTranscriptionResult,
)


class CoquiEngine(BaseTranscribeEngine):
    name = "coqui"

    def __init__(self, settings: Settings):
        try:
            from STT import Model
        except ImportError as exc:
            raise RuntimeError(
                "Coqui STT не установлен. Попробуй: pip install stt. "
                "Лучше тестировать в отдельном venv на Python 3.8/3.9."
            ) from exc

        self.settings = settings
        self.model = Model(settings.coqui_model_path)

        if settings.coqui_scorer_path:
            self.model.enableExternalScorer(settings.coqui_scorer_path)

    def _read_wav_int16(self, wav_path: str | Path) -> tuple[int, np.ndarray]:
        with wave.open(str(wav_path), "rb") as wf:
            sample_rate = wf.getframerate()
            channels = wf.getnchannels()
            sample_width = wf.getsampwidth()

            if channels != 1:
                raise ValueError(f"Coqui ожидает mono WAV, получено channels={channels}")

            if sample_width != 2:
                raise ValueError(
                    f"Coqui ожидает 16-bit WAV, получено sample_width={sample_width}"
                )

            frames = wf.readframes(wf.getnframes())

        audio = np.frombuffer(frames, dtype=np.int16)
        return sample_rate, audio

    def transcribe(
        self,
        wav_path: str | Path,
        language_hint: str | None = None,
    ) -> EngineTranscriptionResult:
        sample_rate, audio = self._read_wav_int16(wav_path)

        expected_rate = self.model.sampleRate()
        if sample_rate != expected_rate:
            raise ValueError(
                f"Coqui модель ожидает sample_rate={expected_rate}, "
                f"но WAV имеет sample_rate={sample_rate}. "
                f"Сейчас сервис конвертирует в 16000 Hz. "
                f"Используй Coqui-модель на 16000 Hz или измени конвертацию."
            )

        text = self.model.stt(audio).strip()

        return EngineTranscriptionResult(
            text=text,
            language=language_hint,
            language_probability=None,
            segments=[],
            model=f"coqui:{self.settings.coqui_model_path}",
        )