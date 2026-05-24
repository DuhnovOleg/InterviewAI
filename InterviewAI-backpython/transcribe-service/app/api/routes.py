from fastapi import APIRouter, Depends, Request

from app.core.config_transcribe import Settings, get_settings
from app.models.request import TranscribeBase64Request
from app.models.response import HealthResponse, TranscribeResponse
from app.services.transcription_service import TranscriptionService

router = APIRouter(prefix="/api/v1", tags=["Transcription"])


def get_transcription_service(
    request: Request,
    settings: Settings = Depends(get_settings),
) -> TranscriptionService:
    return TranscriptionService(
        settings=settings,
        engine_registry=request.app.state.engine_registry,
    )


@router.post("/transcribe", response_model=TranscribeResponse)
def transcribe_base64(
    request_body: TranscribeBase64Request,
    service: TranscriptionService = Depends(get_transcription_service),
) -> TranscribeResponse:
    return service.transcribe_base64(
        audio_base64=request_body.audioBase64,
        language_hint=request_body.languageHint,
        engine=request_body.engine,
    )


@router.get("/health", response_model=HealthResponse)
def health(
    request: Request,
    settings: Settings = Depends(get_settings),
) -> HealthResponse:
    whisper_loaded = (
        hasattr(request.app.state, "whisper_service")
        and request.app.state.whisper_service is not None
    )

    return HealthResponse(
        status="ok",
        model_loaded=whisper_loaded,
        model_name=settings.whisper_model,
        device=settings.whisper_device,
        engine=settings.transcribe_engine,
    )