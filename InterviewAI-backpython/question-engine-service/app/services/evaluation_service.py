import json
import re
from typing import Any

from app.models.evaluation import EvaluateAnswerRequest, EvaluateAnswerResponse


def clamp_score(value: Any, default: float = 0.0) -> float:
    try:
        number = float(value)
    except Exception:
        number = default

    if number < 0:
        return 0.0

    if number > 10:
        return 10.0

    return round(number, 2)


def extract_json_from_text(text: str) -> dict:
    if not text:
        raise ValueError("Empty model response")

    cleaned = text.strip()

    if cleaned.startswith("```"):
        cleaned = re.sub(r"^```(?:json)?", "", cleaned, flags=re.IGNORECASE).strip()
        cleaned = re.sub(r"```$", "", cleaned).strip()

    match = re.search(r"\{.*\}", cleaned, flags=re.DOTALL)

    if not match:
        raise ValueError(f"JSON object not found in model response: {text[:300]}")

    return json.loads(match.group(0))


class EvaluationService:
    def __init__(self, ollama_client):
        self.ollama_client = ollama_client

    async def evaluate_answer(self, payload: EvaluateAnswerRequest) -> EvaluateAnswerResponse:
        prompt = self._build_prompt(payload)

        try:
            raw = await self.ollama_client.generate(prompt, temperature=0.2)
            parsed = extract_json_from_text(raw)
            return self._to_response(parsed)
        except Exception as exc:
            print("EVALUATE ANSWER ERROR:", repr(exc))
            return self._fallback(payload)

    def _build_prompt(self, payload: EvaluateAnswerRequest) -> str:
        response_time_text = ""

        if payload.response_time_seconds is not None:
            response_time_text = (
                f"Время ответа кандидата: {payload.response_time_seconds:.2f} секунд.\n"
                "Важно: response_speed_score должен быть оценкой от 0 до 10, а не количеством секунд."
            )

        confidence_text = ""

        if payload.confidence_score is not None:
            confidence_text = (
                f"Оценка уверенности из внешнего источника: {payload.confidence_score} из 10."
            )

        return f"""
Ты — строгий технический интервьюер.

Позиция:
{payload.profession}

Уровень:
{payload.level or "не указан"}

Вопрос:
{payload.question}

Ответ кандидата:
{payload.answer}

Тип ответа:
{payload.input_type or "text"}

Дополнительные данные:
{response_time_text}
{confidence_text}

Оцени ответ кандидата строго по шкале от 0 до 10.

Правила оценки:
- overall_score: общая оценка.
- correctness_score: насколько ответ технически правильный.
- completeness_score: насколько ответ полный.
- clarity_score: насколько ответ логичный и понятный.
- relevance_score: насколько ответ относится к вопросу.
- grammar_score: грамотность русского языка.
- confidence_score: уверенность кандидата по тексту ответа.
- response_speed_score: оценка скорости ответа от 0 до 10.

ВАЖНО:
- Отвечай только на русском языке.
- Верни только JSON.
- Не добавляй markdown.
- Не добавляй пояснения вне JSON.
- Если ответ не относится к вопросу, relevance_score = 0-1 и overall_score не выше 2.
- Если кандидат пишет "не знаю", "не могу", "хз", overall_score не выше 2.
- Если ответ содержит шутку, оффтоп или бессмысленный текст, overall_score = 0-1.
- Если ответ частично правильный, но неполный, ставь 4-6.
- Если нет примеров там, где они нужны, снижай completeness_score.
- response_speed_score — это строго оценка от 0 до 10, НЕ секунды.
- Все числовые поля должны быть от 0 до 10.

Формат ответа:
{{
  "overall_score": 0,
  "correctness_score": 0,
  "completeness_score": 0,
  "clarity_score": 0,
  "relevance_score": 0,
  "grammar_score": 0,
  "confidence_score": 0,
  "response_speed_score": 0,
  "feedback": "строка на русском языке",
  "strengths": ["строка на русском языке"],
  "weaknesses": ["строка на русском языке"],
  "decision": "NEXT",
  "follow_up_question": null
}}
"""

    def _to_response(self, parsed: dict) -> EvaluateAnswerResponse:
        decision = str(parsed.get("decision") or "NEXT").upper()

        if decision not in {"NEXT", "FOLLOW_UP"}:
            decision = "NEXT"

        strengths = parsed.get("strengths") or []
        weaknesses = parsed.get("weaknesses") or []

        if not isinstance(strengths, list):
            strengths = [str(strengths)]

        if not isinstance(weaknesses, list):
            weaknesses = [str(weaknesses)]

        return EvaluateAnswerResponse(
            overall_score=clamp_score(parsed.get("overall_score")),
            correctness_score=clamp_score(parsed.get("correctness_score")),
            completeness_score=clamp_score(parsed.get("completeness_score")),
            clarity_score=clamp_score(parsed.get("clarity_score")),
            relevance_score=clamp_score(parsed.get("relevance_score")),
            grammar_score=clamp_score(parsed.get("grammar_score")),
            confidence_score=clamp_score(parsed.get("confidence_score"), default=5.0),
            response_speed_score=clamp_score(parsed.get("response_speed_score"), default=5.0),
            feedback=str(parsed.get("feedback") or "Ответ оценен."),
            strengths=[str(item) for item in strengths],
            weaknesses=[str(item) for item in weaknesses],
            decision=decision,
            follow_up_question=parsed.get("follow_up_question"),
        )

    def _fallback(self, payload: EvaluateAnswerRequest) -> EvaluateAnswerResponse:
        answer = payload.answer.strip().lower()

        if not answer:
            return EvaluateAnswerResponse(
                overall_score=0,
                correctness_score=0,
                completeness_score=0,
                clarity_score=0,
                relevance_score=0,
                grammar_score=0,
                confidence_score=0,
                response_speed_score=0,
                feedback="Ответ отсутствует.",
                strengths=[],
                weaknesses=["Кандидат не дал ответ."],
                decision="NEXT",
                follow_up_question=None,
            )

        if answer in {"не знаю", "я не знаю", "хз", "не могу", "не понимаю"}:
            return EvaluateAnswerResponse(
                overall_score=1,
                correctness_score=1,
                completeness_score=0,
                clarity_score=3,
                relevance_score=1,
                grammar_score=5,
                confidence_score=2,
                response_speed_score=5,
                feedback="Кандидат не дал содержательного ответа на технический вопрос.",
                strengths=[],
                weaknesses=["Нет технического содержания.", "Ответ не раскрывает вопрос."],
                decision="NEXT",
                follow_up_question=None,
            )

        if len(answer) < 15:
            return EvaluateAnswerResponse(
                overall_score=2,
                correctness_score=2,
                completeness_score=1,
                clarity_score=3,
                relevance_score=2,
                grammar_score=5,
                confidence_score=3,
                response_speed_score=5,
                feedback="Ответ слишком короткий и не раскрывает вопрос.",
                strengths=[],
                weaknesses=["Ответ слишком краткий.", "Не хватает технических деталей."],
                decision="NEXT",
                follow_up_question=None,
            )

        return EvaluateAnswerResponse(
            overall_score=5,
            correctness_score=5,
            completeness_score=4,
            clarity_score=5,
            relevance_score=5,
            grammar_score=6,
            confidence_score=5,
            response_speed_score=5,
            feedback="Ответ получен, но был использован резервный анализ. Нужна дополнительная проверка качества ответа.",
            strengths=["Ответ был предоставлен."],
            weaknesses=["Не удалось выполнить полноценную LLM-оценку."],
            decision="NEXT",
            follow_up_question=None,
        )