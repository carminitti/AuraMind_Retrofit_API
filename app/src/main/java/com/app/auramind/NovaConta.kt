package com.app.auramind

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.app.auramind.chat.AuthApiService
import com.app.auramind.chat.RegisterReq
import com.app.auramind.chat.ChatRetrofit
import kotlinx.coroutines.launch

class NovaConta : ComponentActivity() {

    private fun saveToken(token: String) {
        val sp = getSharedPreferences("auth", Context.MODE_PRIVATE)
        sp.edit().putString("jwt", token).apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nova_conta)

        val editNLogin  = findViewById<EditText>(R.id.editNLogin)
        val editNSenha  = findViewById<EditText>(R.id.editNSenha)
        val editCNSenha = findViewById<EditText>(R.id.editNConfirmaSenha)
        val btnCriar    = findViewById<Button>(R.id.btnNProximo)

        // API Java (core)
        val authApi = ChatRetrofit.build(this).create(AuthApiService::class.java)

        btnCriar.setOnClickListener {
            val email         = editNLogin.text.toString().trim()
            val senha         = editNSenha.text.toString().trim()
            val confirmaSenha = editCNSenha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (senha != confirmaSenha) {
                Toast.makeText(this, "As senhas não conferem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // Aqui criamos a conta de verdade no banco via API Java
                    val res = authApi.register(
                        RegisterReq(
                            email = email,
                            password = senha,
                            displayName = email.substringBefore("@")
                        )
                    )

                    // Salva JWT para ser usado no interceptor
                    saveToken(res.token)

                    Toast.makeText(
                        this@NovaConta,
                        "Conta criada! Bem-vindo, ${res.displayName}",
                        Toast.LENGTH_LONG
                    ).show()

                    // Vai direto para o Dashboard (já logado)
                    val intent = Intent(this@NovaConta, DashBoardActivity::class.java)
                    startActivity(intent)
                    finish()

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@NovaConta,
                        "Erro ao criar conta. Verifique os dados ou tente novamente.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
