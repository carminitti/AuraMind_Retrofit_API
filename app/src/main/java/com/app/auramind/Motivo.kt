package com.app.auramind

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity

class Motivo : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motivo)

        val etMotivo = findViewById<EditText>(R.id.etMotivo)
        val etEmocional = findViewById<EditText>(R.id.etEmocional)
        val btnProximo = findViewById<Button>(R.id.btnProximo)

        btnProximo.setOnClickListener {
            val motivo = etMotivo.text.toString().trim()
            val emocional = etEmocional.text.toString().trim()

            if (motivo.isEmpty() || emocional.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else {
                // Aqui você pode salvar os dados no banco ou enviar para a próxima tela
                Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show()

                // Exemplo: redirecionar para próxima tela
                val intent = Intent(this, MaisVoceActivity::class.java) // substitua pela tela correta
                startActivity(intent)
                finish()
            }
        }
    }
}
