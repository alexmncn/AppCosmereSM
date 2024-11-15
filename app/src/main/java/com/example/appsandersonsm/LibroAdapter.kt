package com.example.appsandersonsm

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appsandersonsm.Modelo.Libro

class LibroAdapter(
    private val context: Context,
    private var libros: List<Libro>,
    private val onItemClick: (Libro) -> Unit
) : RecyclerView.Adapter<LibroAdapter.LibroViewHolder>() {

    // Clase interna LibroViewHolder
    inner class LibroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewPortada: ImageView = itemView.findViewById(R.id.imageViewPortada)
        val textViewNombreLibro: TextView = itemView.findViewById(R.id.textViewNombreLibro)
        val textViewNombreSaga: TextView = itemView.findViewById(R.id.textViewNombreSaga)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBarLibro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_libro, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = libros[position]
        val resID = context.resources.getIdentifier(libro.nombrePortada, "drawable", context.packageName)
        holder.imageViewPortada.setImageResource(resID)
        holder.textViewNombreLibro.text = libro.nombreLibro
        holder.textViewNombreSaga.text = libro.nombreSaga

        // Calcular el progreso como porcentaje
        val progresoPorcentaje = if (libro.totalPaginas > 0) {
            (libro.progreso * 100) / libro.totalPaginas
        } else {
            0
        }

        // Asignar el progreso calculado al ProgressBar
        holder.progressBar.max = 100
        holder.progressBar.progress = progresoPorcentaje.coerceIn(0, 100)

        // Log para depuración
        Log.d(
            "LibroAdapter",
            "Libro: ${libro.nombreLibro}, Progreso: ${libro.progreso}, Total Páginas: ${libro.totalPaginas}, Progreso Calculado: $progresoPorcentaje"
        )

        holder.itemView.setOnClickListener {
            onItemClick(libro)
        }
    }

    override fun getItemCount(): Int {
        return libros.size
    }

    // Método para actualizar la lista de libros
    fun actualizarLista(nuevaLista: List<Libro>) {
        libros = nuevaLista
        notifyDataSetChanged()
    }
}
