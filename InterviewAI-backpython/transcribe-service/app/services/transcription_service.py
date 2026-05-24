from dataclasses import dataclass

from fastapi import HTTPException

from app.core.config_transcribe import Settings
from app.models.response import TranscribeResponse
from app.services.transcribe_engine_registry import TranscribeEngineRegistry
from app.utils.audio import (
    cleanup_files,
    convert_to_wav_16k_mono,
    decode_base64_audio_to_webm,
    get_file_size_mb,
    get_wav_duration_seconds,
)


@dataclass
class TranscriptionService:
    settings: Settings
    engine_registry: TranscribeEngineRegistry

    def transcribe_base64(
        self,
        audio_base64: str,
        language_hint: str | None = None,
        engine: str | None = None,
    ) -> TranscribeResponse:
        webm_path = None
        wav_path = None

        try:
            webm_path = decode_base64_audio_to_webm(audio_base64)

            if get_file_size_mb(webm_path) > self.settings.max_audio_mb:
                raise HTTPException(
                    status_code=413,
                    detail=f"Аудиофайл слишком большой. Максимум {self.settings.max_audio_mb} MB",
                )

            wav_path = convert_to_wav_16k_mono(webm_path)
            duration_seconds = get_wav_duration_seconds(wav_path)

            selected_engine = self.engine_registry.get(engine)
            result = selected_engine.transcribe(
                wav_path=wav_path,
                language_hint=language_hint,
            )

            return TranscribeResponse(
                text=result.text,
                language=result.language,
                language_probability=result.language_probability,
                duration_seconds=duration_seconds,
                segments=result.segments or [],
                model=result.model,
            )

        except HTTPException:
            raise
        except ValueError as exc:
            raise HTTPException(status_code=400, detail=str(exc)) from exc
        except RuntimeError as exc:
            raise HTTPException(status_code=500, detail=str(exc)) from exc
        except Exception as exc:
            raise HTTPException(
                status_code=500,
                detail=f"Ошибка транскрибации: {str(exc)}",
            ) from exc
        finally:
            cleanup_files(webm_path, wav_path)