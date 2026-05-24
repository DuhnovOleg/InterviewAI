from pydantic import BaseModel, Field


class SegmentResponse(BaseModel):
    start: float
    end: float
    text: str


class TranscribeResponse(BaseModel):
    text: str
    language: str | None = None
    language_probability: float | None = None
    duration_seconds: float | None = None
    segments: list[SegmentResponse] = Field(default_factory=list)
    model: str


class HealthResponse(BaseModel):
    status: str
    model_loaded: bool
    model_name: str
    device: str
    engine: str