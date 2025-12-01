package com.example.pasteleriaapp.data.repository

import androidx.room.withTransaction
import com.example.pasteleriaapp.data.local.AppDatabase
import com.example.pasteleriaapp.data.local.entity.toPedido
import com.example.pasteleriaapp.data.local.entity.toPedidoProducto
import com.example.pasteleriaapp.data.remote.api.PasteleriaApiService
import com.example.pasteleriaapp.data.remote.dto.ActualizarEstadoPedidoDto
import com.example.pasteleriaapp.data.remote.dto.CrearPedidoPayloadDto
import com.example.pasteleriaapp.data.remote.dto.PedidoDto
import com.example.pasteleriaapp.data.remote.mapper.toEntity
import com.example.pasteleriaapp.data.remote.mapper.toPedidoEntity
import com.example.pasteleriaapp.data.remote.mapper.toPedidoProductoRequestDto
import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.model.EstadoPedido
import com.example.pasteleriaapp.domain.model.Pedido
import com.example.pasteleriaapp.domain.model.PedidoProducto
import com.example.pasteleriaapp.domain.repository.PedidoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class PedidoRepositoryImpl(
    private val db: AppDatabase,
    private val apiService: PasteleriaApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PedidoRepository {

    private val pedidoDao = db.pedidoDao()
    private val carritoDao = db.carritoDao()

    override suspend fun crearPedido(pedido: Pedido, items: List<CarritoItem>): Long = withContext(ioDispatcher) {
        val payload = CrearPedidoPayloadDto(
            idUsuario = pedido.idUsuario,
            fechaEntregaPreferida = pedido.fechaEntregaPreferida,
            productos = items.map { it.toPedidoProductoRequestDto() }
        )

        val response = apiService.crearPedido(payload)
        db.withTransaction {
            cacheSinglePedido(response)
            carritoDao.limpiarCarrito(pedido.idUsuario)
        }
        response.idPedido.toLong()
    }

    override fun obtenerPedidosPorUsuario(idUsuario: Int): Flow<List<Pedido>> {
        return pedidoDao.obtenerPedidosPorUsuario(idUsuario)
            .onStart { syncPedidosUsuario(idUsuario) }
            .map { entities -> entities.map { it.toPedido() } }
    }

    override fun obtenerTodosLosPedidos(): Flow<List<Pedido>> {
        return pedidoDao.obtenerTodosLosPedidos()
            .onStart { syncPedidosAdmin() }
            .map { entities -> entities.map { it.toPedido() } }
    }

    override suspend fun obtenerDetallePedido(idPedido: Int): Pair<Pedido?, List<PedidoProducto>> = withContext(ioDispatcher) {
        val localPedido = pedidoDao.obtenerPedidoPorId(idPedido)
        if (localPedido != null) {
            runCatching { syncPedidosUsuario(localPedido.idUsuario) }
        } else {
            runCatching { syncPedidosAdmin() }
        }
        val pedido = pedidoDao.obtenerPedidoPorId(idPedido)?.toPedido()
        val productos = pedidoDao.obtenerProductosPorPedidoId(idPedido).map { it.toPedidoProducto() }
        pedido to productos
    }

    override suspend fun actualizarEstadoPedido(idPedido: Int, estado: EstadoPedido) = withContext(ioDispatcher) {
        val response = apiService.actualizarEstadoPedido(idPedido, ActualizarEstadoPedidoDto(estado))
        db.withTransaction { cacheSinglePedido(response) }
    }

    private suspend fun syncPedidosUsuario(idUsuario: Int) = withContext(ioDispatcher) {
        runCatching {
            val remote = apiService.obtenerPedidosUsuario(idUsuario)
            db.withTransaction {
                depurarPedidosObsoletos(
                    remoteIds = remote.map { it.idPedido },
                    existentes = pedidoDao.obtenerIdsPedidosPorUsuario(idUsuario)
                )
                cachePedidos(remote)
            }
        }
    }

    private suspend fun syncPedidosAdmin() = withContext(ioDispatcher) {
        runCatching {
            val remote = apiService.obtenerPedidos()
            db.withTransaction {
                depurarPedidosObsoletos(
                    remoteIds = remote.map { it.idPedido },
                    existentes = pedidoDao.obtenerTodosLosIds()
                )
                cachePedidos(remote)
            }
        }
    }

    private suspend fun cachePedidos(remotePedidos: List<PedidoDto>) {
        if (remotePedidos.isEmpty()) return
        val pedidoEntities = remotePedidos.map { it.toPedidoEntity() }
        val productos = remotePedidos.flatMap { pedido ->
            pedido.productos.map { it.toEntity() }
        }
        remotePedidos.forEach { pedidoDao.eliminarProductosPorPedido(it.idPedido) }
        pedidoDao.insertarPedidos(pedidoEntities)
        if (productos.isNotEmpty()) {
            pedidoDao.insertarPedidoProductos(productos)
        }
    }

    private suspend fun cacheSinglePedido(pedidoDto: PedidoDto) {
        pedidoDao.eliminarProductosPorPedido(pedidoDto.idPedido)
        pedidoDao.insertarPedidos(listOf(pedidoDto.toPedidoEntity()))
        val productos = pedidoDto.productos.map { it.toEntity() }
        if (productos.isNotEmpty()) {
            pedidoDao.insertarPedidoProductos(productos)
        }
    }

    private suspend fun depurarPedidosObsoletos(remoteIds: List<Int>, existentes: List<Int>) {
        if (existentes.isEmpty()) return
        val idsRemotos = remoteIds.toSet()
        val idsEliminar = existentes.filterNot { idsRemotos.contains(it) }
        if (idsEliminar.isEmpty()) return
        pedidoDao.eliminarProductosPorPedidos(idsEliminar)
        pedidoDao.eliminarPedidosPorIds(idsEliminar)
    }
}