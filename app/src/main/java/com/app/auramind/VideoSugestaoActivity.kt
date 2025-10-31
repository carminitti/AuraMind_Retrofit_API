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
import android.widget.Toast
import androidx.activity.ComponentActivity

class VideoSugestaoActivity : ComponentActivity() {

    companion object {
        // 🔑 CHAVES dos extras (não são URLs!)
        const val EXTRA_VIDEO_URL_1 = "extra_video_url_1"
        const val EXTRA_VIDEO_URL_2 = "extra_video_url_2"
        const val EXTRA_VIDEO_URL_3 = "extra_video_url_3"

        // Fallbacks (se nada vier via Intent, usa estes)
        private const val DEFAULT_URL_1 = "https://www.youtube.com/watch?v=bhrxz6kq7qA"
        private const val DEFAULT_URL_2 = "https://www.youtube.com/watch?v=WSLMTSxARbg"
        private const val DEFAULT_URL_3 = "https://www.youtube.com/watch?v=hO_tjm9i32g"
    }

    private lateinit var btnVoltarRow: LinearLayout
    private lateinit var ivVoltar: ImageView
    private lateinit var tvVoltar: TextView
    private lateinit var web1: WebView
    private lateinit var web2: WebView
    private lateinit var web3: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_sugestao)

        btnVoltarRow = findViewById(R.id.btnVoltarRow)
        ivVoltar     = findViewById(R.id.ivVoltar)
        tvVoltar     = findViewById(R.id.tvVoltar)
        web1         = findViewById(R.id.webVideo1)
        web2         = findViewById(R.id.webVideo2)
        web3         = findViewById(R.id.webVideo3)

        // Voltar: DashboardActivity
        btnVoltarRow.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        // Lê URLs dos extras; se vier vazio, usa os defaults
        val url1 = (intent.getStringExtra(EXTRA_VIDEO_URL_1)?.trim()).takeUnless { it.isNullOrBlank() } ?: DEFAULT_URL_1
        val url2 = (intent.getStringExtra(EXTRA_VIDEO_URL_2)?.trim()).takeUnless { it.isNullOrBlank() } ?: DEFAULT_URL_2
        val url3 = (intent.getStringExtra(EXTRA_VIDEO_URL_3)?.trim()).takeUnless { it.isNullOrBlank() } ?: DEFAULT_URL_3

        // Configura e carrega
        setupWebView(web1, toEmbedHtml(url1))
        setupWebView(web2, toEmbedHtml(url2))
        setupWebView(web3, toEmbedHtml(url3))
    }

    private fun setupWebView(web: WebView, html: String) {
        val ws = web.settings
        ws.javaScriptEnabled = true
        ws.domStorageEnabled = true
        ws.mediaPlaybackRequiresUserGesture = true
        ws.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url ?: return false
                return if (url.startsWith("http")) {
                    // Abre links externos no navegador do sistema (opcional)
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    true
                } else false
            }
        }
        web.webChromeClient = WebChromeClient()

        web.loadDataWithBaseURL(
            "https://www.youtube.com", html, "text/html", "utf-8", null
        )
    }

    /** Gera HTML com iframe embed a partir de watch?v=... / youtu.be/... */
    private fun toEmbedHtml(url: String): String {
        val embedUrl = toEmbedUrl(url)
        return """
            <!DOCTYPE html>
            <html>
              <head>
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <style>
                  body { margin:0; background:#86A6A3; }
                  .wrap { position:relative; width:100%; height:0; padding-bottom:56.25%; }
                  .wrap iframe {
                    position:absolute; top:0; left:0; width:100%; height:100%;
                    border:0;
                  }
                </style>
              </head>
              <body>
                <div class="wrap">
                  <iframe
                    src="$embedUrl"
                    title="YouTube video player"
                    frameborder="0"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                    allowfullscreen>
                  </iframe>
                </div>
              </body>
            </html>
        """.trimIndent()
    }

    private fun toEmbedUrl(url: String): String {
        if (url.contains("/embed/")) return url

        val youtuBe = Regex("""https?://youtu\.be/([A-Za-z0-9_\-]+)""")
        youtuBe.find(url)?.let { return "https://www.youtube.com/embed/${it.groupValues[1]}" }

        val watch = Regex("""https?://(www\.)?youtube\.com/watch\?v=([A-Za-z0-9_\-]+)""")
        watch.find(url)?.let { return "https://www.youtube.com/embed/${it.groupValues[2]}" }

        return url // fallback
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
