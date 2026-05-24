from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    app_name: str = "Question Engine Service"
    api_prefix: str = "/api/v1"

    ollama_url: str = Field(default="http://localhost:11434/api/generate")

    # mistral-nemo:12b-instruct-2407-q4_K_M
    # gemma3:4b
    # llama3.1:8b-instruct-q8_0
    # phi4-mini:latest
    # deepseek-r1:7b
    # granite3.3:8b
    # mistral:7b-instruct-v0.3-q8_0
    # ministral-3:8b
    # qwen3:8b

    model_name: str = Field(default="mistral-nemo:12b-instruct-2407-q4_K_M")
    llm_timeout_seconds: float = Field(default=230.0)

    default_questions_count: int = Field(default=5)
    max_questions_count: int = Field(default=15)

    redis_host: str = Field(default="localhost")
    redis_port: int = Field(default=6379)
    redis_db: int = Field(default=0)
    redis_password: str | None = Field(default=None)

    uvicorn_host: str = Field(default="0.0.0.0")
    uvicorn_port: int = Field(default=8001)

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore",
    )


settings = Settings()
