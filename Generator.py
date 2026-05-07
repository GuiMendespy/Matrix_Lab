from fastapi import FastAPI
from pydantic import BaseModel
from langchain_google_genai import ChatGoogleGenerativeAI
import os
import socket
from zeroconf import ServiceInfo, Zeroconf #serve para nao ser necessario descobrir o IP , para nao precisar ficar trocando toda horaz

app = FastAPI()

# Configuração do LLM
llm = ChatGoogleGenerativeAI(
    model="gemini-2.5-flash", # Ajustado para a versão estável
    google_api_key="AIzaSyC1cozDinFz_Y7zPTdzAvDA7S2vSpbWuCw", # Dica: Use variáveis de ambiente por segurança!
    temperature=0
)

class ChatRequest(BaseModel):
    message: str

@app.post("/chat")
async def chat_endpoint(request: ChatRequest):
    response = llm.invoke(request.message)#recebe mensagem 
    return {"response": response.content} #retorna resposta aqui

# --- FUNÇÃO PARA DESCOBRIR O IP E ANUNCIAR NA REDE ---
def start_mdns():
    try:
        # Descobre o IP real do seu computador na rede local
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        local_ip = s.getsockname()[0]
        s.close()

        desc = {'path': '/chat'}
        info = ServiceInfo(
            "_http._tcp.local.",
            "MatrixServer._http._tcp.local.",
            addresses=[socket.inet_aton(local_ip)],
            port=8000,
            properties=desc,
            server="matrix-server.local.",
        )

        zeroconf = Zeroconf()
        zeroconf.register_service(info)
        print(f"Agente Online em: http://{local_ip}:8000")
        print(f"Anunciando como: http://matrix-server.local:8000")
        return zeroconf
    except Exception as e:
        print(f"Erro ao iniciar mDNS: {e}")
        return None

if __name__ == "__main__":
    import uvicorn
    # Inicia o anúncio do IP antes de subir o servidor
    zc = start_mdns()
    
    try:
        uvicorn.run(app, host="0.0.0.0", port=8000)
    finally:
        if zc:
            zc.close()
