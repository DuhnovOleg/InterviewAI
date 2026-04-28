from typing import List, Literal, Optional

from pydantic import BaseModel, Field


class ParsedInterviewRequest(BaseModel):
    profession: str = "разработчик"
    level: str = "middle"
    num_questions: int = Field(default=5, ge=1, le=15)


class AnswerAnalysis(BaseModel):
    overall_score: float = Field(ge=0, le=10)
    correctness_score: float = Field(ge=0, le=10)
    completeness_score: float = Field(ge=0, le=10)
    clarity_score: float = Field(ge=0, le=10)
    relevance_score: float = Field(ge=0, le=10)
    grammar_score: float = Field(ge=0, le=10)
    confidence_score: float = Field(default=0, ge=0, le=10)
    response_speed_score: float = Field(default=0, ge=0, le=10)

    feedback: str
    strengths: List[str] = Field(default_factory=list)
    weaknesses: List[str] = Field(default_factory=list)

    decision: Literal["NEXT", "FOLLOW_UP"] = "NEXT"
    follow_up_question: Optional[str] = None

class UserIntentResult(BaseModel):
    intent: Literal["ANSWER", "STOP_INTERVIEW", "CONFIRM_STOP", "CANCEL_STOP"]
    confidence: float = Field(default=0.0, ge=0, le=1)