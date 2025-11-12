package com.app.auramind


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.app.auramind.chat.AuthApiService
import com.app.auramind.chat.LoginReq
import com.app.auramind.chat.RegisterReq
import com.app.auramind.chat.ChatRetrofit
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editLogin = findViewById<EditText>(R.id.editLogin)
        val editSenha = findViewById<EditText>(R.id.editSenha)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        val txtCriarConta = findViewById<TextView>(R.id.txtCriarConta)

        val authApi = ChatRetrofit.build(this).create(AuthApiService::class.java)
        val prefs = getSharedPreferences("auth", Context.MODE_PRIVATE)

        fun saveToken(t: String) {
            prefs.edit().putString("jwt", t).apply()
        }

        // Se já houver token, tenta /me e pula pro dashboard
        lifecycleScope.launch {
            val token = prefs.getString("jwt", null)
            if (!token.isNullOrBlank()) {
                try {
                    val me = authApi.me()
                    Toast.makeText(this@MainActivity, "Bem-vindo, ${me.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, DashBoardActivity::class.java))
                    finish()
                } catch (_: Exception) {
                    // token inválido → ignorar e pedir login
                }
            }
        }

        btnConfirmar.setOnClickListener {
            val login = editLogin.text.toString().trim()
            val senha = editSenha.text.toString()

            if (login.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha login e senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // Tenta login
                    val lr = authApi.login(LoginReq(login, senha))
                    saveToken(lr.token)
                    val me = authApi.me()
                    Toast.makeText(this@MainActivity, "Olá, ${me.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, DashBoardActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Falha no login. Verifique seus dados.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        txtCriarConta.setOnClickListener {
            // Registro rápido de exemplo (substitua pela sua UI NovaConta)
            val login = editLogin.text.toString().trim()
            val senha = editSenha.text.toString()
            if (login.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Informe e-mail e senha para registrar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                try {
                    val rr = authApi.register(RegisterReq(login, senha, "Usuário"))
                    saveToken(rr.token)
                    val me = authApi.me()
                    Toast.makeText(this@MainActivity, "Bem-vindo, ${me.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, DashBoardActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Falha no registro.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
