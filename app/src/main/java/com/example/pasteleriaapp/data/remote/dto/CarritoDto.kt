package com.example.pasteleriaapp.data.remote.dto

data class CarritoItemDto(
    val idCarrito: Int,
    val idUsuario: Int,
    val idProducto: Int,
    val nombreProducto: String,
    val precioProducto: Double,
    val imagenProducto: String,
    val cantidad: Int,
    val mensajePersonalizado: String
)

data class CarritoItemPayloadDto(
    val idProducto: Int,
    val nombreProducto: String,
    val precioProducto: Double,
    val imagenProducto: String,
    val cantidad: Int,
    val mensajePersonalizado: String?
)

data class ActualizarCantidadCarritoDto(
    val cantidad: Int
)

data class ActualizarMensajeCarritoDto(
    val mensajePersonalizado: String?
)
