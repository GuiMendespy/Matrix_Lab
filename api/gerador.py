from fastapi import APIRouter
from core.agente_gerador import generator_agent # Ajuste o nome conforme o arquivo
from api.chat import ChatRequest # Importe o modelo de dados do seu outro arquivo Pydantic

router = APIRouter()

@router.post("/generate")
async def generate_question(request: ChatRequest):
    # O invoke no LangGraph/LangChain geralmente espera a chave "messages"
    result = generator_agent.invoke(
        {"messages": [("user", request.message)]},
        config={"configurable": {"thread_id": "generator"}}
    )
    # Garante que pega a última mensagem de IA de forma segura
    return {"response": result["messages"][-1].content}