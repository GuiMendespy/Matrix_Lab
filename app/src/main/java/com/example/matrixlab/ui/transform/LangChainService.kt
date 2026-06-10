package com.example.matrixlab.ui.transform

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

data class ChatRequest(
    val message: String,
    @SerializedName("session_id") val sessionId: String? = null
)

data class ChatResponse(
    val response: String,
    @SerializedName("session_id") val sessionId: String
)

interface LangChainService {
    @POST("chat")
    suspend fun enviarPergunta(@Body request: ChatRequest): ChatResponse
}