package com.example.pasteleriaapp.data.repository

import com.example.pasteleriaapp.data.local.dao.CarritoDao
import com.example.pasteleriaapp.data.local.entity.toCarritoItem
import com.example.pasteleriaapp.data.local.entity.toCarritoItemEntity
import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.domain.repository.CarritoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CarritoRepositoryImpl(
    private val dao: CarritoDao
) : CarritoRepository {

    override fun obtenerItemsCarrito(): Flow<List<CarritoItem>> {
        return dao.obtenerItemsCarrito().map { entities ->
            entities.map { it.toCarritoItem() }
        }
    }

    /**
     * Lógica principal: Revisa si el item ya existe.
     * Si existe, actualiza la cantidad.
     * Si no existe, crea un nuevo item.
     */
    override suspend fun agregarAlCarrito(producto: Producto, cantidad: Int, mensaje: String) {
        val itemExistente = dao.obtenerItemPorProductoId(producto.idProducto)

        if (itemExistente != null) {
            // Producto ya existe, actualizamos cantidad
            val itemActualizado = itemExistente.copy(
                cantidad = itemExistente.cantidad + cantidad,
                mensajePersonalizado = if (mensaje.isNotBlank()) mensaje else itemExistente.mensajePersonalizado
            )
            dao.actualizarItem(itemActualizado)
        } else {
            // Producto nuevo, lo creamos
            val nuevoItem = CarritoItem(
                idProducto = producto.idProducto,
                nombreProducto = producto.nombreProducto,
                precioProducto = producto.precioProducto,
                imagenProducto = producto.imagenProducto,
                cantidad = cantidad,
                mensajePersonalizado = mensaje
            ).toCarritoItemEntity()
            dao.insertarItem(nuevoItem)
        }
    }

    override suspend fun actualizarCantidadItem(item: CarritoItem, nuevaCantidad: Int) {
        if (nuevaCantidad <= 0) {
            eliminarItem(item)
        } else {
            val itemActualizado = item.copy(cantidad = nuevaCantidad).toCarritoItemEntity()
            dao.actualizarItem(itemActualizado)
        }
    }

    override suspend fun eliminarItem(item: CarritoItem) {
        dao.eliminarItem(item.toCarritoItemEntity())
    }

    override suspend fun limpiarCarrito() {
        dao.limpiarCarrito()
    }
}