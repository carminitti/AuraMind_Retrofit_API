package com.app.auramind.chat

import android.content.Context
import com.app.auramind.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Interceptor para adicionar automaticamente o token JWT
private class TokenInterceptor(private val ctx: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain) = chain.proceed(
        chain.request().newBuilder().apply {
            val t = ctx.getSharedPreferences("auth", Context.MODE_PRIVATE)
                .getString("jwt", null)
            if (!t.isNullOrBlank()) {
                addHeader("Authorization", "Bearer $t")
            }
        }.build()
    )
}

object ChatRetrofit {

    // Cliente Retrofit para a API Java (Auth + Diary)
    fun build(ctx: Context, requireAuth: Boolean = true): Retrofit {
        val prefs = ctx.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = prefs.getString("jwt", null)

        // Só exige JWT se requireAuth = true
        if (requireAuth && token.isNullOrBlank()) {
            throw IllegalStateException("Usuário não logado — JWT ausente.")
        }

        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(ctx)) // ainda adiciona header se tiver token
            .addInterceptor(logger)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_CORE) // já está com a barra no final
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}


