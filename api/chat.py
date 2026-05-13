from fastapi import APIRouter
from pydantic import BaseModel

import rag.build as rag_build
from core.agent import agent

router = APIRouter()


class ChatRequest(BaseModel):
    message: str


@router.post("/chat")
async def chat_endpoint(request: ChatRequest):

    if rag_build.retriever is None:
        return {"response": "O sistema RAG ainda não foi inicializado."}

    docs = rag_build.retriever.invoke(request.message)

    if not docs:
        return {
            "response": "Não encontrei essa informação no PDF."
        }

    context = "\n\n".join([
        f"[Página {doc.metadata.get('page', 0) + 1}] {doc.page_content}"
        for doc in docs
    ])

    prompt = f"""
Você é um tutor de álgebra linear e vetorial.

Regras:
- Use SOMENTE o contexto fornecido
- Se não houver resposta no contexto, diga que não encontrou

CONTEXTO:
{context}

Pergunta:
{request.message}
"""

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
                    "content": prompt
                }
            ]
        },
        config=config
    )

    return {
        "response": response["messages"][-1].content
    }