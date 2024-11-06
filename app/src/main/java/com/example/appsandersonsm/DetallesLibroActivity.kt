package com.example.appsandersonsm

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetallesLibroActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var imagenPortada: ImageView  // Agrega una ImageView en tu layout para la portada

    private lateinit var editTextProgressCurrent: EditText
    private lateinit var editTextProgressTotal: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_libro)

        editTextProgressCurrent = findViewById(R.id.editTextProgressCurrent)
        editTextProgressTotal = findViewById(R.id.editTextProgressTotal)


        progressBar = findViewById(R.id.progressBar)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        imagenPortada = findViewById(R.id.portadaImageView) // ImageView en el layout

        // Obtén el ID del libro del Intent
        val libroId = intent.getIntExtra("LIBRO_ID", 0)

        // Asigna los datos según el ID del libro
        configurarVistaLibro(libroId)

        // Configuración del BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_settings -> {
                    startActivity(Intent(this, AjustesActivity::class.java))
                    true
                }
                R.id.nav_map -> {
                    startActivity(Intent(this, MapaInteractivoActivity::class.java))
                    true
                }
                R.id.nav_book -> true
                else -> false
            }
        }
        bottomNavigationView.menu.findItem(R.id.nav_book).isChecked = true

        editTextProgressCurrent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateProgressBar()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        editTextProgressTotal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateProgressBar()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


    }


    private fun updateProgressBar() {
        val currentText = editTextProgressCurrent.text.toString()
        val totalText = editTextProgressTotal.text.toString()

        val current = currentText.toIntOrNull() ?: 0
        val total = totalText.toIntOrNull() ?: 0

        if (total > 0) {
            val progress = (current * 100) / total
            progressBar.progress = progress.coerceIn(0, 100) // Asegura que el progreso esté entre 0 y 100
        } else {
            progressBar.progress = 0
        }
    }


    private fun configurarVistaLibro(libroId: Int) {
        // Usa `libroId` para personalizar la vista
        when (libroId) {
            1 -> {
                imagenPortada.setImageResource(R.drawable.portada_elcamino) // Reemplaza con la portada correspondiente
                progressBar.progress = 50
                editTextProgressCurrent.setText("50")
                editTextProgressTotal.setText("100")
            }
            2 -> {
                imagenPortada.setImageResource(R.drawable.portada_palabrasradiantes) // Otra portada
                progressBar.progress = 20
                editTextProgressCurrent.setText("20")
                editTextProgressTotal.setText("100")
            }
            // Añade más casos para otros libros
            else -> {
                imagenPortada.setImageResource(R.drawable.portada_elcamino) // Imagen por defecto
                progressBar.progress = 0
            }
        }
    }
}
