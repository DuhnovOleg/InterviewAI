from datetime import datetime
import uuid

from fastapi import HTTPException

from app.models.session import InterviewSession, AnswerRecord
from app.services.session_store import InMemorySessionStore
from app.services.parsing_service import ParsingService
from app.services.question_service import QuestionService
from app.services.analysis_service import AnalysisService


class InterviewService:
    STOP_KEYWORDS = [
        "стоп",
        "остановись",
        "остановим",
        "остановиться",
        "давай остановимся",
        "давай закончим",
        "закончим",
        "завершим",
        "завершить интервью",
        "хватит",
        "достаточно",
        "прекратить",
        "остановить интервью",
    ]

    CONFIRM_STOP_KEYWORDS = [
        "да",
        "давай",
        "подтверждаю",
        "подтверждаю завершение",
        "заканчиваем",
        "завершаем",
        "точно",
        "yes",
        "ok",
        "ага",
    ]

    CANCEL_STOP_KEYWORDS = [
        "нет",
        "не надо",
        "не заканчиваем",
        "продолжим",
        "давай продолжим",
        "хочу продолжить",
        "continue",
        "no",
    ]

    def __init__(
        self,
        store: InMemorySessionStore,
        parsing_service: ParsingService,
        question_service: QuestionService,
        analysis_service: AnalysisService,
    ):
        self.store = store
        self.parsing_service = parsing_service
        self.question_service = question_service
        self.analysis_service = analysis_service

    async def start_interview(self, message: str) -> InterviewSession:
        parsed = await self.parsing_service.parse_user_message(message)
        questions = await self.question_service.generate_questions(
            profession=parsed.profession,
            level=parsed.level,
            num_questions=parsed.num_questions,
        )

        session = InterviewSession(
            session_id=uuid.uuid4().hex[:12],
            profession=parsed.profession,
            level=parsed.level,
            questions=questions,
            original_message=message,
            started_at=datetime.utcnow(),
            current_question_started_at=datetime.utcnow(),
            awaiting_stop_confirmation=False,
        )

        await self.store.save(session)
        return session

    def _normalize_text(self, text: str) -> str:
        return " ".join(text.lower().strip().split())

    def _contains_any_phrase(self, text: str, phrases: list[str]) -> bool:
        normalized = self._normalize_text(text)
        return any(phrase in normalized for phrase in phrases)

    async def _detect_intent(self, session: InterviewSession, answer: str) -> str:
        normalized = self._normalize_text(answer)

        if session.awaiting_stop_confirmation:
            if self._contains_any_phrase(normalized, self.CONFIRM_STOP_KEYWORDS):
                return "CONFIRM_STOP"
            if self._contains_any_phrase(normalized, self.CANCEL_STOP_KEYWORDS):
                return "CANCEL_STOP"
        else:
            if self._contains_any_phrase(normalized, self.STOP_KEYWORDS):
                return "STOP_INTERVIEW"

        llm_intent = await self.analysis_service.detect_user_intent(
            answer=answer,
            awaiting_stop_confirmation=session.awaiting_stop_confirmation,
        )
        return llm_intent.intent

    async def submit_answer(
        self,
        session_id: str,
        answer: str,
        input_type: str = "text",
        confidence_score: float | None = None,
        response_time_seconds: float | None = None,
    ):
        session = await self.store.get(session_id)
        if not session:
            raise HTTPException(status_code=404, detail="Сессия не найдена")

        if session.is_complete:
            return self.complete_interview(session)

        intent = await self._detect_intent(session, answer)

        if session.awaiting_stop_confirmation:
            if intent == "CONFIRM_STOP":
                session.awaiting_stop_confirmation = False
                session.is_complete = True
                await self.store.save(session)

                result = self.complete_interview(session)
                result["stopped_early"] = True
                result["message"] = (
                    "Интервью завершено досрочно по вашей инициативе. "
                    f"Отвечено на {len(session.answers)} из {len(session.questions)} вопросов."
                )
                return result

            if intent == "CANCEL_STOP":
                session.awaiting_stop_confirmation = False
                await self.store.save(session)

                current_question = session.questions[session.current_index]
                return {
                    "session_id": session.session_id,
                    "interview_complete": False,
                    "awaiting_stop_confirmation": False,
                    "question": current_question,
                    "question_number": session.current_index + 1,
                    "total_questions": len(session.questions),
                    "message": "Продолжаем интервью. Вот текущий вопрос снова:"
                }

            current_question = session.questions[session.current_index]
            return {
                "session_id": session.session_id,
                "interview_complete": False,
                "awaiting_stop_confirmation": True,
                "question": current_question,
                "question_number": session.current_index + 1,
                "total_questions": len(session.questions),
                "message": "Пожалуйста, ответьте 'да', чтобы завершить интервью, или 'нет', чтобы продолжить."
            }

        if intent == "STOP_INTERVIEW":
            session.awaiting_stop_confirmation = True
            await self.store.save(session)

            current_question = session.questions[session.current_index]
            return {
                "session_id": session.session_id,
                "interview_complete": False,
                "awaiting_stop_confirmation": True,
                "question": current_question,
                "question_number": session.current_index + 1,
                "total_questions": len(session.questions),
                "message": "Вы точно хотите завершить интервью? Ответьте 'да' для завершения или 'нет' для продолжения."
            }

        current_question = session.questions[session.current_index]

        if response_time_seconds is None and session.current_question_started_at:
            response_time_seconds = (
                datetime.utcnow() - session.current_question_started_at
            ).total_seconds()

        analysis = await self.analysis_service.analyze_answer(
            profession=session.profession,
            question=current_question,
            answer=answer,
            response_time_seconds=response_time_seconds,
            confidence_score_from_audio=confidence_score,
        )

        session.answers.append(
            AnswerRecord(
                question=current_question,
                answer=answer,
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
                timestamp=datetime.utcnow(),
                response_time_seconds=response_time_seconds,
                input_type=input_type,
            )
        )

        if analysis.decision == "FOLLOW_UP" and analysis.follow_up_question:
            session.questions.insert(session.current_index + 1, analysis.follow_up_question)

        session.current_index += 1
        session.current_question_started_at = datetime.utcnow()

        if session.current_index >= len(session.questions):
            session.is_complete = True
            await self.store.save(session)
            return self.complete_interview(session)

        await self.store.save(session)
        next_question = session.questions[session.current_index]

        return {
            "session_id": session.session_id,
            "question": next_question,
            "question_number": session.current_index + 1,
            "total_questions": len(session.questions),
            "interview_complete": False,
            "awaiting_stop_confirmation": False,
            "previous_score": analysis.overall_score,
            "feedback": analysis.feedback,
            "strengths": analysis.strengths,
            "weaknesses": analysis.weaknesses,
            "message": f"Вопрос {session.current_index + 1} из {len(session.questions)}"
        }

    def complete_interview(self, session: InterviewSession):
        if not session.answers:
            return {
                "session_id": session.session_id,
                "interview_complete": True,
                "message": "Интервью завершено без ответов",
            }

        avg_score = sum(a.overall_score for a in session.answers) / len(session.answers)

        if avg_score >= 8.5:
            verdict = "Отличный результат."
            recommendation = "accepted"
        elif avg_score >= 7.0:
            verdict = "Хороший результат."
            recommendation = "conditional"
        elif avg_score >= 5.5:
            verdict = "Средний результат."
            recommendation = "reserve"
        else:
            verdict = "Требуется дополнительная подготовка."
            recommendation = "rejected"

        return {
            "session_id": session.session_id,
            "profession": session.profession,
            "level": session.level,
            "interview_complete": True,
            "total_questions": len(session.answers),
            "average_score": round(avg_score, 2),
            "recommendation": recommendation,
            "verdict": verdict,
            "answers": [
                {
                    "question_number": idx,
                    "question": a.question,
                    "your_answer": a.answer[:200] + "..." if len(a.answer) > 200 else a.answer,
                    "overall_score": a.overall_score,
                    "correctness_score": a.correctness_score,
                    "completeness_score": a.completeness_score,
                    "clarity_score": a.clarity_score,
                    "relevance_score": a.relevance_score,
                    "grammar_score": a.grammar_score,
                    "confidence_score": a.confidence_score,
                    "response_speed_score": a.response_speed_score,
                    "feedback": a.feedback,
                    "strengths": a.strengths,
                    "weaknesses": a.weaknesses,
                }
                for idx, a in enumerate(session.answers, start=1)
            ],
            "message": f"Собеседование завершено. Средний балл: {avg_score:.1f}/10",
        }

    async def generate_final_evaluation(self, session_id: str):
        session = await self.store.get(session_id)
        if not session:
            raise HTTPException(status_code=404, detail="Сессия не найдена")

        if not session.answers:
            raise HTTPException(status_code=400, detail="Нет ответов для анализа")

        avg_overall = sum(a.overall_score for a in session.answers) / len(session.answers)
        avg_correctness = sum(a.correctness_score for a in session.answers) / len(session.answers)
        avg_completeness = sum(a.completeness_score for a in session.answers) / len(session.answers)
        avg_clarity = sum(a.clarity_score for a in session.answers) / len(session.answers)
        avg_relevance = sum(a.relevance_score for a in session.answers) / len(session.answers)
        avg_grammar = sum(a.grammar_score for a in session.answers) / len(session.answers)

        voice_answers = [a for a in session.answers if a.input_type == "voice"]
        avg_confidence = (
            sum(a.confidence_score for a in voice_answers) / len(voice_answers)
            if voice_answers else None
        )

        known_speed_answers = [a for a in session.answers if a.response_speed_score > 0]
        avg_response_speed = (
            sum(a.response_speed_score for a in known_speed_answers) / len(known_speed_answers)
            if known_speed_answers else None
        )

        strengths = []
        weaknesses = []

        for answer in session.answers:
            strengths.extend(answer.strengths)
            weaknesses.extend(answer.weaknesses)

        strengths = list(dict.fromkeys(strengths))[:10]
        weaknesses = list(dict.fromkeys(weaknesses))[:10]

        consistency_score = 10 - (
            max(a.overall_score for a in session.answers) - min(a.overall_score for a in session.answers)
        )

        if avg_overall >= 8.5:
            hire_recommendation = "Нанимать"
            recommended_level = session.level
        elif avg_overall >= 7.0:
            hire_recommendation = "Можно рассматривать"
            recommended_level = session.level
        elif avg_overall >= 5.5:
            hire_recommendation = "Нужна дополнительная проверка"
            recommended_level = "junior"
        else:
            hire_recommendation = "Пока не рекомендуется"
            recommended_level = "ниже заявленного уровня"

        improvement_plan = []
        if avg_correctness < 7:
            improvement_plan.append("Углубить технические знания и повысить точность ответов")
        if avg_completeness < 7:
            improvement_plan.append("Давать более полные и развернутые ответы")
        if avg_clarity < 7:
            improvement_plan.append("Улучшить структуру и логичность изложения")
        if avg_grammar < 7:
            improvement_plan.append("Повысить грамотность письменной речи")
        if avg_confidence is not None and avg_confidence < 7:
            improvement_plan.append("Поработать над уверенностью устных ответов")
        if avg_response_speed is not None and avg_response_speed < 7:
            improvement_plan.append("Потренировать скорость реакции на технические вопросы")

        red_flags = []
        if avg_correctness < 5:
            red_flags.append("Низкая корректность ответов")
        if avg_relevance < 5:
            red_flags.append("Часть ответов слабо связана с вопросами")
        if avg_grammar < 5:
            red_flags.append("Низкий уровень грамотности речи")

        return {
            "session_id": session.session_id,
            "profession": session.profession,
            "declared_level": session.level,
            "overall_score": round(avg_overall, 2),
            "technical_score": round((avg_correctness + avg_completeness + avg_relevance) / 3, 2),
            "correctness_score": round(avg_correctness, 2),
            "completeness_score": round(avg_completeness, 2),
            "clarity_score": round(avg_clarity, 2),
            "relevance_score": round(avg_relevance, 2),
            "grammar_score": round(avg_grammar, 2),
            "confidence_score": round(avg_confidence, 2) if avg_confidence is not None else None,
            "response_speed_score": round(avg_response_speed, 2) if avg_response_speed is not None else None,
            "consistency_score": round(max(0, consistency_score), 2),
            "hire_recommendation": hire_recommendation,
            "recommended_level": recommended_level,
            "strengths": strengths,
            "weaknesses": weaknesses,
            "red_flags": red_flags,
            "improvement_plan": improvement_plan,
            "question_breakdown": [
                {
                    "question": a.question,
                    "overall_score": a.overall_score,
                    "correctness_score": a.correctness_score,
                    "completeness_score": a.completeness_score,
                    "clarity_score": a.clarity_score,
                    "relevance_score": a.relevance_score,
                    "grammar_score": a.grammar_score,
                    "confidence_score": a.confidence_score,
                    "response_speed_score": a.response_speed_score,
                    "feedback": a.feedback,
                }
                for a in session.answers
            ],
            "summary": (
                f"Кандидат прошел интервью на позицию {session.profession}. "
                f"Общий уровень знаний оценивается на {avg_overall:.1f}/10. "
                f"Наиболее сильные стороны: {', '.join(strengths[:3]) if strengths else 'не выявлены явно'}. "
                f"Зоны роста: {', '.join(weaknesses[:3]) if weaknesses else 'минимальны'}."
            )
        }