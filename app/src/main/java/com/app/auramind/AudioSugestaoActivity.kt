package com.app.auramind

import android.content.Intent
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

class AudioSugestaoActivity : ComponentActivity() {

    companion object {
        const val EXTRA_SPOTIFY_URL_1 = "extra_spotify_url_1"
        const val EXTRA_SPOTIFY_URL_2 = "extra_spotify_url_2"
        const val EXTRA_SPOTIFY_URL_3 = "extra_spotify_url_3"

        // Defaults: troque para os álbuns/faixas/playlist que você quiser
        private const val DEFAULT_1 = "https://open.spotify.com/track/7ouMYWpwJ422jRcDASZB7P"
        private const val DEFAULT_2 = "https://open.spotify.com/album/1ATL5GLyefJaxhQzSPVrLX"
        private const val DEFAULT_3 = "https://open.spotify.com/playlist/37i9dQZF1DXcBWIGoYBM5M"
    }

    private lateinit var btnVoltarRow: LinearLayout
    private lateinit var ivVoltar: ImageView
    private lateinit var tvVoltar: TextView
    private lateinit var web1: WebView
    private lateinit var web2: WebView
    private lateinit var web3: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_sugestao)

        btnVoltarRow = findViewById(R.id.btnVoltarRow)
        ivVoltar     = findViewById(R.id.ivVoltar)
        tvVoltar     = findViewById(R.id.tvVoltar)
        web1         = findViewById(R.id.webAudio1)
        web2         = findViewById(R.id.webAudio2)
        web3         = findViewById(R.id.webAudio3)

        btnVoltarRow.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        val url1 = intent.getStringExtra(EXTRA_SPOTIFY_URL_1)?.trim().takeUnless { it.isNullOrBlank() } ?: DEFAULT_1
        val url2 = intent.getStringExtra(EXTRA_SPOTIFY_URL_2)?.trim().takeUnless { it.isNullOrBlank() } ?: DEFAULT_2
        val url3 = intent.getStringExtra(EXTRA_SPOTIFY_URL_3)?.trim().takeUnless { it.isNullOrBlank() } ?: DEFAULT_3

        setupWebView(web1, toEmbedHtml(url1))
        setupWebView(web2, toEmbedHtml(url2))
        setupWebView(web3, toEmbedHtml(url3))
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
        // baseURL no domínio do Spotify ajuda a evitar bloqueios de origem
        web.loadDataWithBaseURL(
            "https://open.spotify.com", html, "text/html", "utf-8", null
        )
    }

    /** Gera HTML com iframe embed do Spotify a partir de diversas formas de link */
    private fun toEmbedHtml(url: String): String {
        val embedUrl = toSpotifyEmbedUrl(url)
        // Altura: o embed do Spotify costuma ter ~152px (track); aqui usamos responsivo simples.
        return """
            <!DOCTYPE html>
            <html>
              <head>
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <style>
                  html,body { margin:0; background:#86A6A3; }
                  .wrap { position:relative; width:100%; height:0; padding-bottom:40%; } /* ~proporção para 200dp visível */
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
        // Se já for embed, retorna
        if (urlOrUri.contains("/embed/")) return urlOrUri

        // spotify:track:ID  / spotify:album:ID / spotify:playlist:ID
        val uriRegex = Regex("""^spotify:(track|album|playlist):([A-Za-z0-9]+)$""")
        uriRegex.find(urlOrUri)?.let {
            val type = it.groupValues[1]
            val id = it.groupValues[2]
            return "https://open.spotify.com/embed/$type/$id"
        }

        // https://open.spotify.com/track/ID
        val webRegex = Regex("""https?://open\.spotify\.com/(track|album|playlist)/([A-Za-z0-9]+)""")
        webRegex.find(urlOrUri)?.let {
            val type = it.groupValues[1]
            val id = it.groupValues[2]
            return "https://open.spotify.com/embed/$type/$id"
        }

        // Fallback: se for artista/página que não tem embed direto, tenta abrir como é
        return urlOrUri
    }

    override fun onPause() {
        super.onPause()
        web1.onPause(); web2.onPause(); web3.onPause()
    }
    override fun onResume() {
        super.onResume()
        web1.onResume(); web2.onResume(); web3.onResume()
    }
    override fun onDestroy() {
        super.onDestroy()
        listOf(web1, web2, web3).forEach { w ->
            (w.parent as? android.view.ViewGroup)?.removeView(w)
            w.removeAllViews()
            w.destroy()
        }
    }
}
