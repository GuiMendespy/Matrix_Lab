import os

# Definicão das chaves de API
OPENROUTER_API_KEY = "CHAVE AQUI"

# Definição dos caminhos para os arquivos
QUESTION_BANK_PATH = os.getenv("QUESTION_BANK_PATH", "rag/question_bank")
THEORY_BANK_PATH = os.getenv("THEORY_BANK_PATH", "rag/theory_bank")