package com.example.pasteleriaapp.domain.model

// Esto almacena una "copia" del producto en el momento de la compra
data class PedidoProducto(
    val idPedidoProducto: Int = 0,
    val idPedido: Int,
    val idProducto: Int,
    val nombreProducto: String,
    val precioProducto: Double, // Precio al momento de la compra
    val imagenProducto: String,
    val cantidad: Int,
    val mensajePersonalizado: String
)