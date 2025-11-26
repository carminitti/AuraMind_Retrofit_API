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

class VideoSugestaoActivity : ComponentActivity() {

    companion object {
        const val EXTRA_VIDEO_URL_1 = "extra_video_url_1"
        const val EXTRA_VIDEO_URL_2 = "extra_video_url_2"

        private const val DEFAULT_URL_1 = "https://www.youtube.com/watch?v=bhrxz6kq7qA"
        private const val DEFAULT_URL_2 = "https://www.youtube.com/watch?v=WSLMTSxARbg"
    }

    private lateinit var btnVoltarRow: LinearLayout
    private lateinit var ivVoltar: ImageView
    private lateinit var tvVoltar: TextView
    private lateinit var web1: WebView
    private lateinit var web2: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_sugestao)

        btnVoltarRow = findViewById(R.id.btnVoltarRow)
        ivVoltar     = findViewById(R.id.ivVoltar)
        tvVoltar     = findViewById(R.id.tvVoltar)
        web1         = findViewById(R.id.webVideo1)
        web2         = findViewById(R.id.webVideo2)

        // Voltar para PerfilActivity
        btnVoltarRow.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        // URLs vindas da Intent (podem ser nulas/vazias)
        val extra1 = intent.getStringExtra(EXTRA_VIDEO_URL_1)?.trim()
        val extra2 = intent.getStringExtra(EXTRA_VIDEO_URL_2)?.trim()

        // Decide origem das URLs: Intent -> emoção salva -> defaults
        val (url1, url2) =
            if (!extra1.isNullOrBlank() || !extra2.isNullOrBlank()) {
                val final1 = extra1?.takeIf { it.isNotBlank() } ?: DEFAULT_URL_1
                val final2 = extra2?.takeIf { it.isNotBlank() } ?: DEFAULT_URL_2
                final1 to final2
            } else {
                val lastEmotion = getSharedPreferences("emotion", MODE_PRIVATE)
                    .getString("last_emotion_en", "undefined")

                val pack = EmotionMapper.map(lastEmotion)

                val emo1 = pack.video.getOrNull(0)
                val emo2 = pack.video.getOrNull(1)

                val final1 = emo1 ?: DEFAULT_URL_1
                val final2 = emo2 ?: DEFAULT_URL_2

                final1 to final2
            }

        // Aplica paleta (tema escolhido) ou, se "auto", usa emoção
        aplicarTemaPorPaletaOuEmocao()

        // Configura WebViews
        setupWebView(web1)
        setupWebView(web2)

        // Carrega cards com thumbnail + botão play
        val html1 = buildVideoCardHtml(url1)
        val html2 = buildVideoCardHtml(url2)

        web1.loadDataWithBaseURL(
            "https://www.youtube.com",
            html1,
            "text/html",
            "utf-8",
            null
        )

        web2.loadDataWithBaseURL(
            "https://www.youtube.com",
            html2,
            "text/html",
            "utf-8",
            null
        )
    }

    /**
     * Aplica a cor de fundo com base em:
     * - tema escolhido nas Configurações (auramind_config.theme_mode), OU
     * - emoção detectada (modo "auto").
     */
    private fun aplicarTemaPorPaletaOuEmocao() {
        val prefsConfig = getSharedPreferences("auramind_config", MODE_PRIVATE)
        val themeMode = prefsConfig.getString("theme_mode", "auto") ?: "auto"

        val corFundoFixo = when (themeMode) {
            "light"   -> Color.parseColor("#FAFAFA")
            "dark"    -> Color.parseColor("#263238")
            "calm"    -> Color.parseColor("#86A6A3")
            "vibrant" -> Color.parseColor("#FF7043")
            else      -> null
        }

        if (corFundoFixo != null) {
            window.decorView.setBackgroundColor(corFundoFixo)
            return
        }

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

    /**
     * Configuração base do WebView.
     */
    private fun setupWebView(web: WebView) {
        val ws = web.settings
        ws.javaScriptEnabled = true
        ws.domStorageEnabled = true
        ws.mediaPlaybackRequiresUserGesture = true
        ws.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

        web.webChromeClient = WebChromeClient()
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url ?: return false

                // Ao clicar, abre o link no app do YouTube / navegador
                return if (url.startsWith("http")) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                    true
                } else {
                    false
                }
            }
        }
    }

    /**
     * Monta um HTML simples com:
     * - Thumbnail do YouTube
     * - Botão de play central
     * - Link do vídeo abaixo
     * - Clique no card abre o vídeo (URL original)
     */
    private fun buildVideoCardHtml(url: String): String {

        val videoId = extractVideoId(url)

        // Se não deu pra extrair ID → mostra só o link
        if (videoId == null) {
            return """
            <html>
            <body style="background:#86A6A3; padding:16px; font-family:sans-serif;">
                <a href="$url" style="color:white; font-size:16px;">$url</a>
            </body>
            </html>
        """.trimIndent()
        }

        val thumbUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

        // HTML com imagem + fallback automático caso thumbnail não carregue
        // Se der erro no carregamento → remove o card e mostra só o link
        return """
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <style>
                body {
                    margin: 0;
                    padding: 16px;
                    background-color: #86A6A3;
                    font-family: sans-serif;
                }
                .card {
                    cursor: pointer;
                }
                .thumb-wrap {
                    position: relative;
                    width: 100%;
                    overflow: hidden;
                    border-radius: 12px;
                    display: block;
                }
                .thumb {
                    width: 100%;
                    display: block;
                }
                .play-icon {
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                    font-size: 64px;
                    color: white;
                    text-shadow: 0 0 10px rgba(0,0,0,0.7);
                    pointer-events: none;
                }
                .fallback-link {
                    color:white;
                    font-size:16px;
                    display:none;
                }
            </style>
        </head>
        <body>

            <div class="card" onclick="window.location.href = '$url';">
                
                <!-- CARD PRINCIPAL -->
                <div class="thumb-wrap" id="thumbBox">
                    <img 
                        id="thumbImg"
                        class="thumb"
                        src="$thumbUrl"
                        alt="Thumbnail"
                        onerror="mostrarLink()"
                    >
                    <div class="play-icon">▶</div>
                </div>

                <!-- FALLBACK SOMENTE LINK -->
                <div id="linkBox" class="fallback-link">
                    <a href="$url" style="color:white;">$url</a>
                </div>

            </div>

            <script>
                function mostrarLink(){
                    document.getElementById('thumbBox').style.display = 'none';
                    document.getElementById('linkBox').style.display = 'block';
                }
            </script>

        </body>
        </html>
    """.trimIndent()
    }
    /**
     * Extrai o ID do vídeo a partir de formatos comuns do YouTube:
     * - https://youtu.be/VIDEOID
     * - https://www.youtube.com/watch?v=VIDEOID
     */
    private fun extractVideoId(url: String): String? {
        val shortRegex = Regex("""https?://youtu\.be/([A-Za-z0-9_\-]+)""")
        shortRegex.find(url)?.let { return it.groupValues[1] }

        val watchRegex = Regex("""https?://(www\.)?youtube\.com/watch\?v=([A-Za-z0-9_\-]+)""")
        watchRegex.find(url)?.let { return it.groupValues[2] }

        return null
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
