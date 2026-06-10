from fastapi import APIRouter
from pydantic import BaseModel
import uuid

from core.agent import agent

router = APIRouter()


class ChatRequest(BaseModel):
    message: str
    session_id: str | None = None


@router.post("/chat")
async def chat_endpoint(request: ChatRequest):

    session_id = request.session_id or str(uuid.uuid4())

    config = {
        "configurable": {
            "thread_id": session_id
        }
    }

    response = agent.invoke(
        {
            "messages": [
                {
                    "role": "user",
                    "content": request.message
                }
            ]
        },
        config=config,
        timeout=120
    )

    return {
        "session_id": session_id,
        "response": response["messages"][-1].content
    }