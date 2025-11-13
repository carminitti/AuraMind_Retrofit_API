package com.app.auramind.chat

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class RegisterReq(
    val email: String,
    val password: String,
    val displayName: String
)

data class LoginReq(
    val email: String,
    val password: String
)

/**
 * Precisa bater com o AuthDTOs.AuthResponse da API Java:
 * - userId
 * - email
 * - displayName
 * - token
 */
data class AuthRes(
    val userId: Long,
    val email: String,
    val displayName: String,
    val token: String
)

data class MeRes(
    val id: Long,
    val email: String,
    val displayName: String
)

interface AuthApiService {
    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterReq): AuthRes

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginReq): AuthRes

    @GET("api/users/me")
    suspend fun me(): MeRes
}
