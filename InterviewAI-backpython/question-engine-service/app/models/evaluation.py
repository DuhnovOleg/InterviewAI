from pydantic import BaseModel, Field


class EvaluateAnswerRequest(BaseModel):
    profession: str
    level: str | None = None
    question: str
    answer: str
    input_type: str | None = "text"
    response_time_seconds: float | None = None
    confidence_score: float | None = None


class EvaluateAnswerResponse(BaseModel):
    overall_score: float = Field(ge=0, le=10)
    correctness_score: float = Field(ge=0, le=10)
    completeness_score: float = Field(ge=0, le=10)
    clarity_score: float = Field(ge=0, le=10)
    relevance_score: float = Field(ge=0, le=10)
    grammar_score: float = Field(ge=0, le=10)
    confidence_score: float = Field(ge=0, le=10)
    response_speed_score: float = Field(ge=0, le=10)
    feedback: str
    strengths: list[str]
    weaknesses: list[str]
    decision: str = "NEXT"
    follow_up_question: str | None = None