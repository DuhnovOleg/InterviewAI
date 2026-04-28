from contextlib import asynccontextmanager

import httpx
import uvicorn
from app.api.v1.router import api_router
from app.core.config import settings
from app.services.session_store import InMemorySessionStore
from fastapi import FastAPI


@asynccontextmanager
async def lifespan(app: FastAPI):
    app.state.http_client = httpx.AsyncClient(timeout=settings.llm_timeout_seconds)
    app.state.session_store = InMemorySessionStore()
    yield
    await app.state.http_client.aclose()


app = FastAPI(
    title=settings.app_name,
    lifespan=lifespan,
)

app.include_router(api_router, prefix=settings.api_prefix)

if __name__ == "__main__":
    uvicorn.run("app.main:app", host="0.0.0.0", port=8001, reload=True)