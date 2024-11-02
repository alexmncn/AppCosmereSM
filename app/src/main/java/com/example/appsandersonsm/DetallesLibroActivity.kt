// DetallesLibroActivity.kt

package com.example.appsandersonsm

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetallesLibroActivity : AppCompatActivity() {


    private lateinit var progressBar: ProgressBar
    private lateinit var progressTextView: TextView
    private lateinit var iniciarButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_libro) // Asegúrate de que el nombre del layout sea correcto

        /*
        // Inicializar vistas
        progressBar = findViewById(R.id.progressBar)
        progressTextView = findViewById(R.id.progressTextView)
        iniciarButton = findViewById(R.id.iniciarButton)

        // Configurar el progreso inicial
        updateProgress(50) // Por ejemplo, 50%

        // Listener para actualizar el progreso al hacer clic en el botón
        iniciarButton.setOnClickListener {
            val newProgress = progressBar.progress + 10
            if (newProgress <= progressBar.max) {
                updateProgress(newProgress)
            }
        }
    }

    private fun updateProgress(progress: Int) {
        progressBar.progress = progress
        progressTextView.text = "$progress%"
    }

     */
    }
}
