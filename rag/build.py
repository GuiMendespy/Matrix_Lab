from langchain_community.document_loaders import PyPDFLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_chroma import Chroma
from core.llm import embeddings
from config.settings import PDF_PATH

retriever = None


def build_rag():
    global retriever

    print("Construindo RAG...")

    loader = PyPDFLoader(PDF_PATH)
    docs = loader.load()

    splitter = RecursiveCharacterTextSplitter(
        chunk_size=1000,
        chunk_overlap=200
    )

    chunks = splitter.split_documents(docs)

    vectorstore = Chroma.from_documents(
        documents=chunks,
        embedding=embeddings,
        collection_name="pdf_rag"
    )

    retriever = vectorstore.as_retriever(
        search_kwargs={"k": 4}
    )

    print("RAG construído com sucesso!")