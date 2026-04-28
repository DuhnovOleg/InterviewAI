from pydantic import BaseModel, Field
from typing import Optional


class StartInterviewRequest(BaseModel):
    message: str = Field(min_length=3, max_length=1000)


class AnswerRequest(BaseModel):
    session_id: str = Field(min_length=8, max_length=64)
    answer: str = Field(min_length=1, max_length=5000)
    input_type: str = Field(default="text")  # text / voice
    confidence_score: Optional[float] = None  # от transcribe-service
    response_time_seconds: Optional[float] = None


class FinalEvaluationRequest(BaseModel):
    session_id: str = Field(min_length=8, max_length=64)