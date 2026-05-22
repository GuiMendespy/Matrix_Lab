from langchain.tools import tool

import rag.build_rag as build_rag

@tool
def search_exercises(query: str) -> str:
    """
    Busca exercícios, listas, problemas e questões
    de álgebra linear e vetorial.

    Use esta ferramenta SEMPRE que o usuário:
    - pedir uma questão
    - pedir exercícios
    - pedir listas
    - pedir problemas
    - pedir exemplos práticos
    """

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
def search_theory(query: str) -> str:
    """
    Busca teoria, definições e explicações
    de álgebra linear e vetorial.

    Use esta ferramenta para:
    - conceitos
    - definições
    - explicações
    """

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