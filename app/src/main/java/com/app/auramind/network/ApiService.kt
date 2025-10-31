package com.app.auramind.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("prever/")
    fun enviarAudio(@Part file: MultipartBody.Part): Call<ApiResponse>

    @GET("ultima_emocao/")
    fun getUltimaEmocao(): Call<ApiResponse>
}