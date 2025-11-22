package com.app.auramind

import android.content.Intent
import android.os.Bundle
import android.widget.*
import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.app.auramind.chat.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.app.auramind.chat.DiaryApiService
import com.app.auramind.chat.DiaryReq
import com.app.auramind.chat.ChatRetrofit
import androidx.activity.ComponentActivity
import com.app.auramind.network.ApiResponse
import com.app.auramind.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

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
    private lateinit var txtResultado: TextView
    private lateinit var tvTitulo: TextView
    private lateinit var tvVoltar: TextView
    private lateinit var ivVoltar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diario)

        btnEnviar = findViewById(R.id.btnEnviar)

        etDiario = findViewById(R.id.etDiario)

        btnEnviar.setOnClickListener {
            val texto = etDiario.text?.toString()?.trim() ?: ""
            if (texto.isEmpty()) {
                Toast.makeText(this, "Escreva algo antes de enviar.", Toast.LENGTH_SHORT).show()
            } else {
                // guarda (mínimo: SharedPreferences histórico)
                val prefs = getSharedPreferences("diario_history", Context.MODE_PRIVATE)
                val prev = prefs.getString("last_text", "")
                prefs.edit().putString("last_text", texto + "\n" + (prev ?: "")).apply()
                // limpa campo
                etDiario.setText("")
                enviarFluxoCompleto(texto)
            }
        }
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




        // Mostra o conteúdo da página atual
        atualizarEditText()

        // "Voltar" (linha inteira clicável)
        btnVoltarRow.setOnClickListener {
            salvarPaginaAtual()
            Toast.makeText(this, "Voltando (o texto será salvo depois no BD)", Toast.LENGTH_SHORT).show()
            val voltarTela = Intent(this, DashBoardActivity::class.java)
            startActivity(voltarTela)
            finish()
        }

        // Ir para a página anterior
        btnAnterior.setOnClickListener {
            salvarPaginaAtual()
            if (paginaAtual > 0) {
                paginaAtual--
                atualizarEditText()
            } else {
                Toast.makeText(this, "Você já está na primeira página", Toast.LENGTH_SHORT).show()
            }
        }

        // Ir para a próxima página (cria até 3)
        btnProximo.setOnClickListener {
            salvarPaginaAtual()
            if (paginaAtual < MAX_PAGES - 1) {
                paginaAtual++
                atualizarEditText()
            } else {
                Toast.makeText(this, "Limite de 3 páginas atingido", Toast.LENGTH_SHORT).show()
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
    private fun getOrCreateUserId(): String {
        val prefs = getSharedPreferences("auramind_prefs", Context.MODE_PRIVATE)
        var id = prefs.getString("user_id", null)
        if (id.isNullOrBlank()) {
            id = java.util.UUID.randomUUID().toString()
            prefs.edit().putString("user_id", id).apply()
        }
        return id
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
            // Se o usuário não estiver logado, ChatRetrofit.build(this)
            // deve lançar IllegalStateException (conforme combinamos no ChatRetrofit.kt)
            val diaryApi = ChatRetrofit.build(this).create(DiaryApiService::class.java)

            // 1) Mostra a mensagem do usuário na UI (isso você já faz antes de chamar essa função)
            // 2) Chama a API
            lifecycleScope.launch {
                try {
                    val res = diaryApi.sendDiaryMessage(DiaryReq(texto))
                    val ai = res.aiReply.ifBlank { "A IA não enviou nenhuma resposta." }

                    // 3) "Apaga" o texto digitado pelo usuário
                    etDiario.setText("")

                    // 4) Mostra a resposta da IA
                    showDialog("Resposta da IA", ai)
                } catch (e: Exception) {
                    showDialog("Erro", "Não foi possível se conectar à IA agora.")
                }
            }

        } catch (e: IllegalStateException) {
            // Cai aqui se não tiver JWT salvo (usuário não logado)
            AlertDialog.Builder(this)
                .setTitle("Sessão expirada")
                .setMessage("Por segurança, faça login novamente.")
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                .show()
        }
    }





}