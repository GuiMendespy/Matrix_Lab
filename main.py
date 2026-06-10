from fastapi import FastAPI
import uvicorn

from rag.build_rag import build_question_rag, build_theory_rag
from network.mdns import start_mdns

# Alterado: Dando um apelido claro para o router do chat
from api.chat import router as chat_router
from api.gerador import router as generate_router

app = FastAPI()

# Incluindo os routers com seus respectivos apelidos
app.include_router(chat_router)
app.include_router(generate_router)


if __name__ == "__main__":
    # Inicializa as variáveis para evitar NameError no bloco finally
    zc = None 

    try:
        build_question_rag()
    except Exception as e:
        print(f"Erro ao construir RAG de questões: {e}")
        exit(1)

    try:
        build_theory_rag()
    except Exception as e:
        print(f"Erro ao construir RAG de teoria: {e}")
        exit(1)

    try:
        zc = start_mdns()
    except Exception as e:
        print(f"Erro ao iniciar mDNS (Ignorando...): {e}")
        # Opcional: não dar exit(1) aqui se o mDNS não for obrigatório para rodar local
    
    try:
        uvicorn.run(
            app,
            host="0.0.0.0",
            port=8000
        )
    finally:
        if zc:
            zc.close()