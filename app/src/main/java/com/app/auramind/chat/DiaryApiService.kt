package com.app.auramind.chat

import retrofit2.http.Body
import retrofit2.http.POST

data class DiaryReq(val message: String)
data class DiaryRes(val aiReply: String)

interface DiaryApiService {
    @POST("api/diary/message")
    suspend fun sendDiaryMessage(@Body body: DiaryReq): DiaryRes
}
