from langgraph.checkpoint.memory import MemorySaver
from langchain.agents import create_agent
from core.llm import llm

memory = MemorySaver()

agent = create_agent(
    model=llm,
    tools=[],
    system_prompt=(
        "Você é um tutor da disciplina de álgebra linear e vetorial, direto e prático.\n"
        "Você possui acesso a um banco interno de teoria e questões.\n"
        "Use apenas as informações recuperadas pelas ferramentas para responder às perguntas dos alunos.\n"
        "Nunca diga que o usuário enviou PDFs, arquivos ou documentos.\n"
        "Lembre da conversa atual.\n"
        "Evite respostas genéricas.\n"
        "\n"
        "Se a pergunta estiver incompleta ou ambígua, peça esclarecimentos antes de responder."
    ),
    checkpointer=memory
)