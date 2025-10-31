package com.app.auramind

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity

class HistoricoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)

        // Botões "Ver"
        findViewById<Button>(R.id.btnVerProblemas).setOnClickListener {
            Toast.makeText(this, "Abrir histórico de problemas e soluções", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btnVerConversas).setOnClickListener {
            Toast.makeText(this, "Abrir histórico de conversas", Toast.LENGTH_SHORT).show()
        }

        // Barra inferior (toasts por enquanto)
        findViewById<ImageButton>(R.id.navAgenda).setOnClickListener {
            //Toast.makeText(this, "Agenda clicada", Toast.LENGTH_SHORT).show()
            val intentHist = Intent(this, HistoricoActivity::class.java)
            startActivity(intentHist)
            finish()
        }

        findViewById<ImageButton>(R.id.navHome).setOnClickListener {
            //Toast.makeText(this, "Home clicada", Toast.LENGTH_SHORT).show()
            val intentHome = Intent(this, DashBoardActivity::class.java)
            startActivity(intentHome)
            finish()
        }

        findViewById<ImageButton>(R.id.navProfile).setOnClickListener {
            //Toast.makeText(this, "Perfil clicado", Toast.LENGTH_SHORT).show()
            val intentPerfil = Intent(this, PerfilActivity::class.java)
            startActivity(intentPerfil)
            finish()
        }
        findViewById<ImageButton>(R.id.navSettings).setOnClickListener {
            //Toast.makeText(this, "Configurações clicadas", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ConfiguracoesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
