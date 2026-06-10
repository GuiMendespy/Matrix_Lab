from langchain_community.document_loaders import TextLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_chroma import Chroma

import os

from core.llm import embeddings
from config.settings import QUESTION_BANK_PATH, THEORY_BANK_PATH

question_retriever = None
theory_retriever = None

def build_question_rag():
    global question_retriever

    print("Construindo RAG de questões...")

    docs = []

    for file_name in os.listdir(QUESTION_BANK_PATH):

        if file_name.endswith(".txt"):

            file_path = os.path.join(
                QUESTION_BANK_PATH,
                file_name
            )

            loader = TextLoader(
                file_path,
                encoding="utf-8"
            )

            loaded_docs = loader.load()

            for doc in loaded_docs:
                doc.metadata["source"] = file_name

            docs.extend(loaded_docs)

    splitter = RecursiveCharacterTextSplitter(
        chunk_size=1000,
        chunk_overlap=150
    )

    chunks = splitter.split_documents(docs)

    vectorstore = Chroma.from_documents(
        documents=chunks,
        embedding=embeddings,
        collection_name="question_rag",
        persist_directory="./chroma/chroma_questions"
    )

    question_retriever = vectorstore.as_retriever(
        search_type="mmr",
        search_kwargs={"k": 4}
    )

    print("RAG de questões construído com sucesso!")


def build_theory_rag():
    global theory_retriever

    print("Construindo RAG de teoria...")

    docs = []

    for file_name in os.listdir(THEORY_BANK_PATH):

        if file_name.endswith(".txt"):

            file_path = os.path.join(
                THEORY_BANK_PATH,
                file_name
            )

            loader = TextLoader(
                file_path,
                encoding="utf-8"
            )

            loaded_docs = loader.load()

            for doc in loaded_docs:
                doc.metadata["source"] = file_name

            docs.extend(loaded_docs)

    splitter = RecursiveCharacterTextSplitter(
        chunk_size=300,
        chunk_overlap=50
    )

    chunks = splitter.split_documents(docs)

    vectorstore = Chroma.from_documents(
        documents=chunks,
        embedding=embeddings,
        collection_name="theory_rag",
        persist_directory="./chroma/chroma_theory"
    )

    theory_retriever = vectorstore.as_retriever(
        search_type="mmr",
        search_kwargs={"k": 4}
    )

    print("RAG de teoria construído com sucesso!")