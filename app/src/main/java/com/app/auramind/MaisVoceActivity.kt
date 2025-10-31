package com.app.auramind

import android.content.Intent
import androidx.activity.ComponentActivity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MaisVoceActivity : ComponentActivity() {

    // Variável para guardar o texto digitado (você pode salvar no banco depois)
    private var mensagemUsuario: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mais_voce)

        val etMaisSobreVoce = findViewById<EditText>(R.id.etMaisSobreVoce)
        val btnAgoraNao = findViewById<Button>(R.id.btnAgoraNao)
        val btnConcluir = findViewById<Button>(R.id.btnConcluir)

        // Clique: Agora não
        btnAgoraNao.setOnClickListener {
            mensagemUsuario = etMaisSobreVoce.text.toString()
            Toast.makeText(this, "Ação: Agora não (texto salvo em memória)", Toast.LENGTH_SHORT).show()
            // TODO: Navegar ou fechar se desejar
            val agoraNao = Intent(this, DashBoardActivity::class.java)
            startActivity(agoraNao)
            finish()
        }

        // Clique: Concluir
        btnConcluir.setOnClickListener {
            mensagemUsuario = etMaisSobreVoce.text.toString()
            val qtd = mensagemUsuario.length
            //Toast.makeText(this, "Concluído! (${qtd} caracteres salvos em memória)", Toast.LENGTH_SHORT).show()
            // TODO: Enviar para o banco de dados / próxima tela
            val concluir = Intent(this, DashBoardActivity::class.java)
            startActivity(concluir)
            finish()
        }
    }
}