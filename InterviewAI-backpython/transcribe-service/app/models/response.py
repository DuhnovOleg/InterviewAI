from pydantic import BaseModel


class SegmentResponse(BaseModel):
    start: float
    end: float
    text: str


class TranscribeResponse(BaseModel):
    text: str
    language: str | None
    duration_seconds: float | None
    segments: list[SegmentResponse]
    model: str


class HealthResponse(BaseModel):
    status: str
    model_loaded: bool
    model_name: str
    device: str