package com.app.auramind

import android.content.Intent
import androidx.activity.ComponentActivity

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast

class ConfiguracoesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        val switchNotificacoes = findViewById<Switch>(R.id.switchNotificacoes)
        val switchDados = findViewById<Switch>(R.id.switchDados)
        val btnCores = findViewById<ImageView>(R.id.btnCores)

        // Ações das opções
        switchNotificacoes.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Notificações ativadas" else "Notificações desativadas"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        switchDados.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Acesso a dados permitido" else "Acesso a dados negado"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        btnCores.setOnClickListener {
            Toast.makeText(this, "Opção de cores clicada", Toast.LENGTH_SHORT).show()
        }

        // Barra inferior
        val navAgenda = findViewById<ImageButton>(R.id.navAgenda)
        val navHome = findViewById<ImageButton>(R.id.navHome)
        val navProfile = findViewById<ImageButton>(R.id.navProfile)
        val navSettings = findViewById<ImageButton>(R.id.navSettings)

        navAgenda.setOnClickListener {
            //Toast.makeText(this, "Agenda clicada", Toast.LENGTH_SHORT).show()
            val intentHist = Intent(this, HistoricoActivity::class.java)
            startActivity(intentHist)
            finish()
        }

        navHome.setOnClickListener {
            //Toast.makeText(this, "Home clicada", Toast.LENGTH_SHORT).show()
            val intentHome = Intent(this, DashBoardActivity::class.java)
            startActivity(intentHome)
            finish()
        }

        navProfile.setOnClickListener {
            //Toast.makeText(this, "Perfil clicado", Toast.LENGTH_SHORT).show()
            val intentPerfil = Intent(this, PerfilActivity::class.java)
            startActivity(intentPerfil)
            finish()
        }

        navSettings.setOnClickListener {
            //Toast.makeText(this, "Configurações já abertas", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ConfiguracoesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
