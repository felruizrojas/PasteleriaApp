package com.example.pasteleriaapp.data.remote.dto

data class CategoriaDto(
    val idCategoria: Int? = null,
    val nombreCategoria: String,
    val imagenCategoria: String,
    val estaBloqueada: Boolean = false
)
