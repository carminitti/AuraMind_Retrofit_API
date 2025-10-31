package com.app.auramind.chat

data class ChatRequest(
    val userId: String,
    val message: String
)

data class ChatResponse(
    val userId: String,
    val message: String,
    val botReply: String?,
    val timestamp: String?
)