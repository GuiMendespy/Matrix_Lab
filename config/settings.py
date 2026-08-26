import os

from dotenv import load_dotenv

load_dotenv()

# Definição do caminho para banco de questões
QUESTION_BANK_PATH = os.getenv(
    "QUESTION_BANK_PATH",
    "rag/Questoes_geradas"
)
# Definição do caminho para banco de teoria
THEORY_BANK_PATH = os.getenv(
    "THEORY_BANK_PATH",
    "rag/theory_bank"
)