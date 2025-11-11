package com.example.pasteleriaapp.data.repository

import androidx.room.withTransaction
import com.example.pasteleriaapp.data.local.AppDatabase
import com.example.pasteleriaapp.data.local.entity.PedidoProductoEntity
import com.example.pasteleriaapp.data.local.entity.toPedido
import com.example.pasteleriaapp.data.local.entity.toPedidoEntity
import com.example.pasteleriaapp.data.local.entity.toPedidoProducto
import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.model.EstadoPedido
import com.example.pasteleriaapp.domain.model.Pedido
import com.example.pasteleriaapp.domain.model.PedidoProducto
import com.example.pasteleriaapp.domain.repository.PedidoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PedidoRepositoryImpl(
    private val db: AppDatabase // Pedimos la BD completa para la transacción
) : PedidoRepository {

    private val pedidoDao = db.pedidoDao()
    private val carritoDao = db.carritoDao()

    override suspend fun crearPedido(pedido: Pedido, items: List<CarritoItem>): Long {
        return db.withTransaction {
            // 1. Insertar el pedido y obtener su nuevo ID (Long)
            val idPedidoLong = pedidoDao.insertarPedido(pedido.toPedidoEntity())

            // 2. Mapear los items del carrito a items de pedido (idPedido necesita ser Int en la entidad)
            val idPedidoInt = idPedidoLong.toInt()
            val pedidoProductos = items.map { item ->
                PedidoProductoEntity(
                    idPedido = idPedidoInt,
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

            // 5. Devolver el ID generado
            idPedidoLong
        }
    }

    override fun obtenerPedidosPorUsuario(idUsuario: Int): Flow<List<Pedido>> {
        return pedidoDao.obtenerPedidosPorUsuario(idUsuario).map { entities ->
            entities.map { it.toPedido() }
        }
    }

    override fun obtenerTodosLosPedidos(): Flow<List<Pedido>> {
        return pedidoDao.obtenerTodosLosPedidos().map { entities ->
            entities.map { it.toPedido() }
        }
    }

    override suspend fun obtenerDetallePedido(idPedido: Int): Pair<Pedido?, List<PedidoProducto>> {
        val pedido = pedidoDao.obtenerPedidoPorId(idPedido)?.toPedido()
        val productos = pedidoDao.obtenerProductosPorPedidoId(idPedido).map { it.toPedidoProducto() }
        return Pair(pedido, productos)
    }

    override suspend fun actualizarEstadoPedido(idPedido: Int, estado: EstadoPedido) {
        pedidoDao.actualizarEstadoPedido(idPedido, estado)
    }
}