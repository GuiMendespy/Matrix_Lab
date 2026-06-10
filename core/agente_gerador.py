# core/generator_agent.py
from langgraph.prebuilt import create_react_agent
from langgraph.checkpoint.memory import MemorySaver
from core.llm import llm
from rag.tools import search_exercises, save_question

memory = MemorySaver()

generator_agent = create_react_agent(
    model=llm,
    tools=[search_exercises, save_question],
    prompt=(
        """Você é um gerador de questões de álgebra linear.

        FLUXO OBRIGATÓRIO:
        1. Use search_exercises para buscar questões modelo sobre o tema pedido
        2. Analise o padrão: estrutura, dificuldade, enunciado
        3. Gere uma questão NOVA e ORIGINAL baseada no padrão
        4. Use save_question para salvar no banco
        5. Confirme o que foi gerado

        REGRAS:
        - A nova questão deve ter dificuldade similar às encontradas
        - Nunca duplique questões existentes
        - Sempre salve antes de responder
        - Responda em HTML puro com KaTeX para fórmulas
        """
    ),
    checkpointer=memory
)