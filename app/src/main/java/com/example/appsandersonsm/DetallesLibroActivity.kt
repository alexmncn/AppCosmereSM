package com.example.appsandersonsm

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetallesLibroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_libro)

        val libroId = intent.getIntExtra("LIBRO_ID", -1)
        if (libroId == -1) {
            // Manejar el error, por ejemplo, finalizar la actividad
            finish()
            return
        }

        // Referencias a las vistas
        val tituloTextView: TextView = findViewById(R.id.tituloTextView)
        val portadaImageView: ImageView = findViewById(R.id.portadaImageView)
        val descripcionTextView: TextView = findViewById(R.id.descripcionTextView)

        // Datos de los libros (puedes reemplazar esto con una base de datos o API)
        val libros = listOf(
            Libro(
                id = 1,
                titulo = "El alma del emperador",
                descripcion = "Descripción detallada de 'El alma del emperador'.",
                portadaResId = R.drawable.portada_elcamino
            ),
            Libro(
                id = 2,
                titulo = "Elantris",
                descripcion = "Descripción detallada de 'Elantris'.",
                portadaResId = R.drawable.portada_palabrasradiantes
            )
            // Agrega más libros según tus necesidades
        )

        val libro = libros.find { it.id == libroId }
        if (libro == null) {
            // Manejar el caso donde el libro no se encuentra
            finish()
            return
        }

        // Configurar las vistas con la información del libro
        tituloTextView.text = libro.titulo
        descripcionTextView.text = libro.descripcion
        Glide.with(this)
            .load(libro.portadaResId)
            .into(portadaImageView)
    }

    data class Libro(
        val id: Int,
        val titulo: String,
        val descripcion: String,
        val portadaResId: Int
    )
}
