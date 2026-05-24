from dataclasses import dataclass

from app.core.config_transcribe import Settings
from app.services.transcribe_engines.coqui_engine import CoquiEngine
from app.services.transcribe_engines.kaldi_engine import KaldiEngine
from app.services.transcribe_engines.nemo_engine import NemoEngine
from app.services.transcribe_engines.vosk_engine import VoskEngine
from app.services.transcribe_engines.whisper_engine import WhisperEngine
from app.services.whisper_service import WhisperService


@dataclass
class TranscribeEngineRegistry:
    settings: Settings
    whisper_service: WhisperService

    def __post_init__(self):
        self._engines = {}

    def get(self, engine_name: str | None):
        selected = (engine_name or self.settings.transcribe_engine or "whisper").strip().lower()

        if selected in self._engines:
            return self._engines[selected]

        if selected in {"whisper", "faster-whisper"}:
            engine = WhisperEngine(
                whisper_service=self.whisper_service,
                model_name=self.settings.whisper_model,
            )
        elif selected == "vosk":
            engine = VoskEngine(self.settings)
        elif selected in {"coqui", "coqui-stt", "stt"}:
            engine = CoquiEngine(self.settings)
        elif selected in {"nemo", "parakeet", "nemo-parakeet"}:
            engine = NemoEngine(self.settings)
        elif selected == "kaldi":
            engine = KaldiEngine(self.settings)
        else:
            raise ValueError(
                f"Неизвестный transcribe engine: {selected}. "
                f"Доступные: whisper, vosk, coqui, nemo, kaldi"
            )

        self._engines[selected] = engine
        return engine