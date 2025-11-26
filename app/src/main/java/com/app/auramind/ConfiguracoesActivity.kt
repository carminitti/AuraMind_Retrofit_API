package com.app.auramind

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationManagerCompat

class ConfiguracoesActivity : ComponentActivity() {

    private val PREFS = "auramind_config"
    private val KEY_NOTIFICACOES = "notificacoes_ativas"
    private val KEY_DADOS = "dados_permitidos"
    private val KEY_THEME_MODE = "theme_mode"   // já usado pelas outras telas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        val switchNotificacoes = findViewById<Switch>(R.id.switchNotificacoes)
        val switchDados = findViewById<Switch>(R.id.switchDados)

        // ------ CARREGAR ESTADOS SALVOS ------
        switchNotificacoes.isChecked = prefs.getBoolean(KEY_NOTIFICACOES, true)
        switchDados.isChecked = prefs.getBoolean(KEY_DADOS, false)

        // ------ AÇÕES DOS SWITCHES ------

        // Notificações
        switchNotificacoes.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_NOTIFICACOES, isChecked).apply()

            if (isChecked) {
                // Verifica se as notificações do app estão realmente liberadas no sistema
                val notificacoesAtivas = NotificationManagerCompat.from(this)
                    .areNotificationsEnabled()

                if (!notificacoesAtivas) {
                    // Mostra um alerta orientando a habilitar nas configurações do sistema
                    AlertDialog.Builder(this)
                        .setTitle("Habilitar notificações")
                        .setMessage(
                            "Para que o AuraMind envie avisos e lembretes, " +
                                    "você precisa habilitar as notificações deste app nas configurações do sistema."
                        )
                        .setPositiveButton("Abrir configurações") { _, _ ->
                            abrirConfigNotificacoesDoApp()
                        }
                        .setNegativeButton("Agora não", null)
                        .show()
                } else {
                    Toast.makeText(this, "Notificações ativadas", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Notificações desativadas", Toast.LENGTH_SHORT).show()
            }
        }

        // Acesso a dados
        switchDados.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_DADOS, isChecked).apply()

            if (isChecked) {
                AlertDialog.Builder(this)
                    .setTitle("Coleta de dados habilitada")
                    .setMessage(
                        "Ao permitir o uso de dados, o AuraMind poderá usar suas entradas " +
                                "para melhorar as respostas da IA e personalizar sugestões.\n\n" +
                                "Suas informações não serão compartilhadas com terceiros sem autorização."
                    )
                    .setPositiveButton("Entendi", null)
                    .show()
            } else {
                Toast.makeText(this, "Acesso a dados negado", Toast.LENGTH_SHORT).show()
            }
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

    /**
     * Abre diretamente a tela de configurações de notificações do app no Android.
     */
    private fun abrirConfigNotificacoesDoApp() {
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        startActivity(intent)
    }
}
