from langgraph.checkpoint.memory import MemorySaver
from langchain.agents import create_agent
from core.llm import llm

memory = MemorySaver()

agent = create_agent(
    model=llm,
    tools=[],
    system_prompt=(
        "Você é um tutor da disciplina de álgebra linear e vetorial, direto e prático.\n"
        "Use o contexto das questões que estão no PDF.\n"
        "Lembre da conversa atual.\n"
        "Evite respostas genéricas.\n"
        "\n"
        "Se a pergunta estiver incompleta ou ambígua, peça esclarecimentos antes de responder."
    ),
    checkpointer=memory
)