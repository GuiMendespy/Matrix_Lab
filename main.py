from fastapi import FastAPI
import uvicorn

from rag.build import build_rag
from network.mdns import start_mdns
from api.chat import router

app = FastAPI()

app.include_router(router)


if __name__ == "__main__":

    try:
        build_rag()
    except Exception as e:
        print(f"Erro ao construir RAG: {e}")
        exit(1)

    try:
        zc = start_mdns()
    except Exception as e:
        print(f"Erro ao iniciar mDNS: {e}")
        exit(1)

    try:
        uvicorn.run(
            app,
            host="0.0.0.0",
            port=8000
        )
    finally:
        if zc:
            zc.close()