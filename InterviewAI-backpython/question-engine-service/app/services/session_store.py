from typing import Dict, Optional

from app.models.session import InterviewSession


class InMemorySessionStore:
    def __init__(self) -> None:
        self._sessions: Dict[str, InterviewSession] = {}

    async def save(self, session: InterviewSession) -> None:
        self._sessions[session.session_id] = session

    async def get(self, session_id: str) -> Optional[InterviewSession]:
        return self._sessions.get(session_id)

    async def count(self) -> int:
        return len(self._sessions)