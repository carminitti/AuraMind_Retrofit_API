package com.app.auramind

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.app.auramind.util.EmotionMapper

class AudioSugestaoActivity : ComponentActivity() {

    companion object {
        const val EXTRA_SPOTIFY_URL_1 = "extra_spotify_url_1"
        const val EXTRA_SPOTIFY_URL_2 = "extra_spotify_url_2"

        // Defaults: troque para os álbuns/faixas/playlist que você quiser
        private const val DEFAULT_1 = "https://open.spotify.com/track/7ouMYWpwJ422jRcDASZB7P"
        private const val DEFAULT_2 = "https://open.spotify.com/album/1ATL5GLyefJaxhQzSPVrLX"
    }

    private lateinit var btnVoltarRow: LinearLayout
    private lateinit var ivVoltar: ImageView
    private lateinit var tvVoltar: TextView
    private lateinit var web1: WebView
    private lateinit var web2: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_sugestao)

        btnVoltarRow = findViewById(R.id.btnVoltarRow)
        ivVoltar     = findViewById(R.id.ivVoltar)
        tvVoltar     = findViewById(R.id.tvVoltar)
        web1         = findViewById(R.id.webAudio1)
        web2         = findViewById(R.id.webAudio2)

        btnVoltarRow.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        // 1) Lê o que veio da Intent (pode ser nulo)
        val extra1 = intent.getStringExtra(EXTRA_SPOTIFY_URL_1)?.trim()
        val extra2 = intent.getStringExtra(EXTRA_SPOTIFY_URL_2)?.trim()

        // 2) Decide a fonte: Intent -> emoção salva -> defaults
        val (url1, url2) =
            if (!extra1.isNullOrBlank() || !extra2.isNullOrBlank()) {
                // Tem pelo menos um link vindo da Intent
                val final1 = extra1?.takeIf { it.isNotBlank() } ?: DEFAULT_1
                val final2 = extra2?.takeIf { it.isNotBlank() } ?: DEFAULT_2
                final1 to final2
            } else {
                // Nada veio pela Intent -> usa EMOÇÃO salva
                val lastEmotion = getSharedPreferences("emotion", MODE_PRIVATE)
                    .getString("last_emotion_en", "undefined")

                val pack = EmotionMapper.map(lastEmotion)

                val emo1 = pack.audio.getOrNull(0)
                val emo2 = pack.audio.getOrNull(1)

                val final1 = emo1 ?: DEFAULT_1
                val final2 = emo2 ?: DEFAULT_2

                final1 to final2
            }

        // aplica tema de cor baseado na emoção
        aplicarTemaPorEmocao()

        setupWebView(web1, toEmbedHtml(url1))
        setupWebView(web2, toEmbedHtml(url2))
    }

    private fun aplicarTemaPorEmocao() {
        val lastEmotion = getSharedPreferences("emotion", MODE_PRIVATE)
            .getString("last_emotion_en", null)

        val pack = EmotionMapper.map(lastEmotion)
        val corFundo = when (pack.emotionPt) {
            "Triste" -> Color.parseColor("#455A64")
            "Feliz"  -> Color.parseColor("#FFB300")
            "Raiva"  -> Color.parseColor("#D32F2F")
            "Neutro" -> Color.parseColor("#789C9C")
            else     -> Color.parseColor("#86A6A3")
        }

        window.decorView.setBackgroundColor(corFundo)
    }

    private fun setupWebView(web: WebView, html: String) {
        with(web.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = true
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }
        web.webChromeClient = WebChromeClient()
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url ?: return false
                return if (url.startsWith("http")) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    true
                } else false
            }
        }
        web.loadDataWithBaseURL(
            "https://open.spotify.com", html, "text/html", "utf-8", null
        )
    }

    /** Gera HTML com iframe embed do Spotify a partir de diversas formas de link */
    private fun toEmbedHtml(url: String): String {
        val embedUrl = toSpotifyEmbedUrl(url)
        return """
            <!DOCTYPE html>
            <html>
              <head>
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <style>
                  html,body { margin:0; background:#86A6A3; }
                  .wrap { position:relative; width:100%; height:0; padding-bottom:40%; }
                  .wrap iframe {
                    position:absolute; top:0; left:0; width:100%; height:100%;
                    border:0; border-radius:12px;
                  }
                </style>
              </head>
              <body>
                <div class="wrap">
                  <iframe
                    src="$embedUrl"
                    allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"
                    loading="lazy">
                  </iframe>
                </div>
              </body>
            </html>
        """.trimIndent()
    }

    /** Converte link/URI do Spotify para /embed/{type}/{id} */
    private fun toSpotifyEmbedUrl(urlOrUri: String): String {
        if (urlOrUri.contains("/embed/")) return urlOrUri

        val uriRegex = Regex("""^spotify:(track|album|playlist):([A-Za-z0-9]+)$""")
        uriRegex.find(urlOrUri)?.let {
            val type = it.groupValues[1]
            val id = it.groupValues[2]
            return "https://open.spotify.com/embed/$type/$id"
        }

        val webRegex = Regex("""https?://open\.spotify\.com/(track|album|playlist)/([A-Za-z0-9]+)""")
        webRegex.find(urlOrUri)?.let {
            val type = it.groupValues[1]
            val id = it.groupValues[2]
            return "https://open.spotify.com/embed/$type/$id"
        }

        return urlOrUri
    }

    override fun onPause() {
        super.onPause()
        web1.onPause()
        web2.onPause()
    }

    override fun onResume() {
        super.onResume()
        web1.onResume()
        web2.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        listOf(web1, web2).forEach { w ->
            (w.parent as? android.view.ViewGroup)?.removeView(w)
            w.removeAllViews()
            w.destroy()
        }
    }
}