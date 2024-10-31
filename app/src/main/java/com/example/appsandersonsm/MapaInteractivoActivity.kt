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
import io.getstream.photoview.PhotoView

class MapaInteractivoActivity : AppCompatActivity() {

    private lateinit var photoView: PhotoView
    private lateinit var markerContainer: FrameLayout

    // Coordenadas normalizadas (valores entre 0 y 1) respecto al tamaño original de la imagen
    private val libroCoordenadasNormalizadas = listOf(
        PointF(0.3f, 0.2f),  // 30% desde la izquierda, 20% desde la parte superior para "El alma del emperador"
        PointF(0.7f, 0.8f)   // 70% desde la izquierda, 80% desde la parte superior para "Elantris"
        // Agrega más coordenadas según tus necesidades
    )

    // Recursos de imágenes para cada libro
    private val libroImagenes = listOf(
        R.drawable.portada_elcamino,          // Portada para "El alma del emperador"
        R.drawable.portada_palabrasradiantes   // Portada para "Elantris"
        // Agrega más imágenes según tus necesidades
    )

    private val markers = mutableListOf<ImageView>()
    private lateinit var drawable: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_interactivo)

        photoView = findViewById(R.id.photoView)
        markerContainer = findViewById(R.id.mapContainer)

        // Carga la imagen en el PhotoView (asegúrate de que la imagen esté establecida en el XML o aquí)
        // Si la imagen está establecida en el XML como en el ejemplo, obtén el drawable de PhotoView
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
    }

    private fun inicializarMarcadores() {
        libroCoordenadasNormalizadas.forEachIndexed { index, _ ->
            val marker = ImageView(this).apply {
                val sizeInDp = 60 // Tamaño del marcador en dp (más grande)
                val scale = resources.displayMetrics.density
                val sizeInPx = (sizeInDp * scale + 0.5f).toInt()
                layoutParams = FrameLayout.LayoutParams(sizeInPx, sizeInPx)
                scaleType = ImageView.ScaleType.CENTER_CROP
                contentDescription = "Libro ${index + 1}"
                // Carga la imagen circular utilizando Glide
                Glide.with(this@MapaInteractivoActivity)
                    .load(libroImagenes.getOrElse(index) { R.drawable.portada_elcamino })
                    .apply(RequestOptions.circleCropTransform())
                    .into(this)
                setOnClickListener {
                    abrirDetallesLibro(index + 1)
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
            val coordNormalizada = libroCoordenadasNormalizadas[index]
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
}
