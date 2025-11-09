package com.example.pasteleriaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pasteleriaapp.domain.model.PedidoProducto

@Entity(tableName = "pedido_producto")
data class PedidoProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val idPedidoProducto: Int = 0,
    val idPedido: Int, // Clave foránea a PedidoEntity
    val idProducto: Int,
    val nombreProducto: String,
    val precioProducto: Double,
    val imagenProducto: String,
    val cantidad: Int,
    val mensajePersonalizado: String
)

fun PedidoProductoEntity.toPedidoProducto() = PedidoProducto(
    idPedidoProducto = idPedidoProducto,
    idPedido = idPedido,
    idProducto = idProducto,
    nombreProducto = nombreProducto,
    precioProducto = precioProducto,
    imagenProducto = imagenProducto,
    cantidad = cantidad,
    mensajePersonalizado = mensajePersonalizado
)

fun PedidoProducto.toPedidoProductoEntity() = PedidoProductoEntity(
    idPedidoProducto = idPedidoProducto,
    idPedido = idPedido,
    idProducto = idProducto,
    nombreProducto = nombreProducto,
    precioProducto = precioProducto,
    imagenProducto = imagenProducto,
    cantidad = cantidad,
    mensajePersonalizado = mensajePersonalizado
)