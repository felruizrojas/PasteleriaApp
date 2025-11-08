package com.example.pasteleriaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pasteleriaapp.domain.model.Categoria
import com.example.pasteleriaapp.domain.model.Producto

@Entity(tableName = "categoria")

data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true)
    val idCategoria: Int = 0,
    val nombreCategoria: String
)

fun CategoriaEntity.toCategoria() = Categoria(
    idCategoria = idCategoria,
    nombreCategoria = nombreCategoria
)

fun Categoria.toCategoriaEntity() = CategoriaEntity(
    idCategoria = idCategoria,
    nombreCategoria = nombreCategoria
)
