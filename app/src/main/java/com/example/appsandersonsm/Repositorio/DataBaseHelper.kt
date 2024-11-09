package com.example.appsandersonsm.Repositorio

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.appsandersonsm.Modelo.Libro

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "libros.db"
        private const val DATABASE_VERSION = 1

        // Nombre de la tabla y columnas
        const val TABLE_LIBROS = "libros"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE_LIBRO = "nombreLibro"
        const val COLUMN_NOMBRE_SAGA = "nombreSaga"
        const val COLUMN_NOMBRE_PORTADA = "nombrePortada"
        const val COLUMN_PROGRESO = "progreso"
    }

    private val jsonHandler = JsonHandler(context) // Instancia de JsonHandler para leer JSON

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_LIBROS_TABLE = ("CREATE TABLE $TABLE_LIBROS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY,"
                + "$COLUMN_NOMBRE_LIBRO TEXT,"
                + "$COLUMN_NOMBRE_SAGA TEXT,"
                + "$COLUMN_NOMBRE_PORTADA TEXT,"
                + "$COLUMN_PROGRESO INTEGER)")
        db?.execSQL(CREATE_LIBROS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_LIBROS")
        onCreate(db)
    }

    // Método para cargar libros desde JSON si la base de datos está vacía (solo la primera vez)
    fun cargarDatosInicialesDesdeJson() {
        if (isDatabaseEmpty()) {
            jsonHandler.copiarJsonDesdeAssetsSiNoExiste() // Asegura que el JSON está en almacenamiento interno
            val libros = jsonHandler.cargarLibrosDesdeJson() // Carga la lista de libros desde JSON
            insertarLibros(libros)
        }
    }

    // Verifica si la base de datos está vacía
    private fun isDatabaseEmpty(): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_LIBROS", null)
        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        db.close()
        return count == 0
    }

    // Inserta la lista de libros en la base de datos (sin sobrescribir)
    private fun insertarLibros(libros: List<Libro>) {
        val db = this.writableDatabase
        libros.forEach { libro ->
            val values = ContentValues().apply {
                put(COLUMN_ID, libro.id)
                put(COLUMN_NOMBRE_LIBRO, libro.nombreLibro)
                put(COLUMN_NOMBRE_SAGA, libro.nombreSaga)
                put(COLUMN_NOMBRE_PORTADA, libro.nombrePortada)
                put(COLUMN_PROGRESO, libro.progreso)
            }
            db.insert(TABLE_LIBROS, null, values)
        }
        db.close()
    }

    // Método para actualizar solo un libro en la base de datos
    fun actualizarProgresoLibro(libro: Libro) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PROGRESO, libro.progreso)
        }
        db.update(TABLE_LIBROS, values, "$COLUMN_ID = ?", arrayOf(libro.id.toString()))
        db.close()
    }

    // Método para obtener un libro por ID
    fun getLibroById(id: Int): Libro? {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_LIBROS,
            arrayOf(COLUMN_ID, COLUMN_NOMBRE_LIBRO, COLUMN_NOMBRE_SAGA, COLUMN_NOMBRE_PORTADA, COLUMN_PROGRESO),
            "$COLUMN_ID=?",
            arrayOf(id.toString()),
            null, null, null, null
        )

        var libro: Libro? = null
        if (cursor.moveToFirst()) {
            libro = Libro(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                nombreLibro = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE_LIBRO)),
                nombreSaga = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE_SAGA)),
                nombrePortada = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE_PORTADA)),
                progreso = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROGRESO))
            )
        }
        cursor.close()
        db.close()
        return libro
    }
    fun getAllSagas(): List<String> {
        val sagas = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT $COLUMN_NOMBRE_SAGA FROM $TABLE_LIBROS", null)
        if (cursor.moveToFirst()) {
            do {
                val saga = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE_SAGA))
                sagas.add(saga)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return sagas
    }
    // Método para obtener todos los libros desde la base de datos
    fun getAllLibros(): List<Libro> {
        val libros = mutableListOf<Libro>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_LIBROS", null)
        if (cursor.moveToFirst()) {
            do {
                val libro = Libro(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    nombreLibro = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE_LIBRO)),
                    nombreSaga = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE_SAGA)),
                    nombrePortada = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE_PORTADA)),
                    progreso = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROGRESO))
                )
                libros.add(libro)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return libros
    }

}
