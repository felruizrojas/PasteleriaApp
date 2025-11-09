package com.example.pasteleriaapp.domain.model

data class Pedido(
    val idPedido: Int = 0,
    val idUsuario: Int,
    val fechaPedido: Long, // Usaremos Long para guardar la fecha como timestamp
    val fechaEntregaPreferida: String,
    val estado: EstadoPedido,
    val total: Double
)