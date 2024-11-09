package com.example.appsandersonsm.Modelo

data class Libro(
    val id: Int,
    val nombreLibro: String,
    val nombreSaga: String,
    val nombrePortada: String,
    var progreso: Int,
    var totalPaginas: Int // Nueva propiedad para el total de páginas
)
