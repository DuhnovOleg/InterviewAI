from datetime import datetime
from typing import List, Optional
from pydantic import BaseModel, Field


class AnswerRecord(BaseModel):
    question: str
    answer: str

    overall_score: float
    correctness_score: float
    completeness_score: float
    clarity_score: float
    relevance_score: float
    grammar_score: float
    confidence_score: float = 0
    response_speed_score: float = 0

    feedback: str
    strengths: List[str] = Field(default_factory=list)
    weaknesses: List[str] = Field(default_factory=list)

    timestamp: datetime
    response_time_seconds: Optional[float] = None
    input_type: str = "text"


class InterviewSession(BaseModel):
    session_id: str
    profession: str
    level: str
    questions: List[str]
    answers: List[AnswerRecord] = Field(default_factory=list)
    current_index: int = 0
    is_complete: bool = False
    original_message: str
    started_at: datetime
    current_question_started_at: Optional[datetime] = None
    awaiting_stop_confirmation: bool = False