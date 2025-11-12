package com.app.auramind.network

import com.app.auramind.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = BuildConfig.BASE_URL_AUDIO

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // precisa terminar com "/"
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}


