import httpx
from app.core.config import settings

class OllamaClient:
    def __init__(self, client: httpx.AsyncClient):
        self._client = client

    async def generate(self, prompt: str, temperature: float = 0.7) -> str:
        response = await self._client.post(
            settings.ollama_url,
            json={
                "model": settings.model_name,
                "prompt": prompt,
                "stream": False,
                "options": {
                    "temperature": temperature,
                    "top_p": 0.9,
                },
            },
        )
        response.raise_for_status()
        data = response.json()
        return data.get("response", "")