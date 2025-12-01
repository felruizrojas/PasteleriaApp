package com.example.pasteleriaapp.data.remote.dto

data class ProductoDto(
    val idProducto: Int? = null,
    val idCategoria: Int,
    val codigoProducto: String,
    val nombreProducto: String,
    val precioProducto: Double,
    val descripcionProducto: String,
    val imagenProducto: String,
    val stockProducto: Int,
    val stockCriticoProducto: Int,
    val estaBloqueado: Boolean = false
)
