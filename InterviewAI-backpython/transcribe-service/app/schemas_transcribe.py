from pydantic import BaseModel, Field


class TranscribeRequest(BaseModel):
    audioBase64: str = Field(min_length=1)
    languageHint: str | None = "ru"

    # whisper | vosk | coqui | nemo | kaldi
    engine: str | None = None


class TranscribeResponse(BaseModel):
    text: str
    engine: str