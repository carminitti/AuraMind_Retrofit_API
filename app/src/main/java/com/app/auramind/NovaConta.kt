package com.app.auramind

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity

class NovaConta : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nova_conta)

        val editNLogin = findViewById<EditText>(R.id.editNLogin)
        val editNSenha = findViewById<EditText>(R.id.editNSenha)
        val editNConfirmaSenha = findViewById<EditText>(R.id.editNConfirmaSenha)
        val btnNProximo = findViewById<Button>(R.id.btnNProximo)

        btnNProximo.setOnClickListener {
            val login = editNLogin.text.toString()
            val senha = editNSenha.text.toString()
            val confirmaSenha = editNConfirmaSenha.text.toString()

            if (login.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else if (senha != confirmaSenha) {
                Toast.makeText(this, "As senhas não conferem", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()

                // Exemplo: redirecionar para MainActivity
                val intent = Intent(this, Motivo::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
