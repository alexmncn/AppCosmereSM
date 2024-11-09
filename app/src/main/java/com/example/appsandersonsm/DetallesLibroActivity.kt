package com.example.appsandersonsm

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.appsandersonsm.Modelo.Libro
import com.example.appsandersonsm.Repositorio.DatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetallesLibroActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var imagenPortada: ImageView
    private lateinit var editTextProgressCurrent: EditText
    private lateinit var editTextProgressTotal: EditText
    private lateinit var dbHelper: DatabaseHelper
    private var libro: Libro? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_libro)

        // Inicializar vistas
        editTextProgressCurrent = findViewById(R.id.editTextProgressCurrent)
        editTextProgressTotal = findViewById(R.id.editTextProgressTotal)
        progressBar = findViewById(R.id.progressBar)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        imagenPortada = findViewById(R.id.portadaImageView)

        // Inicializar el DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Cargar datos iniciales en la base de datos si es la primera vez
        dbHelper.cargarDatosInicialesDesdeJson()

        // Obtén el ID del libro del Intent
        val libroId = intent.getIntExtra("LIBRO_ID", 0)

        // Cargar el libro desde la base de datos
        cargarDatosLibro(libroId)

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
                R.id.nav_book -> {
                    startActivity(Intent(this, LibroActivity::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.menu.findItem(R.id.nav_book).isChecked = true

        // Listeners para actualizar el progreso cuando cambian los valores
        editTextProgressCurrent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateProgressBar()
                guardarProgresoEnBaseDeDatos()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        editTextProgressTotal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateProgressBar()
                guardarProgresoEnBaseDeDatos()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun cargarDatosLibro(libroId: Int) {
        libro = dbHelper.getLibroById(libroId)

        libro?.let {
            val resID = resources.getIdentifier(it.nombrePortada, "drawable", packageName)
            imagenPortada.setImageResource(resID)
            progressBar.progress = it.progreso
            editTextProgressCurrent.setText(it.progreso.toString())
            editTextProgressTotal.setText("100")
        }
    }

    private fun updateProgressBar() {
        val current = editTextProgressCurrent.text.toString().toIntOrNull() ?: 0
        val total = editTextProgressTotal.text.toString().toIntOrNull() ?: 100

        val progress = (current * 100) / total
        progressBar.progress = progress.coerceIn(0, 100)
    }

    private fun guardarProgresoEnBaseDeDatos() {
        val current = editTextProgressCurrent.text.toString().toIntOrNull() ?: 0
        libro?.progreso = current
        libro?.let { dbHelper.actualizarProgresoLibro(it) }
    }


}
