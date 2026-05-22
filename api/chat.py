from fastapi import APIRouter
from pydantic import BaseModel

from core.agent import agent

router = APIRouter()


class ChatRequest(BaseModel):
    message: str


@router.post("/chat")
async def chat_endpoint(request: ChatRequest):

    config = {
        "configurable": {
            "thread_id": "usuario-1"
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
        config=config
    )

    return {
        "response": response["messages"][-1].content
    }