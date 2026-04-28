import re

from app.clients.ollama_client import OllamaClient
from app.core.config import settings
from app.schemas.llm import ParsedInterviewRequest
from app.utils.json_utils import extract_json_from_text


class ParsingService:
    def __init__(self, ollama_client: OllamaClient):
        self.ollama_client = ollama_client

    async def parse_user_message(self, message: str) -> ParsedInterviewRequest:
        prompt = f"""
Проанализируй сообщение кандидата и определи параметры собеседования.

Сообщение: "{message}"

Определи:
1. Профессию/позицию
2. Уровень (junior/middle/senior/lead), если не указан — middle
3. Количество вопросов, если не указано — {settings.default_questions_count}

Верни только JSON:
{{
  "profession": "строка",
  "level": "junior|middle|senior|lead",
  "num_questions": число
}}
"""
        try:
            raw = await self.ollama_client.generate(prompt, temperature=0.2)
            parsed = extract_json_from_text(raw)
            return ParsedInterviewRequest.model_validate(parsed)
        except Exception:
            return self.fallback_parse(message)

    def fallback_parse(self, message: str) -> ParsedInterviewRequest:
        message_lower = message.lower()

        professions = {
            "java": "Java разработчик",
            "python": "Python разработчик",
            "javascript": "JavaScript разработчик",
            "frontend": "Frontend разработчик",
            "backend": "Backend разработчик",
            "devops": "DevOps инженер",
            "менеджер": "Менеджер",
            "продавец": "Продавец-консультант",
            "дизайнер": "Дизайнер",
            "бухгалтер": "Бухгалтер",
            "hr": "HR-специалист",
            "маркетолог": "Маркетолог",
            "аналитик": "Аналитик",
        }

        profession = "разработчик"
        for key, value in professions.items():
            if key in message_lower:
                profession = value
                break

        if "junior" in message_lower or "джуниор" in message_lower:
            level = "junior"
        elif "senior" in message_lower or "синьор" in message_lower:
            level = "senior"
        elif "lead" in message_lower or "тимлид" in message_lower:
            level = "lead"
        else:
            level = "middle"

        numbers = re.findall(r"\d+", message_lower)
        num_questions = settings.default_questions_count
        for raw_number in numbers:
            value = int(raw_number)
            if 1 <= value <= settings.max_questions_count:
                num_questions = value
                break

        return ParsedInterviewRequest(
            profession=profession,
            level=level,
            num_questions=num_questions,
        )