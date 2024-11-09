package com.example.appsandersonsm

import android.content.Intent
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.appsandersonsm.Modelo.Libro
import com.example.appsandersonsm.Repositorio.DatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.getstream.photoview.PhotoView

class MapaInteractivoActivity : AppCompatActivity() {

    private lateinit var photoView: PhotoView
    private lateinit var markerContainer: FrameLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listaLibros: List<Libro>
    private val markers = mutableListOf<ImageView>()
    private lateinit var drawable: Drawable

    // Coordenadas normalizadas asociadas por ID de libro
    private val libroCoordenadasNormalizadas = mapOf(
        1 to PointF(0.5f, 0.85f),  // ID 1
        2 to PointF(0.6f, 0.8f)    // ID 2
        // Agrega más entradas según tus necesidades
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_interactivo)

        // Inicializar el DatabaseHelper y cargar los datos iniciales si la base de datos está vacía
        dbHelper = DatabaseHelper(this)
        dbHelper.cargarDatosInicialesDesdeJson()

        // Obtener la lista de libros desde la base de datos
        listaLibros = dbHelper.getAllLibros()

        photoView = findViewById(R.id.photoView)
        markerContainer = findViewById(R.id.mapContainer)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Configuración de la imagen en el PhotoView
        drawable = photoView.drawable ?: throw IllegalStateException("PhotoView no tiene una imagen establecida.")
        photoView.minimumScale = 1.0f
        photoView.mediumScale = 1.5f
        photoView.maximumScale = 3.0f

        // Listener para inicializar los marcadores
        photoView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                inicializarMarcadores()
                photoView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        // Listener para actualizar la posición de los marcadores
        photoView.setOnMatrixChangeListener { actualizarMarcadores() }

        // Configuración de BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_settings -> {
                    startActivity(Intent(this, AjustesActivity::class.java))
                    true
                }
                R.id.nav_map -> true
                R.id.nav_book -> {
                    startActivity(Intent(this, LibroActivity::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.menu.findItem(R.id.nav_map).isChecked = true
    }

    private fun inicializarMarcadores() {
        listaLibros.forEach { libro ->
            val marker = ImageView(this).apply {
                val sizeInDp = 60
                val scale = resources.displayMetrics.density
                val sizeInPx = (sizeInDp * scale + 0.5f).toInt()
                layoutParams = FrameLayout.LayoutParams(sizeInPx, sizeInPx)
                scaleType = ImageView.ScaleType.CENTER_CROP
                contentDescription = "Libro ${libro.id}"

                // Cargar la imagen de la portada
                val resID = resources.getIdentifier(libro.nombrePortada, "drawable", packageName)
                Glide.with(this@MapaInteractivoActivity)
                    .load(resID)
                    .apply(RequestOptions.circleCropTransform())
                    .into(this)

                setOnClickListener {
                    abrirDetallesLibro(libro.id)
                }
            }
            markers.add(marker)
            markerContainer.addView(marker)
        }
        actualizarMarcadores() // Actualiza las posiciones iniciales
    }

    private fun actualizarMarcadores() {
        val matrix = FloatArray(9)
        photoView.imageMatrix.getValues(matrix)

        val scaleX = matrix[Matrix.MSCALE_X]
        val scaleY = matrix[Matrix.MSCALE_Y]
        val transX = matrix[Matrix.MTRANS_X]
        val transY = matrix[Matrix.MTRANS_Y]

        val imageWidth = drawable.intrinsicWidth.toFloat()
        val imageHeight = drawable.intrinsicHeight.toFloat()

        val scaledWidth = imageWidth * scaleX
        val scaledHeight = imageHeight * scaleY

        markers.forEachIndexed { index, marker ->
            val libro = listaLibros[index]
            val coordNormalizada = libroCoordenadasNormalizadas[libro.id] ?: PointF(0.5f, 0.5f)
            val absX = (coordNormalizada.x * scaledWidth) + transX
            val absY = (coordNormalizada.y * scaledHeight) + transY
            marker.x = absX - (marker.width / 2)
            marker.y = absY - (marker.height / 2)
        }
    }

    private fun abrirDetallesLibro(libroId: Int) {
        val intent = Intent(this, DetallesLibroActivity::class.java)
        intent.putExtra("LIBRO_ID", libroId)
        startActivity(intent)
    }
}
