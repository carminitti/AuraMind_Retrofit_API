package com.app.auramind.util

data class EmotionPack(
    val emotionPt: String,
    val audio: List<String>,
    val video: List<String>
)

object EmotionMapper {
    private val happyAudio = listOf(
        "https://open.spotify.com/playlist/37i9dQZF1DXdPec7aLTmlC?utm_source=chatgpt.com",
        "https://open.spotify.com/playlist/1llkez7kiZtBeOw5UjFlJq?utm_source=chatgpt.com"
    )
    private val happyVideo = listOf(
        "https://www.youtube.com/watch?v=ZbZSe6N_BXs&utm_source=chatgpt.com",
        "https://www.youtube.com/watch?v=J4sjBD1BTq0&utm_source=chatgpt.com"
    )

    private val calmAudio = listOf(
        "https://open.spotify.com/playlist/0XE1iNb6u7DOang4zGQvCQvC",
        "https://open.spotify.com/playlist/4wP1ojJSF4zeoFKKTQAeIy?utm_source=chatgpt.com"
    )
    private val calmVideo = listOf(
        "https://www.youtube.com/watch?v=hlWiI4xVXKY&utm_source=chatgpt.com",
        "https://www.youtube.com/watch?v=lFcSrYw-ARY&utm_source=chatgpt.com"
    )

    private val cheerAudio = listOf(
        "https://open.spotify.com/playlist/4Hp0GzwtzMsLXITEwU9dhv?utm_source=chatgpt.com",
        "https://open.spotify.com/playlist/37i9dQZF1EIcqv6dNT3Dgk?utm_source=chatgpt.com"
    )
    private val cheerVideo = listOf(
        "https://www.youtube.com/playlist?list=PLWCRiy-hIKsJYh4BAKhEsvWcNIuBFEFZ4&utm_source=chatgpt.com",
        "https://www.youtube.com/watch?v=uE-TADy-oN0&utm_source=chatgpt.com"
    )

    fun map(en: String?): EmotionPack {
        val e = (en ?: "").lowercase()
        return when {
            "sad" in e || "depress" in e -> EmotionPack("Triste", happyAudio, happyVideo)
            "angry" in e || "rage" in e || "mad" in e -> EmotionPack("Raiva", calmAudio, calmVideo)
            "happy" in e || "joy" in e || "excited" in e -> EmotionPack("Feliz", cheerAudio, cheerVideo)
            "neutral" in e -> EmotionPack("Neutro", calmAudio, calmVideo)
            else -> EmotionPack("Indefinido", calmAudio, calmVideo)
        }
    }
}
