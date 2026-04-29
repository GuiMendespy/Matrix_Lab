from fastapi import FastAPI
from pydantic import BaseModel
from langchain_google_genai import ChatGoogleGenerativeAI
import os

app = FastAPI()

llm = ChatGoogleGenerativeAI(
    model="gemini-2.5-flash", 
    google_api_key="AIzaSyBuiGc-vv6v4SHWtNwT4MVHJCqPM8Be52U",
    temperature=0
)

class ChatRequest(BaseModel):
    message: str

@app.post("/chat")
async def chat_endpoint(request: ChatRequest):
    response = llm.invoke(request.message)
    return {"response": response.content}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
