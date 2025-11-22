package com.app.auramind

import android.content.Intent
import android.os.Bundle
import android.widget.*
import android.content.Context
import android.app.AlertDialog

import com.app.auramind.chat.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.activity.ComponentActivity

class DiarioActivity : ComponentActivity() {

    companion object {
        private const val MAX_PAGES = 3
        private const val STATE_PAGES = "state_pages"
        private const val STATE_INDEX = "state_index"
    }

    private var paginas: MutableList<String> = MutableList(MAX_PAGES) { "" }
    private var paginaAtual: Int = 0

    val textoCompleto: String
        get() = paginas.filter { it.isNotBlank() }.joinToString("\n\n")

    private lateinit var etDiario: EditText
    private lateinit var btnVoltarRow: LinearLayout
    private lateinit var btnAnterior: ImageButton
    private lateinit var btnProximo: ImageButton
    private lateinit var btnEnviar: Button
    private lateinit var tvTitulo: TextView
    private lateinit var tvVoltar: TextView
    private lateinit var ivVoltar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diario)

        btnEnviar = findViewById(R.id.btnEnviar)
        etDiario = findViewById(R.id.etDiario)
        btnVoltarRow = findViewById(R.id.btnVoltarRow)
        btnAnterior = findViewById(R.id.btnAnterior)
        btnProximo = findViewById(R.id.btnProximo)
        tvTitulo = findViewById(R.id.tvTituloDiario)
        tvVoltar = findViewById(R.id.tvVoltar)
        ivVoltar = findViewById(R.id.ivVoltar)

        // Restaura estado se houver
        if (savedInstanceState != null) {
            val savedList = savedInstanceState.getStringArrayList(STATE_PAGES)
            val savedIndex = savedInstanceState.getInt(STATE_INDEX, 0)
            if (savedList != null && savedList.size == MAX_PAGES) {
                paginas = savedList.toMutableList()
            }
            paginaAtual = savedIndex.coerceIn(0, MAX_PAGES - 1)
        }

        atualizarEditText()

        // Voltar para o dashboard
        btnVoltarRow.setOnClickListener {
            salvarPaginaAtual()
            val voltarTela = Intent(this, DashBoardActivity::class.java)
            startActivity(voltarTela)
            finish()
        }

        // Página anterior
        btnAnterior.setOnClickListener {
            salvarPaginaAtual()
            if (paginaAtual > 0) {
                paginaAtual--
                atualizarEditText()
            } else {
                Toast.makeText(this, "Você já está na primeira página", Toast.LENGTH_SHORT).show()
            }
        }

        // Próxima página (até 3)
        btnProximo.setOnClickListener {
            salvarPaginaAtual()
            if (paginaAtual < MAX_PAGES - 1) {
                paginaAtual++
                atualizarEditText()
            } else {
                Toast.makeText(this, "Limite de 3 páginas atingido", Toast.LENGTH_SHORT).show()
            }
        }

        // Enviar texto para a IA
        btnEnviar.setOnClickListener {
            val texto = etDiario.text?.toString()?.trim() ?: ""
            if (texto.isEmpty()) {
                Toast.makeText(this, "Escreva algo antes de enviar.", Toast.LENGTH_SHORT).show()
            } else {
                // guarda histórico simples
                val prefs = getSharedPreferences("diario_history", Context.MODE_PRIVATE)
                val prev = prefs.getString("last_text", "")
                prefs.edit().putString("last_text", texto + "\n" + (prev ?: "")).apply()

                salvarPaginaAtual()
                enviarFluxoCompleto(texto)
            }
        }
    }

    private fun salvarPaginaAtual() {
        paginas[paginaAtual] = etDiario.text.toString()
    }

    private fun atualizarEditText() {
        etDiario.setText(paginas[paginaAtual])
        etDiario.setSelection(etDiario.text?.length ?: 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        salvarPaginaAtual()
        outState.putStringArrayList(STATE_PAGES, ArrayList(paginas))
        outState.putInt(STATE_INDEX, paginaAtual)
    }

    private fun showDialog(title: String, msg: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun enviarFluxoCompleto(texto: String) {
        try {
            // Se der erro aqui (Retrofit mal configurado, BASE_URL errada, etc.)
            // ele cai no catch logo abaixo e NÃO derruba o app.
            val diaryApi = ChatRetrofit.build(this).create(DiaryApiService::class.java)

            lifecycleScope.launch {
                try {
                    val res = diaryApi.sendDiaryMessage(DiaryReq(texto))
                    val ai = res.aiReply.ifBlank { "A IA não enviou nenhuma resposta." }

                    etDiario.setText("")
                    showDialog("Resposta da IA", ai)

                } catch (e: Exception) {
                    e.printStackTrace()
                    showDialog(
                        "Erro",
                        "Não foi possível se conectar à IA.\n\n" +
                                "Detalhes: ${e.message ?: "erro desconhecido"}"
                    )
                }
            }

        } catch (e: Exception) {
            // QUALQUER erro antes de entrar na coroutine (ex.: Base URL inválida)
            e.printStackTrace()
            showDialog(
                "Erro de configuração",
                "Erro ao preparar a conexão com a API.\n\n" +
                        "Detalhes: ${e.message ?: "erro desconhecido"}"
            )
            // não redireciona, só mostra erro
        }
    }
}
