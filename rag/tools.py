from langchain.tools import tool

import rag.build_rag as build_rag

@tool
def search_questions(query: str):
    """
    Busca questões relacionadas ao tema informado.
    """

    print(f"\n[TOOL] search_questions: {query}\n")

    if build_rag.question_retriever is None:
        return "RAG de questões não inicializado."

    docs = build_rag.question_retriever.invoke(query)

    if not docs:
        return "Nenhuma questão encontrada."

    context = "\n\n".join([
        f"[Fonte: {doc.metadata.get('source', 'desconhecida')}]\n{doc.page_content}"
        for doc in docs
    ])

    return context


@tool
def search_theory(query: str):
    """
    Busca teorias relacionadas ao tema informado.
    """

    print(f"\n[TOOL] search_theory: {query}\n")

    if build_rag.theory_retriever is None:
        return "RAG de teoria não inicializado."

    docs = build_rag.theory_retriever.invoke(query)

    if not docs:
        return "Nenhuma teoria encontrada."

    context = "\n\n".join([
        f"[Fonte: {doc.metadata.get('source', 'desconhecida')}]\n{doc.page_content}"
        for doc in docs
    ])

    return context