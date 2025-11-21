package com.app.auramind.chat

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatApiService {
    data class MessageDTO(
        val role: String,
        val content: String
    )

    data class ChatRequest(
        val userId: String,
        val message: String,
        val history: List<MessageDTO> = emptyList(),
        val profileContext: String = ""
    )

    data class ChatResponse(
        val userId: String,
        val message: String,
        val botReply: String
    )

    interface ChatApiService {
        @POST("chat")
        suspend fun enviarMensagem(@Body request: ChatRequest): ChatResponse
    }
}