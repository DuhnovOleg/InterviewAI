from pydantic import BaseModel, Field


class TranscribeBase64Request(BaseModel):
    audioBase64: str = Field(..., min_length=10)
    languageHint: str | None = None
    sessionId: str | None = None
    engine: str | None = None