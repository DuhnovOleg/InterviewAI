from faster_whisper import WhisperModel

from app.core.config_transcribe import Settings


class WhisperService:
    def __init__(self, settings: Settings):
        self.settings = settings
        self.model = WhisperModel(
            model_size_or_path=settings.whisper_model,
            device=settings.whisper_device,
            compute_type=settings.whisper_compute_type,
        )

    def transcribe(self, audio_path: str, language_hint: str | None = None):
        language = language_hint or self.settings.whisper_language or None

        segments, info = self.model.transcribe(
            audio_path,
            language=language,
            beam_size=self.settings.whisper_beam_size,
            vad_filter=self.settings.whisper_vad_filter,
        )

        return list(segments), info