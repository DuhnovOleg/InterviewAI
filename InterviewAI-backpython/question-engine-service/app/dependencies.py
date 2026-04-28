import httpx
from app.clients.ollama_client import OllamaClient
from app.services.analysis_service import AnalysisService
from app.services.interview_service import InterviewService
from app.services.parsing_service import ParsingService
from app.services.question_service import QuestionService
from app.services.session_store import InMemorySessionStore
from fastapi import Request


def get_http_client(request: Request) -> httpx.AsyncClient:
    return request.app.state.http_client


def get_session_store(request: Request) -> InMemorySessionStore:
    return request.app.state.session_store


def get_ollama_client(request: Request) -> OllamaClient:
    return OllamaClient(get_http_client(request))


def get_interview_service(request: Request) -> InterviewService:
    ollama = get_ollama_client(request)
    store = get_session_store(request)
    return InterviewService(
        store=store,
        parsing_service=ParsingService(ollama),
        question_service=QuestionService(ollama),
        analysis_service=AnalysisService(ollama),
    )