from app.dependencies import get_interview_service, get_session_store
from app.models.evaluation import EvaluateAnswerResponse, EvaluateAnswerRequest
from app.schemas.interview import StartInterviewRequest, AnswerRequest, FinalEvaluationRequest
from app.services.interview_service import InterviewService
from app.services.session_store import InMemorySessionStore
from app.core.config import settings
from fastapi import APIRouter, Depends, HTTPException

router = APIRouter(prefix="/interviews", tags=["interviews"])


@router.post("/start")
async def start_interview(
    request: StartInterviewRequest,
    service: InterviewService = Depends(get_interview_service),
):
    session = await service.start_interview(request.message)
    return {
        "session_id": session.session_id,
        "profession": session.profession,
        "level": session.level,
        "total_questions": len(session.questions),
        "question": session.questions[0],
        "question_number": 1,
        "model_name": settings.model_name,
        "message": (
            f"Привет! Я проведу собеседование на позицию {session.profession} "
            f"({session.level}). Всего будет {len(session.questions)} вопросов."
        ),
    }


@router.post("/answer")
async def submit_answer(
    request: AnswerRequest,
    service: InterviewService = Depends(get_interview_service),
):
    result = await service.submit_answer(
        session_id=request.session_id,
        answer=request.answer,
        input_type=request.input_type,
        confidence_score=request.confidence_score,
        response_time_seconds=request.response_time_seconds,
    )
    result["model_name"] = settings.model_name
    return result


@router.post("/final_evaluation")
async def final_evaluation(
    request: FinalEvaluationRequest,
    service: InterviewService = Depends(get_interview_service),
):
    result = await service.generate_final_evaluation(request.session_id)
    result["model_name"] = settings.model_name
    return result


@router.post("/evaluate_answer", response_model=EvaluateAnswerResponse)
async def evaluate_answer(
    payload: EvaluateAnswerRequest,
    service: InterviewService = Depends(get_interview_service),
):
    analysis = await service.analysis_service.analyze_answer(
        profession=payload.profession,
        question=payload.question,
        answer=payload.answer,
        response_time_seconds=payload.response_time_seconds,
        confidence_score_from_audio=payload.confidence_score,
    )

    return EvaluateAnswerResponse(
        overall_score=analysis.overall_score,
        correctness_score=analysis.correctness_score,
        completeness_score=analysis.completeness_score,
        clarity_score=analysis.clarity_score,
        relevance_score=analysis.relevance_score,
        grammar_score=analysis.grammar_score,
        confidence_score=analysis.confidence_score,
        response_speed_score=analysis.response_speed_score,
        feedback=analysis.feedback,
        strengths=analysis.strengths,
        weaknesses=analysis.weaknesses,
        decision=analysis.decision,
        follow_up_question=analysis.follow_up_question,
    )


@router.get("/{session_id}/generated_questions")
async def generated_questions(
    session_id: str,
    store: InMemorySessionStore = Depends(get_session_store),
):
    session = await store.get(session_id)
    if not session:
        raise HTTPException(status_code=404, detail="Сессия не найдена")

    return {
        "session_id": session.session_id,
        "profession": session.profession,
        "level": session.level,
        "model_name": settings.model_name,
        "questions": [
            {"number": index + 1, "text": question}
            for index, question in enumerate(session.questions)
        ],
    }


@router.get("/{session_id}/result")
async def interview_result(
    session_id: str,
    service: InterviewService = Depends(get_interview_service),
):
    result = await service.generate_final_evaluation(session_id)
    result["model_name"] = settings.model_name
    return result


@router.get("/{session_id}")
async def get_session(
    session_id: str,
    store: InMemorySessionStore = Depends(get_session_store),
):
    session = await store.get(session_id)
    if not session:
        raise HTTPException(status_code=404, detail="Сессия не найдена")

    return {
        "session_id": session.session_id,
        "profession": session.profession,
        "level": session.level,
        "is_complete": session.is_complete,
        "awaiting_stop_confirmation": session.awaiting_stop_confirmation,
        "current_question": (
            session.current_index + 1
            if not session.is_complete and session.current_index < len(session.questions)
            else None
        ),
        "total_questions": len(session.questions),
        "answers_given": len(session.answers),
        "progress": f"{len(session.answers)}/{len(session.questions)}",
        "model_name": settings.model_name,
    }
