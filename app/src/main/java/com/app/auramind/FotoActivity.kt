package com.app.auramind

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Surface
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.camera.view.PreviewView
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FotoActivity : ComponentActivity() {

    companion object {
        private const val REQ_CAMERA = 201
    }

    //private lateinit var previewView: PreviewView
    private lateinit var btnCapturar: ImageButton
    private lateinit var btnVoltarRow: LinearLayout
    private lateinit var ivVoltar: ImageView
    private lateinit var tvVoltar: TextView

    //private var imageCapture: ImageCapture? = null
    //private lateinit var cameraExecutor: ExecutorService

    // Saída: onde a foto final fica disponível para você subir ao BD
    //var fotoArquivo: File? = null
    //var fotoDataHoraTexto: String? = null  // "dd/MM/yyyy HH:mm:ss"
    //var fotoDataHoraEpoch: Long? = null    // em ms

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto)

        //previewView   = findViewById(R.id.previewView)
        //btnCapturar   = findViewById(R.id.btnCapturar)
        btnVoltarRow  = findViewById(R.id.btnVoltarRow)
        ivVoltar      = findViewById(R.id.ivVoltar)
        tvVoltar      = findViewById(R.id.tvVoltar)

        // Preenche melhor o oval (recorte)
        //previewView.scaleType = PreviewView.ScaleType.FILL_CENTER

//        cameraExecutor = Executors.newSingleThreadExecutor()
//        ensureCameraPermissionThenStart()

//        btnCapturar.setOnClickListener { takePhoto() }

        // Voltar -> volta à DashboardActivity
        btnVoltarRow.setOnClickListener {
            val intent = Intent(this, DashBoardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    /*
    private fun ensureCameraPermissionThenStart() {
        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), REQ_CAMERA
            )
        }
    }
    */

/*
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissão da câmera negada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
 */

/*
     private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .setTargetRotation(previewView.display?.rotation ?: Surface.ROTATION_0)
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            imageCapture = ImageCapture.Builder()
                .setTargetRotation(previewView.display?.rotation ?: Surface.ROTATION_0)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT) // frontal
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Falha ao iniciar câmera: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: run {
            Toast.makeText(this, "Câmera não pronta", Toast.LENGTH_SHORT).show()
            return
        }

        // cria arquivo de saída
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (dir?.exists() == false) dir.mkdirs()
        val timestamp = System.currentTimeMillis()
        val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(timestamp)
        val photoFile = File(dir, "foto_$name.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    runOnUiThread {
                        Toast.makeText(this@FotoActivity, "Erro ao salvar: ${exc.message}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // guarda data/hora
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    fotoDataHoraTexto = sdf.format(timestamp)
                    fotoDataHoraEpoch = timestamp

                    fotoArquivo = photoFile

                    runOnUiThread {
                        Toast.makeText(
                            this@FotoActivity,
                            "Foto salva em:\n${photoFile.absolutePath}",
                            Toast.LENGTH_LONG
                        ).show()
                        // TODO: enviar 'fotoArquivo', 'fotoDataHoraTexto', 'fotoDataHoraEpoch' para o seu BD
                    }
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


 */

}


