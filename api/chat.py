from fastapi import APIRouter
from pydantic import BaseModel
import uuid

from core.agent import run_agent

router = APIRouter()


class ChatRequest(BaseModel):
    message: str
    session_id: str | None = None


@router.post("/chat")
async def chat_endpoint(request: ChatRequest):
    session_id = request.session_id or str(uuid.uuid4())

    resposta = run_agent(request.message, session_id)

    return {
        "session_id": session_id,
        "response": resposta
    }