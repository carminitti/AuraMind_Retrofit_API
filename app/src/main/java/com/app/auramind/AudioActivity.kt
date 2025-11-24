package com.app.auramind
import com.app.auramind.util.EmotionMapper

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.*
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.auramind.network.ApiResponse
import com.app.auramind.network.ApiService
import com.app.auramind.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class AudioActivity : ComponentActivity() {

    companion object {
        private const val REQ_RECORD_AUDIO = 101
    }

    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecordingWav = false
    private var audioFile: File? = null
    private var player: MediaPlayer? = null

    private lateinit var btnVoltarRow: LinearLayout
    private lateinit var ivVoltar: ImageView
    private lateinit var tvVoltar: TextView
    private lateinit var btnMic: ImageButton
    private lateinit var btnWave: ImageButton
    private lateinit var btnEnviarAudio: Button
    private lateinit var btnUltimaEmocao: Button

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)

        btnVoltarRow = findViewById(R.id.btnVoltarRow)
        ivVoltar = findViewById(R.id.ivVoltar)
        tvVoltar = findViewById(R.id.tvVoltar)
        btnMic = findViewById(R.id.btnMic)
        btnWave = findViewById(R.id.btnWave)
        btnEnviarAudio = findViewById(R.id.btnEnviarAudio)
        btnUltimaEmocao = findViewById(R.id.btnUltimaEmocao)

        btnUltimaEmocao.setOnClickListener { buscarUltimaEmocao() }

        btnVoltarRow.setOnClickListener {
            if (isRecordingWav) stopRecording(save = false)
            startActivity(Intent(this, DashBoardActivity::class.java))
            finish()
        }

        btnMic.setOnClickListener {
            if (isRecordingWav) {
                Toast.makeText(this, "Já está gravando…", Toast.LENGTH_SHORT).show()
            } else {
                ensureMicPermissionThenStart()
            }
        }

        btnWave.setOnClickListener {
            Toast.makeText(this, "Visualizador de onda (placeholder)", Toast.LENGTH_SHORT).show()
        }

        btnEnviarAudio.setOnClickListener {
            if (isRecordingWav) {
                stopRecording(save = true)
                audioFile?.let {
                    Toast.makeText(this, "Áudio salvo: ${it.name}", Toast.LENGTH_LONG).show()
                    playAudio(it)
                    enviarAudioParaApi(it)
                } ?: Toast.makeText(this, "Erro ao salvar áudio", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Nada está sendo gravado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // === Permissões ===
    @RequiresApi(Build.VERSION_CODES.S)
    private fun ensureMicPermissionThenStart() {
        val micGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (micGranted) {
            startRecording()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO),
                REQ_RECORD_AUDIO
            )
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == REQ_RECORD_AUDIO &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startRecording()
        } else {
            Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show()
        }
    }

    // === Gravação WAV ===
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording() {
        val sampleRate = 44100
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            .coerceAtLeast(sampleRate * 2)

        val dir = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        if (dir?.exists() == false) dir.mkdirs()

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(System.currentTimeMillis())
        audioFile = File(dir, "rec_$timestamp.wav")

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        audioRecord?.startRecording()
        isRecordingWav = true

        recordingThread = thread(start = true) {
            val pcmData = mutableListOf<Byte>()
            val buffer = ByteArray(bufferSize)
            while (isRecordingWav) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    pcmData.addAll(buffer.take(read))
                }
            }
            writeWavFile(audioFile!!, pcmData.toByteArray(), sampleRate, 1, 16)
        }

        Toast.makeText(this, "🎙️ Gravando áudio (WAV)...", Toast.LENGTH_SHORT).show()
    }

    private fun stopRecording(save: Boolean) {
        try {
            isRecordingWav = false
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
            recordingThread?.join()
        } catch (_: Exception) {}

        if (!save) {
            audioFile?.delete()
            audioFile = null
        }
    }

    private fun writeWavFile(
        file: File,
        data: ByteArray,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int
    ) {
        val totalDataLen = data.size + 36
        val byteRate = sampleRate * channels * bitsPerSample / 8

        DataOutputStream(FileOutputStream(file)).use { out ->
            out.writeBytes("RIFF")
            out.writeInt(Integer.reverseBytes(totalDataLen))
            out.writeBytes("WAVE")
            out.writeBytes("fmt ")
            out.writeInt(Integer.reverseBytes(16))
            out.writeShort(java.lang.Short.reverseBytes(1.toShort()).toInt()) // PCM
            out.writeShort(java.lang.Short.reverseBytes(channels.toShort()).toInt())
            out.writeInt(Integer.reverseBytes(sampleRate))
            out.writeInt(Integer.reverseBytes(byteRate))
            out.writeShort(java.lang.Short.reverseBytes((channels * bitsPerSample / 8).toShort()).toInt())
            out.writeShort(java.lang.Short.reverseBytes(bitsPerSample.toShort()).toInt())
            out.writeBytes("data")
            out.writeInt(Integer.reverseBytes(data.size))
            out.write(data)
        }
    }

    // === API ===
    // === API ===
    private fun enviarAudioParaApi(file: File) {
        val retrofit = RetrofitClient.instance
        val api = retrofit.create(ApiService::class.java)

        val requestFile = file.asRequestBody("audio/wav".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        api.enviarAudio(body).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                val tv = findViewById<TextView>(R.id.tvResultado)
                if (response.isSuccessful) {
                    val emocaoEn = response.body()?.emocao_detectada ?: "undefined"
                    getSharedPreferences("emotion", MODE_PRIVATE)
                        .edit()
                        .putString("last_emotion_en", emocaoEn)
                        .apply()
                    val pack = EmotionMapper.map(emocaoEn)

                    // Mostra emoção em PT-BR na própria tela
                    tv.text = "🎭 Emoção detectada: ${pack.emotionPt}"

                    // ❌ NÃO redireciona mais para telas de áudio / vídeo
                    // Se quiser usar os links depois, você ainda tem:
                    // pack.audio  -> lista de URLs Spotify
                    // pack.video  -> lista de URLs YouTube

                    Toast.makeText(
                        this@AudioActivity,
                        "Emoção detectada: ${pack.emotionPt}",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    tv.text = "⚠️ Erro da API (${response.code()})"
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                findViewById<TextView>(R.id.tvResultado).text =
                    "❌ Falha na conexão: ${t.message}"
            }
        })
    }

    private fun buscarUltimaEmocao() {
        val retrofit = RetrofitClient.instance
        val api = retrofit.create(ApiService::class.java)

        api.getUltimaEmocao().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                val tv = findViewById<TextView>(R.id.tvResultado)
                if (response.isSuccessful) {
                    val ultima = response.body()?.ultima_emocao
                        ?: response.body()?.mensagem ?: "Sem dados"
                    tv.text = "🧠 Última emoção: $ultima"
                } else {
                    tv.text = "⚠️ Erro na resposta da API"
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                findViewById<TextView>(R.id.tvResultado).text =
                    "❌ Falha na conexão: ${t.message}"
            }
        })
    }

    // === Reprodução do áudio ===
    private fun playAudio(file: File) {
        releasePlayer()
        try {
            player = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setOnCompletionListener { releasePlayer() }
                prepare()
                start()
            }
            Toast.makeText(this, "▶️ Reproduzindo...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erro ao reproduzir: ${e.message}", Toast.LENGTH_LONG).show()
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        try {
            player?.stop()
            player?.release()
        } catch (_: Exception) {}
        player = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRecordingWav) stopRecording(save = false)
        releasePlayer()
    }
}
