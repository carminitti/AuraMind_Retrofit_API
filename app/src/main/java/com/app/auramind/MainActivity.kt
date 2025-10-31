package com.app.auramind

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import kotlin.jvm.java

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // chama o XML da tela

        val editLogin = findViewById<EditText>(R.id.editLogin)
        val editSenha = findViewById<EditText>(R.id.editSenha)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        val txtCriarConta = findViewById<TextView>(R.id.txtCriarConta)
        val txtEsqueciSenha = findViewById<TextView>(R.id.txtEsqueciSenha)

        // Ação do botão Confirmar
        btnConfirmar.setOnClickListener {
            val login = editLogin.text.toString()
            val senha = editSenha.text.toString()

            if (login == "admin" && senha == "1234") {
                Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()

                // Cria um Intent para iniciar a DashboardActivity
                val intent = Intent(this, DashBoardActivity::class.java)
                // Inicia a nova Activity
                startActivity(intent)

                //Finaliza a MainActivity para que o usuário não possa voltar
                // usando o botão "Voltar" do aparelho.
                // finish()
            } else {
                Toast.makeText(this, "Login ou senha incorretos!", Toast.LENGTH_SHORT).show()
            }
        }

        // Ação do texto "Clique aqui"
        txtCriarConta.setOnClickListener {
            //Toast.makeText(this, "Você clicou no link!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, NovaConta::class.java)
            startActivity(intent)
            finish()
        }

        // Ação do texto "Clique aqui"
        txtEsqueciSenha.setOnClickListener {
            Toast.makeText(this, "Você clicou no link!", Toast.LENGTH_SHORT).show()
        }

  }
}



