from langchain_ollama import ChatOllama
from langchain_huggingface import HuggingFaceEmbeddings

llm = ChatOllama(
    model="qwen2.5:1.5b",
    temperature=0,
    base_url="http://localhost:11434",
    num_predict=300
)

embeddings = HuggingFaceEmbeddings(
    model_name="sentence-transformers/all-MiniLM-L6-v2"
)