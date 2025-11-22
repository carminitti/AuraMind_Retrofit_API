package com.app.auramind

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.app.auramind.chat.AuthApiService
import com.app.auramind.chat.LoginReq
import com.app.auramind.chat.ChatRetrofit
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private fun saveToken(ctx: Context, t: String) {
        val prefs = ctx.getSharedPreferences("auth", Context.MODE_PRIVATE)
        prefs.edit().putString("jwt", t).apply()
    }

    private fun getToken(ctx: Context): String? {
        val prefs = ctx.getSharedPreferences("auth", Context.MODE_PRIVATE)
        return prefs.getString("jwt", null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editLogin     = findViewById<EditText>(R.id.editLogin)
        val editSenha     = findViewById<EditText>(R.id.editSenha)
        val btnConfirmar  = findViewById<Button>(R.id.btnConfirmar)
        val txtCriarConta = findViewById<TextView>(R.id.txtCriarConta)

        val authApi = ChatRetrofit.build(this, requireAuth = false)
            .create(AuthApiService::class.java)

        // Auto-login se já tiver token salvo
        // Verifica token, mas NÃO redireciona automaticamente
        lifecycleScope.launch {
            val token = getToken(this@MainActivity)
            if (!token.isNullOrBlank()) {
                // opcional: validar o token silenciosamente
                try { authApi.me() } catch (_: Exception) {}
            }
        }


        // LOGIN
        btnConfirmar.setOnClickListener {
            val login = editLogin.text.toString().trim()
            val senha = editSenha.text.toString()

            if (login.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha login e senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val lr = authApi.login(LoginReq(login, senha))
                    saveToken(this@MainActivity, lr.token)

                    val me = authApi.me()
                    Toast.makeText(
                        this@MainActivity,
                        "Olá, ${me.displayName}",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(Intent(this@MainActivity, DashBoardActivity::class.java))
                    finish()

                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Falha no login. Verifique seus dados.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // CRIAR CONTA → vai para a tela própria
        txtCriarConta.setOnClickListener {
            startActivity(Intent(this, NovaConta::class.java))
        }
    }
}
