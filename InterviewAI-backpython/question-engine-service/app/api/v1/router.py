from app.api.v1.endpoints import interviews
from fastapi import APIRouter

api_router = APIRouter()
api_router.include_router(interviews.router)