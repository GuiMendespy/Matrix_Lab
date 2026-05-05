package com.example.matrixlab.ui.transform

import retrofit2.http.Body
import retrofit2.http.POST

// Estas classes de dados (Data Classes) representam o JSON que vai e volta
data class ChatRequest(val message: String)
data class ChatResponse(val response: String)

interface LangChainService {
    @POST("chat")
    suspend fun enviarPergunta(@Body request: ChatRequest): ChatResponse
}