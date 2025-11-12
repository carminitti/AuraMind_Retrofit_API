package com.app.auramind.util

data class EmotionPack(
    val emotionPt: String,
    val audio: List<String>,
    val video: List<String>
)

object EmotionMapper {
    private val happyAudio = listOf(
        "https://open.spotify.com/playlist/37i9dQZF1DXdPec7aLTmlC",
        "https://music.youtube.com/playlist?list=PLrW43fNmjaQWdeIP9oVGXKQ8Uu3FZ7yZ9"
    )
    private val happyVideo = listOf(
        "https://www.youtube.com/watch?v=d-diB65scQU",
        "https://www.youtube.com/watch?v=ru0K8uYEZWw"
    )

    private val calmAudio = listOf(
        "https://open.spotify.com/playlist/37i9dQZF1DWU0ScTcjJBdj",
        "https://music.youtube.com/playlist?list=PLSdoVPM0OwZb5_0G2ZrLNPm9XssA2-2iu"
    )
    private val calmVideo = listOf(
        "https://www.youtube.com/watch?v=2OEL4P1Rz04",
        "https://www.youtube.com/watch?v=1ZYbU82GVz4"
    )

    private val cheerAudio = listOf(
        "https://open.spotify.com/playlist/37i9dQZF1DX0kbJZpiYdZl",
        "https://music.youtube.com/playlist?list=PL6bPxvf5dW5cRH8Jy8zGpl2g3rjvSqG6K"
    )
    private val cheerVideo = listOf(
        "https://www.youtube.com/watch?v=HgzGwKwLmgM",
        "https://www.youtube.com/watch?v=Zi_XLOBDo_Y"
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
