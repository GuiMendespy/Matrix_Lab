from fastapi import FastAPI
from pydantic import BaseModel
from langchain_google_genai import (
    ChatGoogleGenerativeAI,
    GoogleGenerativeAIEmbeddings
)
from langchain_community.document_loaders import PyPDFLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_chroma import Chroma
from langgraph.checkpoint.memory import MemorySaver
from langchain.agents import create_agent

import os
import socket
from zeroconf import ServiceInfo, Zeroconf



# ESTRUTURAÇÃO DO FASTAPI
app = FastAPI()

# Define a estrutura de entrada do APP (entrada do usuário)
class ChatRequest(BaseModel):
    message: str



# MODELOS, EMBEDDINGS E VARIÁVEIS GLOBAIS

# Chaves e localização do PDF
GOOGLE_API_KEY = "AIzaSyCA_tZqUbTrZGE4vLAUugXzm_v1QyF3tZY"
PDF_PATH = os.getenv("PDF_PATH", "questoes.pdf")

# Modelo do Gemini
llm = ChatGoogleGenerativeAI(
    model="gemini-2.5-flash-lite",
    google_api_key=GOOGLE_API_KEY,
    temperature=0
)

# Modelo de embeddings do Gemini
embeddings = GoogleGenerativeAIEmbeddings(
    model="models/gemini-embedding-001",
    google_api_key=GOOGLE_API_KEY
)

# Variável global para o contexto do RAG
retriever = None

# Variável global para memória do agente
memory = MemorySaver()

# Criando agente
agent = create_agent(
    model=llm,
    tools=[],
    system_prompt=(
        "Você é um tutor técnico de álgebra linear e vetorial, direto e prático.\n"
        "\n"
        "Você pode utilizar o contexto fornecido (quando houver) para responder perguntas.\n"
        "Você também possui memória da conversa atual e pode usar informações anteriores do usuário.\n"
        "\n"
        "REGRAS PRINCIPAIS:\n"
        "- Use SOMENTE o contexto fornecido quando ele estiver presente\n"
        "- Se a resposta não estiver no contexto, diga claramente: 'Não encontrei essa informação.'\n"
        "- Seja claro, objetivo e bem estruturado\n"
        "- Evite respostas genéricas ou longas sem necessidade\n"
        "\n"
        "TRATAMENTO DE ENTRADAS:\n"
        "- Se a pergunta estiver incompleta, ambígua ou sem informação suficiente, NÃO tente adivinhar\n"
        "- Em vez disso, peça esclarecimentos objetivos ao usuário\n"
        "- Sempre indique exatamente o que está faltando para conseguir responder\n"
        "- Se possível, sugira como o usuário pode reformular a pergunta\n"
        "\n"
        "MEMÓRIA:\n"
        "- Considere o histórico da conversa nesta mesma sessão (thread_id)\n"
        "- Use preferências e contexto já fornecidos pelo usuário quando relevante\n"
    ),
    checkpointer=memory
)



# CRIAÇÃO DO RAG
def build_rag():
    print("Construindo RAG...")

    global retriever

    loader = PyPDFLoader(PDF_PATH)
    docs = loader.load()

    splitter = RecursiveCharacterTextSplitter(
        chunk_size=1000,
        chunk_overlap=200
    )

    chunks = splitter.split_documents(docs)

    vectorstore = Chroma.from_documents(
        documents=chunks,
        embedding=embeddings,
        collection_name="pdf_rag"
    )

    # Usado para ser um recuperador de contexto , ele busca os trechos mais relevantes do rag
    retriever = vectorstore.as_retriever(
        search_kwargs={"k": 4}
    )

    print("RAG construído com sucesso!")





# ENDPOINT DO APP
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

    prompt = f"""
        Você é um tutor de álgebra linear e vetorial.

        Regras:
        - Use SOMENTE o contexto fornecido
        - Responda de forma clara e organizada
        - Se a pergunta estiver incompleta, peça esclarecimentos
        - Se a resposta não estiver no contexto, diga:
        "Não encontrei essa informação no PDF."

        CONTEXTO:
        {context}

        Pergunta:
        {request.message}
    """

    # Configuração da sessão (memória)
    config = {
        "configurable": {
            "thread_id": "usuario-1"
        }
    }

    # Chamada do agente
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

    # Retorna resposta
    return {
        "response": response["messages"][-1].content
    }



# CONFIGURAÇÕES DE REDE E MDNS
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

    # Constrói o RAG antes de inicializar o servidor
    try:
        build_rag()
    except Exception as e:
        print(f"Erro ao construir RAG: {e}")
        exit(1)

    # Inicializa o mDNS para descoberta na rede local
    try:
        zc = start_mdns()
    except Exception as e:
        print(f"Erro ao iniciar mDNS: {e}")
        exit(1)

    # Inicializa o servidor FastAPI e encerra o mDNS corretamente ao finalizar o processo
    try:
        uvicorn.run(
            app,
            host="0.0.0.0",
            port=8000
        )
    finally:
        if zc:
            zc.close()