package com.example.pasteleriaapp.data.repository

import com.example.pasteleriaapp.data.local.dao.ProductoDao
import com.example.pasteleriaapp.data.local.entity.toProducto
import com.example.pasteleriaapp.data.local.entity.toProductoEntity
import com.example.pasteleriaapp.data.remote.api.PasteleriaApiService
import com.example.pasteleriaapp.data.remote.mapper.toDomain
import com.example.pasteleriaapp.data.remote.mapper.toEntity
import com.example.pasteleriaapp.data.remote.mapper.toRemoteDto
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.domain.repository.ProductoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class ProductoRepositoryImpl(
    private val productoDao: ProductoDao,
    private val apiService: PasteleriaApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductoRepository {

    override fun obtenerProductos(): Flow<List<Producto>> {
        return productoDao.obtenerProductos()
            .onStart { syncProductos() }
            .map { entities -> entities.map { it.toProducto() } }
    }

    override suspend fun obtenerProductoPorId(idProducto: Int): Producto? {
        val local = productoDao.obtenerProductoPorId(idProducto)
        if (local != null) return local.toProducto()
        return runCatching { apiService.obtenerProductoPorId(idProducto) }
            .getOrNull()
            ?.also { productoDao.insertarProducto(it.toEntity()) }
            ?.toDomain()
    }

    override fun obtenerProductosPorCategoria(idCategoria: Int): Flow<List<Producto>> {
        return productoDao.obtenerProductosPorCategoria(idCategoria)
            .onStart { syncProductosPorCategoria(idCategoria, admin = false) }
            .map { list -> list.map { it.toProducto() } }
    }

    override fun obtenerProductosPorCategoriaAdmin(idCategoria: Int): Flow<List<Producto>> {
        return productoDao.obtenerProductosPorCategoriaAdmin(idCategoria)
            .onStart { syncProductosPorCategoria(idCategoria, admin = true) }
            .map { list -> list.map { it.toProducto() } }
    }

    override suspend fun insertarProducto(producto: Producto) {
        val remote = apiService.crearProducto(producto.toRemoteDto())
        productoDao.insertarProducto(remote.toEntity())
    }

    override suspend fun insertarProductos(productos: List<Producto>) {
        val entities = productos.map { it.toProductoEntity() }
        productoDao.insertarProductos(entities)
    }

    override suspend fun actualizarProducto(producto: Producto) {
        val remote = apiService.actualizarProducto(producto.idProducto, producto.toRemoteDto())
        productoDao.insertarProducto(remote.toEntity())
    }

    override suspend fun eliminarProducto(producto: Producto) {
        apiService.eliminarProducto(producto.idProducto)
        productoDao.eliminarProducto(producto.toProductoEntity())
    }

    override suspend fun eliminarProductos(productos: List<Producto>) {
        val entities = productos.map { it.toProductoEntity() }
        productoDao.eliminarProductos(entities)
    }

    override suspend fun eliminarTodasLosProductos() {
        productoDao.eliminarTodosLosProductos()
    }

    override suspend fun actualizarEstadoBloqueo(idProducto: Int, estaBloqueado: Boolean) {
        val remote = apiService.actualizarEstadoProducto(idProducto, estaBloqueado)
        productoDao.insertarProducto(remote.toEntity())
    }

    private suspend fun syncProductos() = withContext(ioDispatcher) {
        runCatching {
            val remote = apiService.obtenerProductos()
            productoDao.insertarProductos(remote.map { it.toEntity() })
        }
    }

    private suspend fun syncProductosPorCategoria(idCategoria: Int, admin: Boolean) = withContext(ioDispatcher) {
        runCatching {
            val remote = if (admin) {
                apiService.obtenerProductosPorCategoriaAdmin(idCategoria)
            } else {
                apiService.obtenerProductosPorCategoria(idCategoria)
            }
            if (remote.isNotEmpty()) {
                productoDao.insertarProductos(remote.map { it.toEntity() })
            }
        }
    }
}