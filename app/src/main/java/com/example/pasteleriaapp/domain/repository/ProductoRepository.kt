package com.example.pasteleriaapp.domain.repository

import com.example.pasteleriaapp.domain.model.Producto
import kotlinx.coroutines.flow.Flow

interface ProductoRepository {
    fun obtenerProductos(): Flow<List<Producto>>

    fun obtenerProductosPorCategoria(idCategoria: Int): Flow<List<Producto>>

    fun obtenerProductosPorCategoriaAdmin(idCategoria: Int): Flow<List<Producto>>

    suspend fun obtenerProductoPorId(idProducto: Int): Producto?

    suspend fun insertarProducto(producto: Producto)

    suspend fun insertarProductos(productos: List<Producto>)

    suspend fun actualizarProducto(producto: Producto)

    suspend fun eliminarProducto(producto: Producto)

    suspend fun eliminarProductos(productos: List<Producto>)

    suspend fun eliminarTodasLosProductos()

    suspend fun actualizarEstadoBloqueo(idProducto: Int, estaBloqueado: Boolean)
}
