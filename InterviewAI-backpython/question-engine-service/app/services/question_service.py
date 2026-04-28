from typing import List

from app.clients.ollama_client import OllamaClient
from app.utils.json_utils import extract_json_from_text


class QuestionService:
    def __init__(self, ollama_client: OllamaClient):
        self.ollama_client = ollama_client

    def get_fallback_questions(self, profession: str, level: str) -> list[str]:
        profession_lower = profession.lower()

        if "python" in profession_lower:
            return [
                "Что такое GIL в Python и как он влияет на многопоточность?",
                "Чем отличаются list, tuple и set в Python?",
                "Как работает async/await в Python?",
                "Что такое декораторы и где они применяются?",
                "Как в FastAPI организовать обработку запросов и валидацию данных?",
                "В чем разница между multiprocessing и threading в Python?",
                "Как вы работали с PostgreSQL или другой СУБД в Python-проектах?"
            ]

        if "java" in profession_lower:
            return [
                "В чем разница между HashMap и ConcurrentHashMap?",
                "Что такое Spring Bean и как работает dependency injection?",
                "Как устроены транзакции в Spring?",
                "Чем отличается interface от abstract class в Java?",
                "Как работает garbage collector в JVM?"
            ]

        return [
            f"Какие ключевые технологии и инструменты используются в направлении {profession}?",
            f"Какие типичные задачи решает специалист уровня {level} в сфере {profession}?",
            "Как вы подходите к диагностике и исправлению ошибок в рабочем приложении?",
            "Как вы проектируете структуру сервиса или модуля в production-среде?",
            "Какие практики тестирования вы считаете обязательными?"
        ]

    async def generate_questions(
            self,
            profession: str,
            level: str,
            num_questions: int,
            focus_areas: list[str] | None = None,
    ) -> list[str]:
        focus_text = ""
        if focus_areas:
            focus_text = f"Сфокусируйся на аспектах: {', '.join(focus_areas)}"

        prompt = f"""
    Ты — технический интервьюер.

    Сгенерируй {num_questions} технических вопросов для собеседования.

    Позиция: {profession}
    Уровень: {level}
    {focus_text}

    Требования:
    - все вопросы должны быть только на русском языке;
    - вопросы должны быть строго техническими;
    - не задавай общие HR-вопросы;
    - не начинай с вопроса про общий опыт работы;
    - вопросы должны соответствовать уровню {level};
    - верни только JSON;
    - формат ответа: массив строк.

    Пример:
    [
      "Что такое GIL в Python и как он влияет на многопоточность?",
      "Чем отличаются list, tuple и set в Python?",
      "Как работает async/await в Python?"
    ]
    """

        try:
            raw = await self.ollama_client.generate(prompt, temperature=0.7)
            parsed = extract_json_from_text(raw)

            questions = []

            if isinstance(parsed, list):
                questions = [str(q).strip() for q in parsed if str(q).strip()]

            elif isinstance(parsed, dict):
                if "questions" in parsed and isinstance(parsed["questions"], list):
                    questions = [str(q).strip() for q in parsed["questions"] if str(q).strip()]
                else:
                    questions = [
                        str(value).strip()
                        for value in parsed.values()
                        if isinstance(value, str) and str(value).strip()
                    ]

            questions = [q for q in questions if len(q) > 5]

            if len(questions) < num_questions:
                questions.extend(self.get_fallback_questions(profession, level))

            # убираем дубли
            unique_questions = []
            seen = set()
            for q in questions:
                normalized = q.strip().lower()
                if normalized not in seen:
                    seen.add(normalized)
                    unique_questions.append(q)

            return unique_questions[:num_questions]

        except Exception as e:
            print(f"Question generation error: {e}")
            return self.get_fallback_questions(profession, level)[:num_questions]