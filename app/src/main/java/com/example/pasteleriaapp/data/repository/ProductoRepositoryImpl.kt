package com.example.pasteleriaapp.data.repository

import com.example.pasteleriaapp.data.local.dao.ProductoDao
import com.example.pasteleriaapp.data.local.entity.toProducto
import com.example.pasteleriaapp.data.local.entity.toProductoEntity
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.domain.repository.ProductoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductoRepositoryImpl (
    private val productoDao: ProductoDao
) : ProductoRepository {

    override fun obtenerProductos(): Flow<List<Producto>> {
        return productoDao.obtenerProductos()
            .map { entities ->
                entities.map { it.toProducto() }
            }
    }

    override suspend fun obtenerProductoPorId(idProducto: Int): Producto? {
        return productoDao.obtenerProductoPorId(idProducto)?.toProducto()
    }

    override fun obtenerProductosPorCategoria(idCategoria: Int): Flow<List<Producto>> {
        return productoDao.obtenerProductosPorCategoria(idCategoria)
            .map { entities ->
                entities.map { it.toProducto() }
            }
    }

    override fun obtenerProductosPorCategoriaAdmin(idCategoria: Int): Flow<List<Producto>> {
        return productoDao.obtenerProductosPorCategoriaAdmin(idCategoria)
            .map { entities ->
                entities.map { it.toProducto() }
            }
    }

    override suspend fun insertarProducto(producto: Producto) {
        productoDao.insertarProducto(producto.toProductoEntity())
    }

    override suspend fun insertarProductos(productos: List<Producto>) {
        val entities = productos.map { it.toProductoEntity() }
        productoDao.insertarProductos(entities)
    }

    override suspend fun actualizarProducto(producto: Producto) {
        productoDao.actualizarProducto(producto.toProductoEntity())
    }

    override suspend fun eliminarProducto(producto: Producto) {
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
        productoDao.actualizarEstadoBloqueo(idProducto, estaBloqueado)
    }
}