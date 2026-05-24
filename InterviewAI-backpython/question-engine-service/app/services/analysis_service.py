from app.clients.ollama_client import OllamaClient
from app.schemas.llm import AnswerAnalysis, UserIntentResult
from app.utils.json_utils import extract_json_from_text


def _clamp_score(value, default=0.0):
    try:
        number = float(value)
    except Exception:
        number = default
    return max(0.0, min(10.0, number))


class AnalysisService:
    def __init__(self, ollama_client: OllamaClient):
        self.ollama_client = ollama_client

    async def analyze_answer(
        self,
        profession: str,
        question: str,
        answer: str,
        response_time_seconds: float | None = None,
        confidence_score_from_audio: float | None = None,
    ) -> AnswerAnalysis:
        response_time_text = (
            f"Время ответа кандидата: {response_time_seconds} секунд."
            if response_time_seconds is not None else
            "Время ответа неизвестно."
        )

        confidence_text = (
            f"Оценка уверенности речи по аудио: {confidence_score_from_audio} из 10."
            if confidence_score_from_audio is not None else
            "Данных об уверенности речи нет."
        )

        prompt = f"""
Ты — строгий технический интервьюер на позицию {profession}.

Вопрос:
{question}

Ответ кандидата:
{answer}

Дополнительные данные:
{response_time_text}
{confidence_text}

Оцени ответ кандидата по шкале от 0 до 10 и верни только JSON.

Правила:
- overall_score: общая оценка
- correctness_score: насколько ответ правильный
- completeness_score: насколько ответ полный
- clarity_score: насколько ответ логичный и понятный
- relevance_score: насколько ответ соответствует вопросу
- grammar_score: насколько грамотно написан ответ на русском языке
- confidence_score: уверенность кандидата; если нет данных, оцени по тексту косвенно
- response_speed_score — это ИМЕННО оценка по шкале от 0 до 10, где:
- 0-2: очень медленная реакция
- 3-4: медленная реакция
- 5-6: средняя скорость
- 7-8: хорошая скорость
- 9-10: очень быстрая и уверенная реакция
- feedback: краткий содержательный фидбек
- strengths: список сильных сторон
- weaknesses: список слабых сторон
- decision: NEXT или FOLLOW_UP
- follow_up_question: если ответ неполный, задай уточняющий вопрос, иначе null

Будь строгим:
- если ответ неполный, снижай балл
- если нет примеров, можно снижать балл
- если есть фактические ошибки, сильно снижай correctness_score
- если русский язык слабый, снижай grammar_score

Оцени ответ кандидата строго по шкале от 0 до 10:

0-2: ответ полностью неверный или не относится к вопросу
3-4: ответ очень слабый, содержит ошибки
5-6: частично правильный, но неполный
7-8: хороший ответ, но есть неточности
9-10: полный и точный ответ

ВАЖНО:
- если ответ не относится к вопросу → ставь 0-2
- если ответ содержит бред или оффтоп → ставь 0
- не будь вежливым, оцени строго

ВАЖНО:
- Отвечай СТРОГО на русском языке
- ВСЕ текстовые поля (feedback, strengths, weaknesses, follow_up_question) должны быть ТОЛЬКО на русском языке
- Запрещено использовать английский язык в тексте
- Если ответ не на русском — это ошибка

Правила оценки:
- если ответ не относится к вопросу, relevance_score = 0-1 и overall_score не может быть выше 2
- если кандидат пишет "не знаю", overall_score не может быть выше 2
- если ответ содержит оффтоп, шутку вместо ответа или бессмысленный текст, overall_score = 0-1
- если answer correctness_score < 3, feedback не должен содержать фразы "в целом правильный"
- не завышай оценку из вежливости

КРИТИЧЕСКИЕ ПРАВИЛА ФОРМАТА:
Верни только валидный JSON-объект.
Не используй Markdown.
Не используй ```json.
Не добавляй текст до или после JSON.
Все строковые значения должны быть в одну строку без настоящих переносов строк.
Если нужно разделить мысли, используй обычные предложения через точку.
Все кавычки внутри строк экранируй.
Все числовые поля должны быть числами от 0 до 10.
Не используй null для числовых полей.
feedback должен быть одной строкой до 500 символов.
strengths и weaknesses должны быть массивами коротких строк без переносов строк.

КРИТИЧЕСКИЕ ПРАВИЛА ЯЗЫКА:
Отвечай строго на русском языке.
Не используй английский язык, китайский язык, иероглифы или смешанные фразы.
Если модель не уверена, всё равно формируй ответ на русском.
Запрещены фразы с китайскими символами, английскими вставками вроде "well", "very good", "correct".

КРИТИЧЕСКИЕ ПРАВИЛА FOLLOW_UP:
Если кандидат пишет "не знаю", "не могу", "не смогу", "не понимаю", "давай следующий вопрос", "следующий вопрос", "перейдем дальше", "пропустим", "хватит про это", тогда:
- decision должен быть "NEXT";
- follow_up_question должен быть null;
- поставь низкую оценку за текущий ответ;
- не задавай уточняющий вопрос по этой же теме.

Если ответ нерелевантный, пустой или кандидат отказался отвечать:
- decision = "NEXT";
- follow_up_question = null.

FOLLOW_UP разрешен только если кандидат дал частично правильный ответ и явно есть смысл уточнить одну деталь.
Не задавай FOLLOW_UP, если кандидат уже явно не знает тему.
Не повторяй тот же вопрос другими словами.

Формат ответа:
{{
  "overall_score": число,
  "correctness_score": число,
  "completeness_score": число,
  "clarity_score": число,
  "relevance_score": число,
  "grammar_score": число,
  "confidence_score": число,
  "response_speed_score": число,
  "feedback": "строка",
  "strengths": ["строка"],
  "weaknesses": ["строка"],
  "decision": "NEXT или FOLLOW_UP",
  "follow_up_question": "строка или null"
}}
"""
        try:
            raw = await self.ollama_client.generate(prompt, temperature=0.2)
            parsed = extract_json_from_text(raw)

            score_fields = [
                "overall_score",
                "correctness_score",
                "completeness_score",
                "clarity_score",
                "relevance_score",
                "grammar_score",
                "confidence_score",
                "response_speed_score",
            ]

            if parsed.get("confidence_score") is None:
                parsed["confidence_score"] = confidence_score_from_audio or 0

            if parsed.get("response_speed_score") is None:
                parsed["response_speed_score"] = 0 if response_time_seconds is None else 6.0

            for field in score_fields:
                parsed[field] = _clamp_score(parsed.get(field), default=0.0)

            if parsed.get("follow_up_question") in ("", "null"):
                parsed["follow_up_question"] = None

            if parsed.get("decision") not in ("NEXT", "FOLLOW_UP"):
                parsed["decision"] = "NEXT"

            return AnswerAnalysis.model_validate(parsed)

        except Exception as e:
            print("ANALYSIS VALIDATION ERROR:", e)
            print("RAW PARSED:", parsed if 'parsed' in locals() else None)

            return AnswerAnalysis(
                overall_score=6.0,
                correctness_score=6.0,
                completeness_score=6.0,
                clarity_score=6.0,
                relevance_score=6.0,
                grammar_score=7.0,
                confidence_score=confidence_score_from_audio or 0,
                response_speed_score=0 if response_time_seconds is None else 6.0,
                feedback="Не удалось корректно обработать результат анализа.",
                strengths=["Ответ получен"],
                weaknesses=["Анализ выполнен по резервной схеме"],
                decision="NEXT",
                follow_up_question=None,
            )

    async def detect_user_intent(
        self,
        answer: str,
        awaiting_stop_confirmation: bool = False,
    ) -> UserIntentResult:
        prompt = f"""
Определи намерение пользователя по его последнему сообщению.

Сообщение пользователя:
"{answer}"

Контекст:
- Сейчас идет собеседование.
- awaiting_stop_confirmation = {awaiting_stop_confirmation}

Допустимые значения intent:
- ANSWER: пользователь отвечает на вопрос
- STOP_INTERVIEW: пользователь хочет остановить интервью
- CONFIRM_STOP: пользователь подтверждает, что хочет завершить интервью
- CANCEL_STOP: пользователь отменяет завершение и хочет продолжить интервью

Правила:
- Если пользователь пишет "стоп", "давай закончим", "хватит", "завершим", то это STOP_INTERVIEW.
- Если awaiting_stop_confirmation = true и пользователь пишет "да", "подтверждаю", "заканчиваем", то это CONFIRM_STOP.
- Если awaiting_stop_confirmation = true и пользователь пишет "нет", "продолжим", "не надо", то это CANCEL_STOP.
- Во всех остальных случаях это ANSWER.

Верни только JSON:
{{
  "intent": "ANSWER",
  "confidence": 0.95
}}
"""
        try:
            raw = await self.ollama_client.generate(prompt, temperature=0.0)
            parsed = extract_json_from_text(raw)
            return UserIntentResult.model_validate(parsed)
        except Exception:
            return UserIntentResult(intent="ANSWER", confidence=0.0)