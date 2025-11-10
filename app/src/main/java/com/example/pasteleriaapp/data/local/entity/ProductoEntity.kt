package com.example.pasteleriaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pasteleriaapp.domain.model.Producto

@Entity(tableName = "producto")

data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val idProducto: Int = 0,
    val idCategoria: Int,
    val codigoProducto: String,
    val nombreProducto: String,
    val precioProducto: Double,
    val descripcionProducto: String,
    val imagenProducto: String,
    val stockProducto: Int,
    val stockCriticoProducto: Int,
    @ColumnInfo(defaultValue = "0")
    val estaBloqueado: Boolean = false
)

fun ProductoEntity.toProducto() = Producto(
    idProducto = idProducto,
    idCategoria = idCategoria,
    codigoProducto = codigoProducto,
    nombreProducto = nombreProducto,
    precioProducto = precioProducto,
    descripcionProducto = descripcionProducto,
    imagenProducto = imagenProducto,
    stockProducto = stockProducto,
    stockCriticoProducto = stockCriticoProducto,
    estaBloqueado = estaBloqueado
)

fun Producto.toProductoEntity() = ProductoEntity(
    idProducto = idProducto,
    idCategoria = idCategoria,
    codigoProducto = codigoProducto,
    nombreProducto = nombreProducto,
    precioProducto = precioProducto,
    descripcionProducto = descripcionProducto,
    imagenProducto = imagenProducto,
    stockProducto = stockProducto,
    stockCriticoProducto = stockCriticoProducto,
    estaBloqueado = estaBloqueado
)
