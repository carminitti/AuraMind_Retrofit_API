package com.app.auramind.network

data class ApiResponse(
    val emocao_detectada: String? = null,
    val ultima_emocao: String? = null,
    val mensagem: String? = null
)