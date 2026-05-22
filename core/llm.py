from langchain_openai import ChatOpenAI
from langchain_huggingface import HuggingFaceEmbeddings

from config.settings import OPENROUTER_API_KEY

llm = ChatOpenAI(
    model="openai/gpt-oss-120b:free",
    base_url="https://openrouter.ai/api/v1",
    api_key=OPENROUTER_API_KEY,
    temperature=0,
    request_timeout=60
)

embeddings = HuggingFaceEmbeddings(
    model_name="sentence-transformers/all-MiniLM-L6-v2"
)