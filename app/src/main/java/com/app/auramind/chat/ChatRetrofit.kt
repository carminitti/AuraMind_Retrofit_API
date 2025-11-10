package com.app.auramind.chat

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ChatRetrofit {
    // ⬇️ ESCOLHA CERTO:
    // Emulador Android Studio:  use "http://10.0.2.2:8080/"
    // Dispositivo físico:       use "http://192.168.0.179:8080/" (ou o IP real do PC)
    private const val BASE_URL =  "https://auramind-api-v2.onrender.com/"
"   // ajuste aqui

    private val client by lazy {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.SECONDS)
            .writeTimeout(40, TimeUnit.SECONDS)
            .addInterceptor(logger)
            .build()
    }

    val api: ChatApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // precisa terminar com "/"
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ChatApiService::class.java)
    }
}
