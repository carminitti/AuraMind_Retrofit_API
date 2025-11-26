package com.app.auramind.util

data class EmotionPack(
    val emotionPt: String,
    val audio: List<String>,
    val video: List<String>
)

object EmotionMapper {
    private val happyAudio = listOf(
        "https://open.spotify.com/playlist/7egW6I9Z7iS2glkXu7QZ7t?si=ciFHbQL6QYeY-TDqyswJeQ&pi=hOsfH81hSROno&nd=1&dlsi=8cab36bce2964286",
        "https://open.spotify.com/playlist/3e8fpaIgLMr5Pb5rWYP3VJ?si=o9uJtO6WSTKiMGg2lpB9DA&pi=8LzfHbzITtaHG"
    )
    private val happyVideo = listOf(
        "https://www.youtube.com/watch?si=BexRTtNPOxfq_4y7&v=21mDekTZwsw&feature=youtu.be",
        "https://www.youtube.com/watch?si=k-_cuWRS7t0E26tf&v=hIOZ7kta5Ng&feature=youtu.be"

    )

    private val calmAudio = listOf(
        "https://open.spotify.com/playlist/3WlAfHbriUPICeO5CfACnt?si=qBQhzzsjSMuOsEcMgMv1lw&nd=1&dlsi=14f3ff868b8d4bec",
        "https://open.spotify.com/playlist/5EH1pL5Y8G2OgsfFrOOapX?si=cQp_yN1fQOeAC3ozQLLRxg&nd=1&dlsi=b8ce2698eb784a09"
    )
    private val calmVideo = listOf(
        "https://www.youtube.com/watch?v=VvrK9LR7ETc",
        "https://www.youtube.com/watch?si=sKAmCMMEfLR8o-Fg&v=N63-oDNTtic&feature=youtu.be"
    )

    private val cheerAudio = listOf(
        "https://open.spotify.com/playlist/3Ez3CAF1ehMsB6sw0srKcy?si=DfvnywcdQdKmoIp0f2Sshg&nd=1&dlsi=966bb6fa0cf2426d",
        "https://open.spotify.com/playlist/3e8fpaIgLMr5Pb5rWYP3VJ?si=_P9jkFSoQseTeO9T9TifGQ&nd=1&dlsi=1a20e5c6c02a44c3"
    )
    private val cheerVideo = listOf(
        "https://www.youtube.com/watch?si=7iXaE14EgcWKnUnb&v=iE2-SpDGazQ&feature=youtu.be",
        "https://www.youtube.com/watch?v=Y8d2AuUJ1Tc"
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
