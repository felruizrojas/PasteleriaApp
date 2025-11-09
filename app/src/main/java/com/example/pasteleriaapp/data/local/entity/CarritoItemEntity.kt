package com.example.pasteleriaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pasteleriaapp.domain.model.CarritoItem

@Entity(tableName = "carrito")
data class CarritoItemEntity(
    @PrimaryKey(autoGenerate = true)
    val idCarrito: Int = 0,
    val idProducto: Int,
    val nombreProducto: String,
    val precioProducto: Double,
    val imagenProducto: String,
    val cantidad: Int,
    val mensajePersonalizado: String
)

fun CarritoItemEntity.toCarritoItem() = CarritoItem(
    idCarrito = idCarrito,
    idProducto = idProducto,
    nombreProducto = nombreProducto,
    precioProducto = precioProducto,
    imagenProducto = imagenProducto,
    cantidad = cantidad,
    mensajePersonalizado = mensajePersonalizado
)

fun CarritoItem.toCarritoItemEntity() = CarritoItemEntity(
    idCarrito = idCarrito,
    idProducto = idProducto,
    nombreProducto = nombreProducto,
    precioProducto = precioProducto,
    imagenProducto = imagenProducto,
    cantidad = cantidad,
    mensajePersonalizado = mensajePersonalizado
)