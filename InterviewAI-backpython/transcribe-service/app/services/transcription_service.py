from dataclasses import dataclass

from fastapi import HTTPException

from app.core.config_transcribe import Settings
from app.models.response import SegmentResponse, TranscribeResponse
from app.services.whisper_service import WhisperService
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
    whisper_service: WhisperService

    def transcribe_base64(
        self,
        audio_base64: str,
        language_hint: str | None = None,
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

            segments, info = self.whisper_service.transcribe(
                wav_path,
                language_hint=language_hint,
            )

            segment_items: list[SegmentResponse] = []
            texts: list[str] = []
            avg_logprobs: list[float | None] = []
            no_speech_probs: list[float | None] = []

            for seg in segments:
                text = seg.text.strip()
                if text:
                    texts.append(text)

                avg_logprobs.append(getattr(seg, "avg_logprob", None))
                no_speech_probs.append(getattr(seg, "no_speech_prob", None))

                segment_items.append(
                    SegmentResponse(
                        start=round(float(seg.start), 3),
                        end=round(float(seg.end), 3),
                        text=text,
                    )
                )

            full_text = " ".join(texts).strip()

            return TranscribeResponse(
                text=full_text,
                language=getattr(info, "language", None),
                language_probability=round(getattr(info, "language_probability", 0.0), 4)
                if getattr(info, "language_probability", None) is not None
                else None,
                duration_seconds=duration_seconds,
                segments=segment_items,
                model=self.settings.whisper_model,
            )

        except HTTPException:
            raise
        except ValueError as exc:
            raise HTTPException(status_code=400, detail=str(exc)) from exc
        except RuntimeError as exc:
            raise HTTPException(status_code=500, detail=str(exc)) from exc
        except Exception as exc:
            raise HTTPException(status_code=500, detail=f"Ошибка транскрибации: {str(exc)}") from exc
        finally:
            cleanup_files(webm_path, wav_path)