from fastapi import FastAPI
from pydantic import BaseModel
from langchain_google_genai import ChatGoogleGenerativeAI
import os

app = FastAPI()

# Configuração do seu LLM (como no seu código original)
llm = ChatGoogleGenerativeAI(
    model="gemini-2.5-flash", # Use a versão disponível
    google_api_key="AIzaSyBuiGc-vv6v4SHWtNwT4MVHJCqPM8Be52U",
    temperature=0
)

class ChatRequest(BaseModel):
    message: str

@app.post("/chat")
async def chat_endpoint(request: ChatRequest):
    # Aqui o LangChain processa a mensagem
    response = llm.invoke(request.message)
    return {"response": response.content}

if __name__ == "__main__":
    import uvicorn
    # Rode no 0.0.0.0 para ser acessível na rede local
    uvicorn.run(app, host="0.0.0.0", port=8000)