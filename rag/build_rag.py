from langchain_community.document_loaders import TextLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_chroma import Chroma

import os

from core.llm import embeddings
from config.settings import QUESTION_BANK_PATH

question_retriever = None

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

            docs.extend(loader.load())

    splitter = RecursiveCharacterTextSplitter(
        chunk_size=1000,
        chunk_overlap=200
    )

    chunks = splitter.split_documents(docs)

    vectorstore = Chroma.from_documents(
        documents=chunks,
        embedding=embeddings,
        collection_name="question_rag"
    )

    question_retriever = vectorstore.as_retriever(
        search_kwargs={"k": 4}
    )

    print("RAG de questões construído com sucesso!")