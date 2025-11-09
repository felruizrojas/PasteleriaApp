package com.example.pasteleriaapp.domain.repository

import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.model.Producto
import kotlinx.coroutines.flow.Flow

interface CarritoRepository {
    fun obtenerItemsCarrito(): Flow<List<CarritoItem>>

    suspend fun agregarAlCarrito(producto: Producto, cantidad: Int, mensaje: String)

    suspend fun actualizarCantidadItem(item: CarritoItem, nuevaCantidad: Int)

    suspend fun eliminarItem(item: CarritoItem)

    suspend fun limpiarCarrito()
}