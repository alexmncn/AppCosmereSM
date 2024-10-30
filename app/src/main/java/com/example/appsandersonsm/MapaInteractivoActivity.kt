package com.example.appsandersonsm

import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.appsandersonsm.R
import io.getstream.photoview.PhotoView

class MapaInteractivoActivity : AppCompatActivity() {

    private lateinit var photoView: PhotoView
    private lateinit var markerContainer: FrameLayout

    // Coordenadas originales en la imagen del mapa
    private val libroCoordenadas = listOf(
        // Sumar +1000 a to_do para que se vea mas centrado
        PointF(2650f, 518f),      // Coordenadas para el El alma del emperador
        PointF(1950f, 318f),  // Coordenadas para el Elantris
        PointF(1800f, 921f),   // Coordenadas para el La esperanza de Elantris
        PointF(2000f,1800f), // libro Nacidos de la bruma
        PointF(2100f, 2300f), // libro Pozo de la ascensión
        PointF(2200f, 2800f), // libro El héreo de las eras
        PointF(1950f, 3500f), // libro El aliento de los dioses
        PointF(2800f, 3600f), // El camino de los reyes
        PointF(3400f, 3900f), // Palabras Radiantes
        PointF(4200f, 4100f), // Juramentada
        PointF(5000f, 3900f), // El ritmo de la guerra
        PointF(5800f, 3000f), // Trenza
        PointF(6500f, 1500f), // Yumi




    )

    // Colores en formato de cadena HEX para cada marcador
    private val libroColores = listOf(
        "#FF0000",  // Rojo para el libro 1
        "#0000FF",  // Azul para el libro 2
        "#00FF00",   // Verde para el libro 3
        "#FF1493", // libro 4
        "#FF4500", // libro 5
        "#FFD700", // libro 6
        "#FF0000", // libro 7 el aliento de los dioses
        "#0000FF",  // Azul para el libro Camino de los reyes
        "#FF1493",  // Azul para el libro Palabras radiante
        "#00FF00",   // Verde para el libro Juramentada
        "#FF0000", // libro Ritmo de guerra
        "#FF4500", // libro Trenza
        "#FFD700", // libro Yumi



    )

    private val markers = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_interactivo)

        photoView = findViewById(R.id.photoView)
        markerContainer = findViewById(R.id.mapContainer)

        // Añadir marcadores en posiciones específicas en el mapa con colores personalizados
        libroCoordenadas.forEachIndexed { index, coord ->
            val marker = View(this).apply {
                layoutParams = FrameLayout.LayoutParams(50, 50)
                setBackgroundColor(Color.parseColor(libroColores[index]))  // Convierte el color de cadena a entero
                contentDescription = "Libro ${index + 1}"
                setOnClickListener {
                    abrirDetallesLibro(index + 1)
                }
            }
            markers.add(marker)
            markerContainer.addView(marker)
        }

        // Configura el listener para actualizar los marcadores en función del zoom y desplazamiento
        photoView.setOnMatrixChangeListener { matrixRect ->
            if (matrixRect != null) {
                actualizarMarcadores(matrixRect)
            }
        }
    }

    private fun actualizarMarcadores(matrixRect: RectF) {
        // Obtén la matriz de transformación actual del PhotoView
        val matrix = FloatArray(9)
        photoView.imageMatrix.getValues(matrix)

        val scaleX = matrix[Matrix.MSCALE_X]
        val scaleY = matrix[Matrix.MSCALE_Y]
        val transX = matrix[Matrix.MTRANS_X]
        val transY = matrix[Matrix.MTRANS_Y]

        // Actualiza la posición de cada marcador en función de la transformación actual
        libroCoordenadas.forEachIndexed { index, coord ->
            val marker = markers[index]
            val newX = coord.x * scaleX + transX
            val newY = coord.y * scaleY + transY
            marker.x = newX
            marker.y = newY
        }
    }

    private fun abrirDetallesLibro(libroId: Int) {
        // Aquí puedes iniciar una actividad de detalles o mostrar un diálogo con la información del libro
    }
}
