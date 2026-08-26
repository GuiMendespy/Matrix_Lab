import os
from dotenv import load_dotenv

load_dotenv()

QUESTION_BANK_PATH = os.getenv(
    "QUESTION_BANK_PATH",
    "rag/question_bank"
)

GENERATED_QUESTIONS_PATH = os.getenv(
    "GENERATED_QUESTIONS_PATH",
    "rag/Questoes_geradas"
)

THEORY_BANK_PATH = os.getenv(
    "THEORY_BANK_PATH",
    "rag/theory_bank"
)