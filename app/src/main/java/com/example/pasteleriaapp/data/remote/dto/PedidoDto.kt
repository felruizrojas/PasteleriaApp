package com.example.pasteleriaapp.data.remote.dto

import com.example.pasteleriaapp.domain.model.EstadoPedido

data class PedidoDto(
    val idPedido: Int,
    val idUsuario: Int,
    val fechaPedido: Long,
    val fechaEntregaPreferida: String,
    val estado: EstadoPedido,
    val total: Double,
    val productos: List<PedidoProductoDto>
)

data class PedidoProductoDto(
    val idPedidoProducto: Int,
    val idPedido: Int,
    val idProducto: Int,
    val nombreProducto: String,
    val precioProducto: Double,
    val imagenProducto: String,
    val cantidad: Int,
    val mensajePersonalizado: String
)

data class CrearPedidoPayloadDto(
    val idUsuario: Int,
    val fechaEntregaPreferida: String,
    val productos: List<PedidoProductoRequestDto>
)

data class PedidoProductoRequestDto(
    val idProducto: Int,
    val nombreProducto: String,
    val precioProducto: Double,
    val imagenProducto: String,
    val cantidad: Int,
    val mensajePersonalizado: String
)

data class ActualizarEstadoPedidoDto(
    val estado: EstadoPedido
)
