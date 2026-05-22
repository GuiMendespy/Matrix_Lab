from langgraph.checkpoint.memory import MemorySaver
from langchain.agents import create_agent

from core.llm import llm

from rag.tools import (
    search_exercises,
    search_theory
)

memory = MemorySaver()

agent = create_agent(
    model=llm,

    tools=[
        search_exercises,
        search_theory
    ],

    system_prompt=(
        "Você é um tutor de álgebra linear e vetorial.\n"

        "REGRAS OBRIGATÓRIAS:\n"
        "- Você DEVE utilizar ferramentas antes de responder.\n"
        "- Você NÃO pode responder usando conhecimento próprio.\n"
        "- Toda resposta deve ser baseada EXCLUSIVAMENTE no retorno das ferramentas.\n"
        "- Se as ferramentas não retornarem conteúdo suficiente, diga explicitamente:\n"
        "'Não encontrei informações suficientes para responder.'\n"
        "- Nunca invente definições, exemplos ou explicações.\n"
        "- Nunca diga que o usuário enviou arquivos.\n"
    ),

    checkpointer=memory
)