package com.app.auramind.chat

import android.content.Context
import com.app.auramind.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

<<<<<<< HEAD
private class TokenInterceptor(private val ctx: Context): Interceptor {
    override fun intercept(chain: Interceptor.Chain) = chain.proceed(
        chain.request().newBuilder().apply {
            val t = ctx.getSharedPreferences("auth", Context.MODE_PRIVATE).getString("jwt", null)
            if (!t.isNullOrBlank()) addHeader("Authorization", "Bearer $t")
        }.build()
    )
}
=======
object ChatRetrofit {
    // ⬇️ ESCOLHA CERTO:
    // Emulador Android Studio:  use "http://10.0.2.2:8080/"
    // Dispositivo físico:       use "http://192.168.0.179:8080/" (ou o IP real do PC)
    private const val BASE_URL =  "https://auramind-api-v2.onrender.com/"
"   // ajuste aqui
>>>>>>> e9d6a8bb07f57b42a3403d3410e1ed3c8284d350

object ChatRetrofit {
    // Java Core API (auth/diary)
    fun build(ctx: Context): Retrofit {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .readTimeout(40, TimeUnit.SECONDS)
            .writeTimeout(40, TimeUnit.SECONDS)
            .addInterceptor(TokenInterceptor(ctx))
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_CORE)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}
