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

// ── ADICIONADO: modelos do agente gerador ───────────────────
data class GenerateRequest(
    val message: String,
    @SerializedName("thread_id") val threadId: String = "generator-default"
)

data class GenerateResponse(
    val response: String,
    val status: String
)

// ── interface ────────────────────────────────────────────────
interface LangChainService {

    // já existia — agente de consulta
    @POST("chat")
    suspend fun enviarPergunta(@Body request: ChatRequest): ChatResponse

    // ADICIONADO — agente gerador
    @POST("generate")
    suspend fun gerarNovaQuestao(@Body request: GenerateRequest): GenerateResponse
}
