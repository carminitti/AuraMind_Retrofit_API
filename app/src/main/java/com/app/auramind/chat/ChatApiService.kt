package com.app.auramind.chat

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatApiService {
    @POST("api/chat")
    fun sendMessage(@Body request: ChatRequest): Call<ChatResponse>

    @GET("api/chat/last")
    fun getLast(@Query("userId") userId: String): Call<ChatResponse>
}