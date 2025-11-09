package com.example.pasteleriaapp.domain.model

data class CarritoItem(
    val idCarrito: Int = 0,
    val idProducto: Int,
    val nombreProducto: String,
    val precioProducto: Double,
    val imagenProducto: String,
    val cantidad: Int,
    val mensajePersonalizado: String
)