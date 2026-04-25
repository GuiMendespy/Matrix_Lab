
# Commented out IPython magic to ensure Python compatibility.
# %pip install -U langchain langchain-google-genai

from langchain_google_genai import ChatGoogleGenerativeAI
from dotenv import load_dotenv
import os
load_dotenv()
# Get the API key from environment variable (set GOOGLE_API_KEY in your environment)
api_key = os.environ.get("GOOGLE_API_KEY")
# (Optional) Ensure it's set
if not api_key:
    raise ValueError("GOOGLE_API_KEY environment variable not set")

llm = ChatGoogleGenerativeAI(
    model="gemini-2.5-flash",
    temperature=1.7
)

response = llm.invoke("Explique em uma frase o que é Agentic AI.")



print(response.content)
