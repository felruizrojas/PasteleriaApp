package com.example.pasteleriaapp.data.repository

import androidx.room.withTransaction
import com.example.pasteleriaapp.data.local.AppDatabase
import com.example.pasteleriaapp.data.local.dao.CarritoDao
import com.example.pasteleriaapp.data.local.dao.PedidoDao
import com.example.pasteleriaapp.data.local.entity.PedidoProductoEntity
import com.example.pasteleriaapp.data.local.entity.toPedido
import com.example.pasteleriaapp.data.local.entity.toPedidoEntity
import com.example.pasteleriaapp.data.local.entity.toPedidoProducto
import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.model.Pedido
import com.example.pasteleriaapp.domain.model.PedidoProducto
import com.example.pasteleriaapp.domain.repository.PedidoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PedidoRepositoryImpl(
    private val db: AppDatabase // Pedimos la BD completa para la transacción
) : PedidoRepository {

    // Obtenemos los DAOs desde la BD
    private val pedidoDao = db.pedidoDao()
    private val carritoDao = db.carritoDao()

    override suspend fun crearPedido(pedido: Pedido, items: List<CarritoItem>) {
        // ¡TRANSACCIÓN! Si algo falla, todo se revierte.
        db.withTransaction {
            // 1. Insertar el pedido y obtener su nuevo ID
            val idPedido = pedidoDao.insertarPedido(pedido.toPedidoEntity()).toInt()

            // 2. Mapear los items del carrito a items de pedido
            val pedidoProductos = items.map { item ->
                PedidoProductoEntity(
                    idPedido = idPedido, // <-- Usamos el nuevo ID
                    idProducto = item.idProducto,
                    nombreProducto = item.nombreProducto,
                    precioProducto = item.precioProducto,
                    imagenProducto = item.imagenProducto,
                    cantidad = item.cantidad,
                    mensajePersonalizado = item.mensajePersonalizado
                )
            }

            // 3. Insertar todos los productos del pedido
            pedidoDao.insertarPedidoProductos(pedidoProductos)

            // 4. Limpiar el carrito
            carritoDao.limpiarCarrito()
        }
    }

    override fun obtenerPedidosPorUsuario(idUsuario: Int): Flow<List<Pedido>> {
        return pedidoDao.obtenerPedidosPorUsuario(idUsuario).map { entities ->
            entities.map { it.toPedido() }
        }
    }

    override suspend fun obtenerDetallePedido(idPedido: Int): Pair<Pedido?, List<PedidoProducto>> {
        val pedido = pedidoDao.obtenerPedidoPorId(idPedido)?.toPedido()
        val productos = pedidoDao.obtenerProductosPorPedidoId(idPedido).map { it.toPedidoProducto() }
        return Pair(pedido, productos)
    }
}