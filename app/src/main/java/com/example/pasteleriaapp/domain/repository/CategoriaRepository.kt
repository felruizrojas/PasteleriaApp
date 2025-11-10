package com.example.pasteleriaapp.domain.repository

import com.example.pasteleriaapp.domain.model.Categoria
import kotlinx.coroutines.flow.Flow

interface CategoriaRepository {

    fun obtenerCategorias(): Flow<List<Categoria>>

    fun obtenerCategoriasAdmin(): Flow<List<Categoria>>

    suspend fun obtenerCategoriaPorId(idCategoria: Int): Categoria?

    suspend fun insertarCategoria(categoria: Categoria)

    suspend fun insertarCategorias(categorias: List<Categoria>)

    suspend fun actualizarCategoria(categoria: Categoria)

    suspend fun eliminarCategoria(categoria: Categoria)

    suspend fun eliminarTodasLasCategorias()

    suspend fun actualizarEstadoBloqueo(idCategoria: Int, estaBloqueada: Boolean)
}