import os
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    transcribe_engine: str = os.getenv("TRANSCRIBE_ENGINE", "whisper")

    ffmpeg_path: str = os.getenv("FFMPEG_PATH", "ffmpeg")
    temp_dir: str = os.getenv("TEMP_DIR", "tmp_audio")
    audio_sample_rate: int = int(os.getenv("AUDIO_SAMPLE_RATE", "16000"))

    whisper_model_name: str = os.getenv("WHISPER_MODEL_NAME", "small")
    whisper_device: str = os.getenv("WHISPER_DEVICE", "cpu")

    vosk_model_path: str = os.getenv(
        "VOSK_MODEL_PATH",
        "models/vosk-model-small-ru-0.22",
    )

    coqui_model_path: str = os.getenv(
        "COQUI_MODEL_PATH",
        "models/coqui/model.tflite",
    )
    coqui_scorer_path: str | None = os.getenv("COQUI_SCORER_PATH")

    nemo_model_name_or_path: str = os.getenv(
        "NEMO_MODEL_NAME_OR_PATH",
        "nvidia/parakeet-tdt-0.6b-v2",
    )
    nemo_device: str = os.getenv("NEMO_DEVICE", "cpu")

    kaldi_decode_script: str = os.getenv(
        "KALDI_DECODE_SCRIPT",
        "scripts/kaldi_decode.bat",
    )


settings = Settings()