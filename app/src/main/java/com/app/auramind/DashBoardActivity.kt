package com.app.auramind

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.ComponentActivity

class DashBoardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Barra inferior
        val navAgenda = findViewById<ImageButton>(R.id.navAgenda)
        val navHome = findViewById<ImageButton>(R.id.navHome)
        val navProfile = findViewById<ImageButton>(R.id.navProfile)
        val navSettings = findViewById<ImageButton>(R.id.navSettings)
        val btnMic = findViewById<ImageButton>(R.id.btnMic)
        val btnLivro = findViewById<ImageButton>(R.id.btnLivro)
        val btnCamera = findViewById<ImageButton>(R.id.btnCamera)

        // Clicou na imagem do MICROFONE
        btnMic.setOnClickListener {
            val intent = Intent(this, AudioActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Clicou na imagem do DIARIO
        btnLivro.setOnClickListener {
            val diario = Intent(this, DiarioActivity::class.java)
            startActivity(diario)
            finish()
        }

        // Clicou na imagem da CAMERA
        btnCamera.setOnClickListener {
            val foto = Intent(this, FotoActivity::class.java)
            startActivity(foto)
            finish()
        }

        navAgenda.setOnClickListener {
            //Toast.makeText(this, "Agenda clicada", Toast.LENGTH_SHORT).show()
            val intentPerfil = Intent(this, HistoricoActivity::class.java)
            startActivity(intentPerfil)
            finish()
        }

        navHome.setOnClickListener {
            //Toast.makeText(this, "Home clicada", Toast.LENGTH_SHORT).show()]
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
