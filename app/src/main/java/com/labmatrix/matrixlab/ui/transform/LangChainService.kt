package com.labmatrix.matrixlab.ui.transform

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class ChatRequest(
    val message: String,
    @SerializedName("session_id") val sessionId: String? = null
)

data class ChatResponse(
    val response: String,
    @SerializedName("session_id") val sessionId: String
)

data class HealthResponse(
    val status: String,
    val service: String? = null
)

interface LangChainService {

    @GET("health")
    suspend fun verificarHealth(): HealthResponse

    @POST("chat")
    suspend fun enviarPergunta(@Body request: ChatRequest): ChatResponse
}
