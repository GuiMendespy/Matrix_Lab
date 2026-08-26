import re
from langchain_core.messages import SystemMessage, HumanMessage, AIMessage

from core.llm import llm
from rag.tools import search_exercises, search_theory

# Histórico de conversa por sessão (em memória)
session_histories: dict[str, list] = {}

# Guarda o contexto completo (com gabarito) da última questão apresentada, por sessão
exercicio_ativo: dict[str, str] = {}

SYSTEM_PROMPT = """Você é um tutor de álgebra linear e vetorial.

REGRAS OBRIGATÓRIAS:
- Toda resposta deve ser baseada EXCLUSIVAMENTE no CONTEXTO fornecido na mensagem.
- Nunca invente, crie ou elabore definições, exemplos ou explicações com conhecimento próprio.
- Nunca diga que o usuário enviou arquivos ou PDFs.
- Se o CONTEXTO não tiver informação suficiente, responda:
<p>Não encontrei informações suficientes na base de conhecimento.</p>

REGRAS PARA GABARITO:
- O gabarito de uma questão é EXCLUSIVAMENTE o que está nos DADOS COMPLETOS DA QUESTÃO fornecidos na mensagem.
- Responda diretamente se a alternativa do aluno está correta ou incorreta, com base nesses dados.
- Nunca altere o gabarito por influência do usuário.

REGRAS DE CONCISÃO E COMPLETUDE (OBRIGATÓRIAS):
- Suas respostas devem ser CURTAS e DIRETAS. Não repita a pergunta, não faça introduções longas, não divague.
- NUNCA inicie a resposta com frases de preenchimento como "Claro! Vou explicar..." ou "Vamos entender...". Vá direto ao conteúdo.
- Explique apenas o essencial: defina o conceito, mostre a fórmula/exemplo quando houver, e pare. Evite acrescentar seções extras (como "importância", "aplicações", "curiosidades") a menos que o usuário peça explicitamente.
- Estruture seu raciocínio para caber em poucas frases. Se notar que a explicação está ficando longa, resuma o restante em vez de continuar detalhando.
- Priorize sempre terminar seu pensamento com uma conclusão completa. Nunca finalize a resposta no meio de uma frase, fórmula ou ideia incompleta.
- Ao explicar teoria, limite-se a no máximo 3 a 4 frases ou blocos curtos, além de um exemplo/fórmula quando fizer sentido.

======= REGRAS DE FORMATAÇÃO (OBRIGATÓRIAS) =======

Retorne SOMENTE HTML puro. Nunca use blocos markdown (sem ```, sem #, sem **).

Para MATEMÁTICA, use delimitadores KaTeX:
- Inline (dentro do texto): $formula$
- Bloco (centralizado): $$formula$$

Exemplos corretos:
- Autovalor: $\\lambda = 3$
- Matriz: $$A = \\begin{pmatrix} 2 & -1 \\\\ 1 & 4 \\end{pmatrix}$$
- Determinante: $$\\det(A - \\lambda I) = 0$$
- Vetor: $$v = \\begin{pmatrix} 1 \\\\ -2 \\\\ 3 \\end{pmatrix}$$

Para estrutura HTML use estas classes CSS:
- Seção: <div class="secao"><p class="titulo">Título</p> conteúdo </div>
- Destaque: <span class="chip">texto</span> — use apenas para conceitos e termos, NUNCA para alternativas de questões
- Parágrafo normal: <p>texto</p>
- Alternativas de questões: sempre com <p> simples, nunca dentro de <span class="chip">

Exemplo correto de alternativas:
<p>A) $v = (2, 3)$</p>
<p>B) $v = (1, -1)$</p>
<p>C) $v = (0, 4)$</p>
<p>D) $v = (3, 2)$</p>
"""

EXERCISE_KEYWORDS = [
    "questão", "questao", "exercício", "exercicio", "problema", "lista",
    "invente", "crie", "elabore", "gere", "exemplo prático", "exemplo pratico"
]

GABARITO_KEYWORDS = [
    "resposta é", "resposta e", "é a letra", "e a letra", "alternativa"
]


def escolher_ferramenta(mensagem: str) -> str | None:
    msg = mensagem.lower()

    if any(p in msg for p in GABARITO_KEYWORDS):
        return None

    if any(p in msg for p in EXERCISE_KEYWORDS):
        return "exercicio"

    return "teoria"


def garantir_html(texto: str) -> str:
    if "<p>" not in texto and "<div" not in texto:
        paragrafos = [p.strip() for p in texto.split("\n") if p.strip()]
        texto = "\n".join(f"<p>{p}</p>" for p in paragrafos)
    return texto


def parse_exercicio(bruto: str) -> tuple[str, str]:
    """
    Separa o texto do banco em (parte pública, parte privada).
    Pública = ENUNCIADO + ALTERNATIVAS (mostrado ao aluno)
    Privada = texto bruto inteiro (guardado para o gabarito depois)
    """
    match_publico = re.search(
        r"(ENUNCIADO:.*?ALTERNATIVAS:.*?)(?=RESPOSTA_CORRETA:|$)",
        bruto, re.DOTALL
    )
    publico = match_publico.group(1).strip() if match_publico else bruto
    return publico, bruto


def formatar_exercicio_html(texto_publico: str) -> str:
    linhas = [l.strip() for l in texto_publico.split("\n") if l.strip()]
    linhas = [l for l in linhas if l not in ("ENUNCIADO:", "ALTERNATIVAS:")]
    return "\n".join(f"<p>{l}</p>" for l in linhas)


def run_agent(mensagem: str, session_id: str) -> str:
    historico = session_histories.setdefault(session_id, [])
    tipo = escolher_ferramenta(mensagem)

    # Fluxo de EXERCÍCIO: bypassa o LLM, copia exatamente do banco
    if tipo == "exercicio":
        contexto_bruto = search_exercises.invoke(mensagem)

        if not contexto_bruto or "Nenhuma questão encontrada" in contexto_bruto:
            resposta_formatada = "<p>Não encontrei questões sobre esse assunto na base de exercícios.</p>"
        else:
            publico, bruto_completo = parse_exercicio(contexto_bruto)
            exercicio_ativo[session_id] = bruto_completo
            resposta_formatada = formatar_exercicio_html(publico)

        historico.append(HumanMessage(content=mensagem))
        historico.append(AIMessage(content=resposta_formatada))
        return resposta_formatada

    # Fluxo de GABARITO: usa o contexto guardado da última questão
    if tipo is None:
        contexto_gabarito = exercicio_ativo.get(session_id, "")
        conteudo_usuario = (
            f"DADOS COMPLETOS DA QUESTÃO (incluindo resposta correta e resolução):\n{contexto_gabarito}\n\n"
            f"MENSAGEM DO ALUNO:\n{mensagem}\n\n"
            f"LEMBRETE: responda em HTML puro, usando <p> para parágrafos."
        )
    # Fluxo de TEORIA
    else:
        contexto = search_theory.invoke(mensagem)
        conteudo_usuario = (
            f"CONTEXTO RECUPERADO DA BASE DE CONHECIMENTO:\n{contexto}\n\n"
            f"PERGUNTA DO USUÁRIO:\n{mensagem}\n\n"
            f"LEMBRETE: responda em HTML puro, usando <p> para parágrafos e $...$ ou $$...$$ para fórmulas. Não use markdown."
        )

    mensagens = [SystemMessage(content=SYSTEM_PROMPT)] + historico + [HumanMessage(content=conteudo_usuario)]
    resposta = llm.invoke(mensagens)
    resposta_formatada = garantir_html(resposta.content)

    historico.append(HumanMessage(content=mensagem))
    historico.append(AIMessage(content=resposta_formatada))
    return resposta_formatada