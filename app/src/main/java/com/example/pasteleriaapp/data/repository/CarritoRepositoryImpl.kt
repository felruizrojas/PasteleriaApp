package com.example.pasteleriaapp.data.repository

import com.example.pasteleriaapp.data.local.dao.CarritoDao
import com.example.pasteleriaapp.data.local.entity.CarritoItemEntity
import com.example.pasteleriaapp.data.local.entity.toCarritoItem
import com.example.pasteleriaapp.data.local.entity.toCarritoItemEntity
import com.example.pasteleriaapp.data.remote.api.PasteleriaApiService
import com.example.pasteleriaapp.data.remote.dto.ActualizarCantidadCarritoDto
import com.example.pasteleriaapp.data.remote.dto.ActualizarMensajeCarritoDto
import com.example.pasteleriaapp.data.remote.dto.CarritoItemPayloadDto
import com.example.pasteleriaapp.data.remote.mapper.toEntity
import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.domain.repository.CarritoRepository
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class CarritoRepositoryImpl(
    private val dao: CarritoDao,
    private val apiService: PasteleriaApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CarritoRepository {

    override fun obtenerItemsCarrito(usuarioId: Int): Flow<List<CarritoItem>> {
        return dao.obtenerItemsCarrito(usuarioId)
            .onStart { syncCarrito(usuarioId) }
            .map { entities -> entities.map { it.toCarritoItem() } }
    }

    override suspend fun agregarAlCarrito(usuarioId: Int, producto: Producto, cantidad: Int, mensaje: String) {
        val mensajeClave = normalizarMensaje(mensaje)
        val payload = CarritoItemPayloadDto(
            idProducto = producto.idProducto,
            nombreProducto = producto.nombreProducto,
            precioProducto = producto.precioProducto,
            imagenProducto = producto.imagenProducto,
            cantidad = cantidad,
            mensajePersonalizado = mensajeClave.ifBlank { null }
        )

        withContext(ioDispatcher) {
            try {
                val remote = apiService.agregarAlCarrito(usuarioId, payload)
                dao.insertarItem(remote.toEntity())
            } catch (t: Throwable) {
                if (shouldFallback(t)) {
                    agregarLocalmente(usuarioId, producto, cantidad, mensajeClave)
                } else {
                    throw t
                }
            }
        }
    }

    override suspend fun actualizarCantidadItem(usuarioId: Int, item: CarritoItem, nuevaCantidad: Int) {
        if (item.usuarioId != usuarioId) return
        withContext(ioDispatcher) {
            if (nuevaCantidad <= 0) {
                eliminarItem(usuarioId, item)
                return@withContext
            }
            try {
                val remote = apiService.actualizarCantidadCarrito(
                    usuarioId,
                    item.idCarrito,
                    ActualizarCantidadCarritoDto(nuevaCantidad)
                )
                dao.insertarItem(remote.toEntity())
            } catch (t: Throwable) {
                if (shouldFallback(t)) {
                    dao.actualizarItem(item.copy(cantidad = nuevaCantidad).toCarritoItemEntity())
                } else {
                    throw t
                }
            }
        }
    }

    override suspend fun eliminarItem(usuarioId: Int, item: CarritoItem) {
        if (item.usuarioId != usuarioId) return
        withContext(ioDispatcher) {
            try {
                apiService.eliminarItemCarrito(usuarioId, item.idCarrito)
                dao.eliminarItem(item.toCarritoItemEntity())
            } catch (t: Throwable) {
                if (shouldFallback(t)) {
                    dao.eliminarItem(item.toCarritoItemEntity())
                } else {
                    throw t
                }
            }
        }
    }

    override suspend fun limpiarCarrito(usuarioId: Int) {
        withContext(ioDispatcher) {
            try {
                apiService.limpiarCarrito(usuarioId)
                dao.limpiarCarrito(usuarioId)
            } catch (t: Throwable) {
                if (shouldFallback(t)) {
                    dao.limpiarCarrito(usuarioId)
                } else {
                    throw t
                }
            }
        }
    }

    override suspend fun actualizarMensajeItem(usuarioId: Int, idCarrito: Int, nuevoMensaje: String) {
        val mensajeClave = normalizarMensaje(nuevoMensaje)
        withContext(ioDispatcher) {
            val item = dao.obtenerItemPorId(idCarrito) ?: return@withContext
            if (item.idUsuario != usuarioId) return@withContext
            if (item.mensajePersonalizado == mensajeClave) return@withContext
            try {
                val remote = apiService.actualizarMensajeCarrito(
                    usuarioId,
                    idCarrito,
                    ActualizarMensajeCarritoDto(mensajeClave.ifBlank { null })
                )
                dao.insertarItem(remote.toEntity())
            } catch (t: Throwable) {
                if (shouldFallback(t)) {
                    actualizarMensajeLocal(usuarioId, idCarrito, mensajeClave)
                } else {
                    throw t
                }
            }
        }
    }

    private suspend fun syncCarrito(usuarioId: Int) = withContext(ioDispatcher) {
        runCatching {
            val remote = apiService.obtenerCarrito(usuarioId)
            dao.limpiarCarrito(usuarioId)
            if (remote.isNotEmpty()) {
                dao.insertarItems(remote.map { it.toEntity() })
            }
        }
    }

    private suspend fun agregarLocalmente(usuarioId: Int, producto: Producto, cantidad: Int, mensajeClave: String) {
        val existente = dao.obtenerItemPorProductoYMensaje(usuarioId, producto.idProducto, mensajeClave)
        if (existente != null) {
            val actualizado = existente.copy(
                cantidad = existente.cantidad + cantidad,
                nombreProducto = producto.nombreProducto,
                precioProducto = producto.precioProducto,
                imagenProducto = producto.imagenProducto,
                mensajePersonalizado = mensajeClave
            )
            dao.actualizarItem(actualizado)
        } else {
            val nuevoItem = CarritoItem(
                usuarioId = usuarioId,
                idProducto = producto.idProducto,
                nombreProducto = producto.nombreProducto,
                precioProducto = producto.precioProducto,
                imagenProducto = producto.imagenProducto,
                cantidad = cantidad,
                mensajePersonalizado = mensajeClave
            ).toCarritoItemEntity()
            dao.insertarItem(nuevoItem)
        }
    }

    private suspend fun actualizarMensajeLocal(usuarioId: Int, idCarrito: Int, mensajeClave: String) {
        val item = dao.obtenerItemPorId(idCarrito) ?: return
        if (item.idUsuario != usuarioId) return
        val potencialDuplicado = dao.obtenerItemPorProductoYMensaje(usuarioId, item.idProducto, mensajeClave)
        if (potencialDuplicado != null && potencialDuplicado.idCarrito != item.idCarrito) {
            val actualizado = potencialDuplicado.copy(cantidad = potencialDuplicado.cantidad + item.cantidad)
            dao.actualizarItem(actualizado)
            dao.eliminarItem(item)
        } else {
            dao.actualizarMensajeItem(idCarrito, mensajeClave)
        }
    }

    private fun normalizarMensaje(mensaje: String): String = mensaje.trim()

    private fun shouldFallback(error: Throwable): Boolean {
        return error is IOException || (error is HttpException && error.code() >= 500)
    }
}