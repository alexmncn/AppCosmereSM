package com.example.appsandersonsm.MySQL

import android.content.ContentValues
import android.content.Context
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

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_LIBROS_TABLE = ("CREATE TABLE $TABLE_LIBROS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY,"
                + "$COLUMN_NOMBRE_LIBRO TEXT,"
                + "$COLUMN_NOMBRE_SAGA TEXT,"
                + "$COLUMN_NOMBRE_PORTADA TEXT,"
                + "$COLUMN_PROGRESO INTEGER)")
        db?.execSQL(CREATE_LIBROS_TABLE)
    }

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


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_LIBROS")
        onCreate(db)
    }

    // Método para insertar o actualizar un libro
    fun insertOrUpdateLibro(libro: Libro) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ID, libro.id)
        values.put(COLUMN_NOMBRE_LIBRO, libro.nombreLibro)
        values.put(COLUMN_NOMBRE_SAGA, libro.nombreSaga)
        values.put(COLUMN_NOMBRE_PORTADA, libro.nombrePortada)
        values.put(COLUMN_PROGRESO, libro.progreso)

        val rows = db.update(TABLE_LIBROS, values, "$COLUMN_ID = ?", arrayOf(libro.id.toString()))
        if (rows == 0) {
            db.insert(TABLE_LIBROS, null, values)
        }
        db.close()
    }

    // Método para obtener un libro por ID
    fun getLibroById(id: Int): Libro? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_LIBROS,
            arrayOf(COLUMN_ID, COLUMN_NOMBRE_LIBRO, COLUMN_NOMBRE_SAGA, COLUMN_NOMBRE_PORTADA, COLUMN_PROGRESO),
            "$COLUMN_ID=?",
            arrayOf(id.toString()),
            null, null, null, null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val libro = Libro(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                nombreLibro = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE_LIBRO)),
                nombreSaga = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE_SAGA)),
                nombrePortada = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE_PORTADA)),
                progreso = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROGRESO))
            )
            cursor.close()
            db.close()
            return libro
        }
        cursor?.close()
        db.close()
        return null
    }

    // Otros métodos CRUD pueden ser añadidos aquí
}
