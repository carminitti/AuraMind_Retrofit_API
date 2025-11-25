package com.app.auramind

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.Toast

class ConfiguracoesActivity : ComponentActivity() {

    private val PREFS = "auramind_config"
    private val KEY_NOTIFICACOES = "notificacoes_ativas"
    private val KEY_DADOS = "dados_permitidos"
    private val KEY_THEME_MODE = "theme_mode"   // 👈 NOVO: modo de tema

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
        switchNotificacoes.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_NOTIFICACOES, isChecked).apply()
            val msg = if (isChecked) "Notificações ativadas" else "Notificações desativadas"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        switchDados.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_DADOS, isChecked).apply()
            val msg = if (isChecked) "Acesso a dados permitido" else "Acesso a dados negado"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // ------ SELETOR DE TEMA (PALETA DE CORES) ------
        val themeSpinner = findViewById<Spinner?>(R.id.spinnerTema)

        // Só configura se o Spinner existir no XML
        themeSpinner?.let { spinner ->
            // Lista de opções exibidas para o usuário
            val opcoes = listOf(
                "Automático pela emoção", // auto
                "Tema claro",             // light
                "Tema escuro",            // dark
                "Tema sereno",            // calm
                "Tema vibrante"           // vibrant
            )

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                opcoes
            ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

            spinner.adapter = adapter

            // Lê tema salvo
            val modoSalvo = prefs.getString(KEY_THEME_MODE, "auto") ?: "auto"
            val indexSelecao = when (modoSalvo) {
                "auto"     -> 0
                "light"    -> 1
                "dark"     -> 2
                "calm"     -> 3
                "vibrant"  -> 4
                else       -> 0
            }
            spinner.setSelection(indexSelecao, false)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val novoModo = when (position) {
                        0 -> "auto"
                        1 -> "light"
                        2 -> "dark"
                        3 -> "calm"
                        4 -> "vibrant"
                        else -> "auto"
                    }

                    prefs.edit().putString(KEY_THEME_MODE, novoModo).apply()

                    val msg = when (novoModo) {
                        "auto"    -> "Tema baseado na emoção"
                        "light"   -> "Tema claro selecionado"
                        "dark"    -> "Tema escuro selecionado"
                        "calm"    -> "Tema sereno selecionado"
                        "vibrant" -> "Tema vibrante selecionado"
                        else      -> "Tema atualizado"
                    }
                    Toast.makeText(this@ConfiguracoesActivity, msg, Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // nada
                }
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
}
