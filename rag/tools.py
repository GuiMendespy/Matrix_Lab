from langchain.tools import tool

import rag.build_rag as build_rag

@tool
def search_questions(query: str):
    """
    Busca questões relacionadas ao tema informado.
    """

    if build_rag.question_retriever is None:
        return "RAG de questões não inicializado."

    docs = build_rag.question_retriever.invoke(query)

    if not docs:
        return "Nenhuma questão encontrada."

    context = "\n\n".join([
        f"[Página {doc.metadata.get('page', 0) + 1}] {doc.page_content}"
        for doc in docs
    ])

    return context