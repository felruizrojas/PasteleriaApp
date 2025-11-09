package com.example.pasteleriaapp.domain.repository

import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.model.Pedido
import com.example.pasteleriaapp.domain.model.PedidoProducto
import kotlinx.coroutines.flow.Flow

interface PedidoRepository {

    /**
     * Proceso de "Checkout". Guarda el pedido, sus productos, y limpia el carrito.
     * Se debe ejecutar en una transacción.
     */
    suspend fun crearPedido(pedido: Pedido, items: List<CarritoItem>)

    fun obtenerPedidosPorUsuario(idUsuario: Int): Flow<List<Pedido>>

    suspend fun obtenerDetallePedido(idPedido: Int): Pair<Pedido?, List<PedidoProducto>>
}