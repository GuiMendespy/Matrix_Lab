from fastapi import FastAPI
from pydantic import BaseModel
from langchain_google_genai import (
    ChatGoogleGenerativeAI,
    GoogleGenerativeAIEmbeddings
)
from langchain_community.document_loaders import PyPDFLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_chroma import Chroma

from dotenv import load_dotenv

import os
import socket
from zeroconf import ServiceInfo, Zeroconf


load_dotenv()

# FASTAPI
app = FastAPI()

GOOGLE_API_KEY = "AIzaSyCA_tZqUbTrZGE4vLAUugXzm_v1QyF3tZY"
PDF_PATH = os.getenv("PDF_PATH", "questoes.pdf")


llm = ChatGoogleGenerativeAI(
    model="gemini-2.5-flash",
    google_api_key=GOOGLE_API_KEY,
    temperature=0
)


embeddings = GoogleGenerativeAIEmbeddings(
    model="models/gemini-embedding-001",
    google_api_key=GOOGLE_API_KEY
)

retriever = None


# REQUEST DO APP
class ChatRequest(BaseModel):
    message: str


# =========================
# FUNÇÃO QUE CRIA O RAG
# =========================
def build_rag():
    global retriever

    print("Carregando PDF...")

    loader = PyPDFLoader(PDF_PATH)
    docs = loader.load()

    print(f"PDF carregado com {len(docs)} páginas.")

    splitter = RecursiveCharacterTextSplitter(
        chunk_size=1000,
        chunk_overlap=200
    )

    chunks = splitter.split_documents(docs)

    print(f"Documento dividido em {len(chunks)} chunks.")

    print("Criando banco vetorial...")
#aquik banco vetoial
    vectorstore = Chroma.from_documents(
        documents=chunks,
        embedding=embeddings,
        collection_name="pdf_rag"
    )
#usado para ser um recuperador de contexto , ele busca os trechos mais relevantes do rag
    retriever = vectorstore.as_retriever(
        search_kwargs={"k": 4}
    )

    print("RAG pronto.")


# =========================
# ENDPOINT DO APP
# =========================
@app.post("/chat")
async def chat_endpoint(request: ChatRequest):

    if retriever is None:
        return {
            "response": "O sistema RAG ainda não foi inicializado."
        }

    # Busca contexto no PDF
    docs = retriever.invoke(request.message)

    # Junta os chunks encontrados
    context = "\n\n".join([
        f"[Página {doc.metadata.get('page', 0) + 1}] {doc.page_content}"
        for doc in docs
    ])

    # Prompt do RAG
    prompt = f"""
Você é um assistente que responde perguntas usando SOMENTE o contexto abaixo.

Se a resposta não estiver no contexto, diga:
"Não encontrei essa informação no PDF."

================ CONTEXTO ================

{context}

==========================================

Pergunta:
{request.message}
"""

    # Chama o Gemini
    response = llm.invoke(prompt)

    # Retorna para o app
    return {
        "response": response.content
    }


#Recursos para configurar a rede
def start_mdns():
    try:

        # Descobre IP local
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

    # Cria o RAG antes do servidor subir
    build_rag()

    # Inicia mDNS
    zc = start_mdns()

    try:
        uvicorn.run(
            app,
            host="0.0.0.0",
            port=8000
        )

    finally:
        if zc:
            zc.close()