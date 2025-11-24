package com.app.auramind

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast

class ConfiguracoesActivity : ComponentActivity() {

    private val PREFS = "auramind_config"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        val switchNotificacoes = findViewById<Switch>(R.id.switchNotificacoes)
        val switchDados = findViewById<Switch>(R.id.switchDados)

        // ------ CARREGAR ESTADOS SALVOS ------
        switchNotificacoes.isChecked = prefs.getBoolean("notificacoes_ativas", true)
        switchDados.isChecked = prefs.getBoolean("dados_permitidos", false)

        // ------ AÇÕES DOS SWITCHES ------
        switchNotificacoes.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notificacoes_ativas", isChecked).apply()

            val msg = if (isChecked) "Notificações ativadas" else "Notificações desativadas"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        switchDados.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dados_permitidos", isChecked).apply()

            val msg = if (isChecked) "Acesso a dados permitido" else "Acesso a dados negado"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // ------ BARRA INFERIOR ------
        val navAgenda = findViewById<ImageButton>(R.id.navAgenda)
        val navHome = findViewById<ImageButton>(R.id.navHome)
        val navProfile = findViewById<ImageButton>(R.id.navProfile)
        val navSettings = findViewById<ImageButton>(R.id.navSettings)

        navAgenda.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
            finish()
        }

        navHome.setOnClickListener {
            startActivity(Intent(this, DashBoardActivity::class.java))
            finish()
        }

        navProfile.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
        }

        navSettings.setOnClickListener {
            Toast.makeText(this, "Você já está nas configurações", Toast.LENGTH_SHORT).show()
        }
    }
}
