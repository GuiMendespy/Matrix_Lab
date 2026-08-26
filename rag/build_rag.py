from langchain_community.document_loaders import TextLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_chroma import Chroma
from langchain_core.documents import Document

import os

from core.llm import embeddings
from config.settings import QUESTION_BANK_PATH, GENERATED_QUESTIONS_PATH, THEORY_BANK_PATH

# Adicionamos o vectorstore globalmente aqui para podermos manipular depois
question_vectorstore = None
question_retriever = None
theory_retriever = None

# Guarda os documentos originais para busca por palavra-chave
question_docs_raw = []


def build_question_rag():
    global question_retriever, question_vectorstore, question_docs_raw

    print("Construindo RAG de questões...")
    docs = []

    caminhos = [QUESTION_BANK_PATH, GENERATED_QUESTIONS_PATH]

    for caminho in caminhos:
        if not os.path.exists(caminho):
            os.makedirs(caminho, exist_ok=True)

        for file_name in os.listdir(caminho):
            if file_name.endswith(".txt"):
                file_path = os.path.join(caminho, file_name)
                loader = TextLoader(file_path, encoding="utf-8")
                loaded_docs = loader.load()

                for doc in loaded_docs:
                    doc.metadata["source"] = file_name
                docs.extend(loaded_docs)

    # Guarda cópia bruta para busca por palavra-chave
    question_docs_raw = docs

    # Se as pastas estiverem vazias no primeiro início, cria um RAG vazio ou com aviso
    if not docs:
        print("Aviso: Banco de questões vazio. Inicializando RAG limpo.")
        question_vectorstore = Chroma(
            collection_name="question_rag",
            embedding_function=embeddings,
            persist_directory="./chroma/chroma_questions"
        )
    else:
        splitter = RecursiveCharacterTextSplitter(chunk_size=3000, chunk_overlap=0)
        chunks = splitter.split_documents(docs)

        question_vectorstore = Chroma.from_documents(
            documents=chunks,
            embedding=embeddings,
            collection_name="question_rag",
            persist_directory="./chroma/chroma_questions"
        )

    question_retriever = question_vectorstore.as_retriever(
        search_type="similarity",
        search_kwargs={"k": 1}
    )
    print("RAG de questões construído com sucesso!")


def buscar_por_palavra_chave(query: str) -> str | None:
    """
    Busca por palavra-chave nos documentos originais (não fragmentados),
    pontuando por relevância (frequência das palavras-chave).
    """
    STOPWORDS = {
        "me", "dê", "de", "uma", "questão", "sobre", "exercício", "exercicio",
        "problema", "quero", "gostaria", "por", "favor", "para", "com"
    }
    palavras = [p.lower() for p in query.split() if p.lower() not in STOPWORDS and len(p) > 2]

    if not palavras:
        return None

    melhor_doc = None
    melhor_score = 0

    for doc in question_docs_raw:
        conteudo_lower = doc.page_content.lower()

        # Só considera documentos que contenham TODAS as palavras-chave
        if not all(p in conteudo_lower for p in palavras):
            continue

        # Pontua pela frequência total das palavras-chave no documento
        score = sum(conteudo_lower.count(p) for p in palavras)

        # NOVO: dá peso extra se a palavra aparecer no campo SUBTOPICO/TOPICO (mais relevante que aparecer só na resolução)
        cabecalho = conteudo_lower.split("enunciado:")[0] if "enunciado:" in conteudo_lower else ""
        score += sum(cabecalho.count(p) * 5 for p in palavras)

        if score > melhor_score:
            melhor_score = score
            melhor_doc = doc

    if melhor_doc:
        return f"[Fonte: {melhor_doc.metadata.get('source', 'desconhecida')}]\n{melhor_doc.page_content}"

    return None

def add_new_question_to_rag(file_name: str, content: str):
    """
    Função para adicionar dinamicamente uma nova questão ao banco vetorial
    sem reiniciar a aplicação.
    """
    global question_vectorstore, question_retriever, question_docs_raw

    if question_vectorstore is None:
        print("Erro: RAG de questões não foi inicializado ainda.")
        return

    print(f"Adicionando nova questão '{file_name}' ao RAG dinamicamente...")

    # Cria o documento LangChain com os metadados corretos
    new_doc = Document(
        page_content=content,
        metadata={"source": file_name}
    )

    # Atualiza a cópia bruta usada na busca por palavra-chave
    question_docs_raw.append(new_doc)

    # Divide em chunks igualzinho ao build inicial
    splitter = RecursiveCharacterTextSplitter(chunk_size=3000, chunk_overlap=0)
    chunks = splitter.split_documents([new_doc])

    # Adiciona diretamente ao Chroma existente
    question_vectorstore.add_documents(chunks)
    print("Nova questão indexada com sucesso no RAG!")


def build_theory_rag():
    global theory_retriever

    print("Construindo RAG de teoria...")
    docs = []

    if not os.path.exists(THEORY_BANK_PATH):
        os.makedirs(THEORY_BANK_PATH, exist_ok=True)

    for file_name in os.listdir(THEORY_BANK_PATH):
        if file_name.endswith(".txt"):
            file_path = os.path.join(THEORY_BANK_PATH, file_name)
            loader = TextLoader(file_path, encoding="utf-8")
            loaded_docs = loader.load()

            for doc in loaded_docs:
                doc.metadata["source"] = file_name
            docs.extend(loaded_docs)

    if not docs:
        print("Aviso: Banco de teoria vazio.")
        vectorstore = Chroma(
            collection_name="theory_rag",
            embedding_function=embeddings,
            persist_directory="./chroma/chroma_theory"
        )
    else:
        splitter = RecursiveCharacterTextSplitter(chunk_size=300, chunk_overlap=50)
        chunks = splitter.split_documents(docs)

        vectorstore = Chroma.from_documents(
            documents=chunks,
            embedding=embeddings,
            collection_name="theory_rag",
            persist_directory="./chroma/chroma_theory"
        )

    theory_retriever = vectorstore.as_retriever(
        search_type="mmr",
        search_kwargs={"k": 4}
    )
    print("RAG de teoria construído com sucesso!")