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
import com.example.appsandersonsm.MySQL.DatabaseHelper
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

        // Base de Datos
        dbHelper = DatabaseHelper(this)
        crearOActualizarLibros()
        listaLibros = dbHelper.getAllLibros()

        photoView = findViewById(R.id.photoView)
        markerContainer = findViewById(R.id.mapContainer)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Carga la imagen en el PhotoView (asegúrate de que la imagen esté establecida en el XML o aquí)
        drawable = photoView.drawable ?: throw IllegalStateException("PhotoView no tiene una imagen establecida.")

        // Configuración de zoom
        photoView.minimumScale = 1.0f
        photoView.mediumScale = 1.5f
        photoView.maximumScale = 3.0f

        // Listener para inicializar los marcadores después de que `PhotoView` esté completamente cargado
        photoView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                inicializarMarcadores()
                photoView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        // Listener para actualizar la posición de los marcadores cuando se desplaza o hace zoom en `PhotoView`
        photoView.setOnMatrixChangeListener { actualizarMarcadores() }

        // Configuración de BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_settings -> {
                    // Navegar a la actividad de ajustes
                    val intent = Intent(this, AjustesActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_map -> {
                    // Ya estás en MapaInteractivoActivity, puedes refrescar o hacer otra acción
                    true
                }
                R.id.nav_book -> {
                    // Navegar a la actividad de libros
                    val intent = Intent(this, LibroActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Opcional: marcar como seleccionado el ícono del mapa
        bottomNavigationView.menu.findItem(R.id.nav_map).isChecked = true
    }

    private fun inicializarMarcadores() {
        listaLibros.forEach { libro ->
            val marker = ImageView(this).apply {
                val sizeInDp = 60 // Tamaño del marcador en dp
                val scale = resources.displayMetrics.density
                val sizeInPx = (sizeInDp * scale + 0.5f).toInt()
                layoutParams = FrameLayout.LayoutParams(sizeInPx, sizeInPx)
                scaleType = ImageView.ScaleType.CENTER_CROP
                contentDescription = "Libro ${libro.id}"
                // Carga la imagen circular utilizando Glide
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

        // Escala y traslación actuales
        val scaleX = matrix[Matrix.MSCALE_X]
        val scaleY = matrix[Matrix.MSCALE_Y]
        val transX = matrix[Matrix.MTRANS_X]
        val transY = matrix[Matrix.MTRANS_Y]

        // Dimensiones originales de la imagen
        val imageWidth = drawable.intrinsicWidth.toFloat()
        val imageHeight = drawable.intrinsicHeight.toFloat()

        // Calcula las dimensiones escaladas
        val scaledWidth = imageWidth * scaleX
        val scaledHeight = imageHeight * scaleY

        markers.forEachIndexed { index, marker ->
            val libro = listaLibros[index]
            val coordNormalizada = libroCoordenadasNormalizadas[libro.id] ?: PointF(0.5f, 0.5f) // Valor por defecto
            // Calcula la posición absoluta basada en las coordenadas normalizadas
            val absX = (coordNormalizada.x * scaledWidth) + transX
            val absY = (coordNormalizada.y * scaledHeight) + transY
            // Centra el marcador
            marker.x = absX - (marker.width / 2)
            marker.y = absY - (marker.height / 2)
        }
    }

    private fun abrirDetallesLibro(libroId: Int) {
        val intent = Intent(this, DetallesLibroActivity::class.java)
        intent.putExtra("LIBRO_ID", libroId)
        startActivity(intent)
    }

    private fun crearOActualizarLibros() {
        val libro1 = Libro(
            id = 1,
            nombreLibro = "El Camino de los Reyes",
            nombreSaga = "La Guerra de las Tormentas",
            nombrePortada = "portada_elcamino",
            progreso = 20
        )

        val libro2 = Libro(
            id = 2,
            nombreLibro = "Palabras Radiantes",
            nombreSaga = "La Guerra de las Tormentas",
            nombrePortada = "portada_palabrasradiantes",
            progreso = 50
        )

        dbHelper.insertOrUpdateLibro(libro1)
        dbHelper.insertOrUpdateLibro(libro2)
    }
}
