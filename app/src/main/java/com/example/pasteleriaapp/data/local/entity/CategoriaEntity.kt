package com.example.pasteleriaapp.data.local.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.example.pasteleriaapp.domain.model.Categoria

@Entity(tableName = "categoria")

data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true)
    val idCategoria: Int = 0,
    val nombreCategoria: String,
    val imagenCategoria: String,
    @ColumnInfo(defaultValue = "0")
    val estaBloqueada: Boolean = false
)

fun CategoriaEntity.toCategoria() = Categoria(
    idCategoria = idCategoria,
    nombreCategoria = nombreCategoria,
    imagenCategoria = imagenCategoria,
    estaBloqueada = estaBloqueada
)

fun Categoria.toCategoriaEntity() = CategoriaEntity(
    idCategoria = idCategoria,
    nombreCategoria = nombreCategoria,
    imagenCategoria = imagenCategoria,
    estaBloqueada = estaBloqueada

)
