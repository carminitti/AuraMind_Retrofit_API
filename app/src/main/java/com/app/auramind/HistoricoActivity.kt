package com.app.auramind

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import org.json.JSONArray

class HistoricoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)

        // Botões "Ver"
        findViewById<Button>(R.id.btnVerProblemas).setOnClickListener {
            Toast.makeText(this, "Em breve: histórico de problemas e soluções.", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnVerProblemas).setOnClickListener {
            val msg = carregarHistoricoDiario()

            AlertDialog.Builder(this)
                .setTitle("Histórico do diário")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show()
        }

        findViewById<Button>(R.id.btnVerConversas).setOnClickListener {
            val msg = carregarHistoricoEmocoes()
            AlertDialog.Builder(this)
                .setTitle("Histórico de emoções detectadas")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show()
        }

        // Barra inferior
        findViewById<ImageButton>(R.id.navAgenda).setOnClickListener {
            val intentHist = Intent(this, HistoricoActivity::class.java)
            startActivity(intentHist)
            finish()
        }

        findViewById<ImageButton>(R.id.navHome).setOnClickListener {
            val intentHome = Intent(this, DashBoardActivity::class.java)
            startActivity(intentHome)
            finish()
        }

        findViewById<ImageButton>(R.id.navProfile).setOnClickListener {
            val intentPerfil = Intent(this, PerfilActivity::class.java)
            startActivity(intentPerfil)
            finish()
        }
        findViewById<ImageButton>(R.id.navSettings).setOnClickListener {
            val intent = Intent(this, ConfiguracoesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun carregarHistoricoDiario(): String {
        val prefs = getSharedPreferences("diario_history", MODE_PRIVATE)
        val json = prefs.getString("items", null) ?: return "Nenhuma entrada registrada no diário ainda."

        return try {
            val arr = JSONArray(json)
            if (arr.length() == 0) return "Nenhuma entrada registrada no diário ainda."

            val sb = StringBuilder()

            // Mais recente primeiro
            for (i in arr.length() - 1 downTo 0) {
                val obj = arr.getJSONObject(i)

                val ts = obj.optString("timestamp", "-")
                val userText = obj.optString("userText", null)
                val aiText = obj.optString("aiText", null)
                val textoAntigo = obj.optString("texto", null) // compatibilidade com versão antiga

                sb.append("🕒 ").append(ts).append("\n")

                if (!userText.isNullOrBlank() || !aiText.isNullOrBlank()) {
                    if (!userText.isNullOrBlank()) {
                        sb.append("Você:\n")
                        sb.append(userText).append("\n\n")
                    }
                    if (!aiText.isNullOrBlank()) {
                        sb.append("AuraMind:\n")
                        sb.append(aiText).append("\n\n")
                    }
                } else if (!textoAntigo.isNullOrBlank()) {
                    // Histórico antigo (só texto)
                    sb.append(textoAntigo).append("\n\n")
                }
            }

            sb.toString()

        } catch (e: Exception) {
            e.printStackTrace()
            "Erro ao ler o histórico do diário."
        }
    }



    private fun carregarHistoricoEmocoes(): String {
        val prefs = getSharedPreferences("emotion", MODE_PRIVATE)
        val json = prefs.getString("emotion_history", null) ?: return "Nenhuma emoção registrada ainda."

        return try {
            val arr = JSONArray(json)
            if (arr.length() == 0) {
                "Nenhuma emoção registrada ainda."
            } else {
                val sb = StringBuilder()
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val ts = obj.optString("timestamp", "-")
                    val emoPt = obj.optString("emotionPt", "Indefinida")
                    sb.append("• ").append(ts).append(" - ").append(emoPt).append("\n")
                }
                sb.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Erro ao ler o histórico."
        }
    }
}