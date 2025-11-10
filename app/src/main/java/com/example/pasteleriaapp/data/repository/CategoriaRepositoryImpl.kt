package com.example.pasteleriaapp.data.repository

import com.example.pasteleriaapp.data.local.dao.CategoriaDao
import com.example.pasteleriaapp.data.local.entity.toCategoria
import com.example.pasteleriaapp.data.local.entity.toCategoriaEntity
import com.example.pasteleriaapp.domain.model.Categoria
import com.example.pasteleriaapp.domain.repository.CategoriaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoriaRepositoryImpl (
    private val categoriaDao: CategoriaDao
) : CategoriaRepository {

    override fun obtenerCategorias(): Flow<List<Categoria>> {
        return categoriaDao.obtenerCategorias()
            .map { entities ->
                entities.map { it.toCategoria() }
            }
    }

    override fun obtenerCategoriasAdmin(): Flow<List<Categoria>> {
        return categoriaDao.obtenerCategoriasAdmin()
            .map { entities ->
                entities.map { it.toCategoria() }
            }
    }

    override suspend fun obtenerCategoriaPorId(idCategoria: Int): Categoria? {
        return categoriaDao.obtenerCategoriaPorId(idCategoria)?.toCategoria()
    }

    override suspend fun insertarCategoria(categoria: Categoria) {
        categoriaDao.insertarCategoria(categoria.toCategoriaEntity())
    }

    override suspend fun insertarCategorias(categorias: List<Categoria>) {
        val entities = categorias.map { it.toCategoriaEntity() }
        categoriaDao.insertarCategorias(entities)
    }

    override suspend fun actualizarCategoria(categoria: Categoria) {
        categoriaDao.actualizarCategoria(categoria.toCategoriaEntity())
    }

    override suspend fun eliminarCategoria(categoria: Categoria) {
        categoriaDao.eliminarCategoria(categoria.toCategoriaEntity())
    }

    override suspend fun eliminarTodasLasCategorias() {
        categoriaDao.eliminarTodasLasCategorias()
    }

    override suspend fun actualizarEstadoBloqueo(idCategoria: Int, estaBloqueada: Boolean) {
        categoriaDao.actualizarEstadoBloqueo(idCategoria, estaBloqueada)
    }
}
