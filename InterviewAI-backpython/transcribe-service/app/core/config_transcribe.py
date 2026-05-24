from functools import lru_cache
from typing import Any

from pydantic import field_validator
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    app_name: str = "transcribe-service"
    app_port: int = 8002
    app_env: str = "dev"

    enable_cors: bool = True
    cors_origins: list[str] = [
        "http://localhost:5173",
        "http://127.0.0.1:5173",
        "http://localhost:8080",
    ]

    max_audio_mb: float = 20

    # whisper | vosk | coqui | nemo | kaldi
    transcribe_engine: str = "whisper"

    # faster-whisper
    whisper_model: str = "small"
    whisper_device: str = "cpu"
    whisper_compute_type: str = "int8"
    whisper_beam_size: int = 5
    whisper_language: str | None = "ru"
    whisper_vad_filter: bool = True

    # Vosk
    vosk_model_path: str = "models/vosk-model-small-ru-0.22"

    # Coqui STT
    coqui_model_path: str = "models/coqui/model.tflite"
    coqui_scorer_path: str | None = None

    # NVIDIA NeMo / Parakeet
    nemo_model_name_or_path: str = "nvidia/parakeet-tdt-0.6b-v2"
    nemo_device: str = "cpu"

    # Kaldi
    kaldi_decode_script: str = "scripts/kaldi_decode.bat"

    @field_validator("cors_origins", mode="before")
    @classmethod
    def parse_cors_origins(cls, value: Any) -> list[str]:
        if isinstance(value, str):
            return [item.strip() for item in value.split(",") if item.strip()]
        return value

    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"
        extra = "ignore"


@lru_cache
def get_settings() -> Settings:
    return Settings()