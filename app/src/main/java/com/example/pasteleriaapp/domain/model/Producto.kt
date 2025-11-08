package com.example.pasteleriaapp.domain.model

data class Producto (
    val idProducto: Int,
    val idCategoria: Int,
    val codigoProducto: String,
    val nombreProducto: String,
    val precioProducto: Double,
    val descripcionProducto: String,
    val imagenProducto: String,
    val stockProducto: Int,
    val stockCriticoProducto: Int
)
