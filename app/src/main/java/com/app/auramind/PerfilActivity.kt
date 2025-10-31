package com.app.auramind

import android.content.Intent
import androidx.activity.ComponentActivity

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast

class PerfilActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Ícones principais
        findViewById<ImageButton>(R.id.btnFone).setOnClickListener {
            //Toast.makeText(this, "Fone de ouvido clicado", Toast.LENGTH_SHORT).show()
            val intentAudio = Intent(this, AudioSugestaoActivity::class.java)
            startActivity(intentAudio)
            finish()
        }
        findViewById<ImageButton>(R.id.btnPlay).setOnClickListener {
            //Toast.makeText(this, "Play clicado", Toast.LENGTH_SHORT).show()
            val intentPlay = Intent(this, VideoSugestaoActivity::class.java)
            startActivity(intentPlay)
            finish()
        }

        // Barra inferior
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


