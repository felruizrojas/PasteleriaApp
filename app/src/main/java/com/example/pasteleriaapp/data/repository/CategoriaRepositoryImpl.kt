package com.example.pasteleriaapp.data.repository

import com.example.pasteleriaapp.data.local.dao.CategoriaDao
import com.example.pasteleriaapp.data.local.entity.toCategoria
import com.example.pasteleriaapp.data.local.entity.toCategoriaEntity
import com.example.pasteleriaapp.data.remote.api.PasteleriaApiService
import com.example.pasteleriaapp.data.remote.api.ensureRemoteImagePath
import com.example.pasteleriaapp.data.remote.mapper.toDomain
import com.example.pasteleriaapp.data.remote.mapper.toEntity
import com.example.pasteleriaapp.data.remote.mapper.toRemoteDto
import com.example.pasteleriaapp.domain.model.Categoria
import com.example.pasteleriaapp.domain.repository.CategoriaRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class CategoriaRepositoryImpl(
    private val categoriaDao: CategoriaDao,
    private val apiService: PasteleriaApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoriaRepository {

    override fun obtenerCategorias(): Flow<List<Categoria>> {
        return categoriaDao.obtenerCategorias()
            .onStart { syncCategorias(onlyPublic = true) }
            .map { entities -> entities.map { it.toCategoria() } }
    }

    override fun obtenerCategoriasAdmin(): Flow<List<Categoria>> {
        return categoriaDao.obtenerCategoriasAdmin()
            .onStart { syncCategorias(onlyPublic = false) }
            .map { entities -> entities.map { it.toCategoria() } }
    }

    override suspend fun obtenerCategoriaPorId(idCategoria: Int): Categoria? {
        return categoriaDao.obtenerCategoriaPorId(idCategoria)?.toCategoria()
    }

    override suspend fun insertarCategoria(categoria: Categoria) {
        val resolved = categoria.copy(
            imagenCategoria = apiService.ensureRemoteImagePath(categoria.imagenCategoria, "categorias")
        )
        val remote = apiService.crearCategoria(resolved.toRemoteDto())
        categoriaDao.insertarCategoria(remote.toEntity())
    }

    override suspend fun insertarCategorias(categorias: List<Categoria>) {
        // Utilizado solo para el seeding local inicial.
        val entities = categorias.map { it.toCategoriaEntity() }
        categoriaDao.insertarCategorias(entities)
    }

    override suspend fun actualizarCategoria(categoria: Categoria) {
        val resolved = categoria.copy(
            imagenCategoria = apiService.ensureRemoteImagePath(categoria.imagenCategoria, "categorias")
        )
        val remote = apiService.actualizarCategoria(categoria.idCategoria, resolved.toRemoteDto())
        categoriaDao.insertarCategoria(remote.toEntity())
    }

    override suspend fun eliminarCategoria(categoria: Categoria) {
        apiService.eliminarCategoria(categoria.idCategoria)
        categoriaDao.eliminarCategoria(categoria.toCategoriaEntity())
    }

    override suspend fun eliminarTodasLasCategorias() {
        categoriaDao.eliminarTodasLasCategorias()
    }

    override suspend fun actualizarEstadoBloqueo(idCategoria: Int, estaBloqueada: Boolean) {
        val remote = apiService.actualizarEstadoCategoria(idCategoria, estaBloqueada)
        categoriaDao.insertarCategoria(remote.toEntity())
    }

    private suspend fun syncCategorias(onlyPublic: Boolean) = withContext(ioDispatcher) {
        runCatching {
            val remote = if (onlyPublic) {
                apiService.obtenerCategoriasPublicas()
            } else {
                apiService.obtenerCategoriasAdmin()
            }
            categoriaDao.insertarCategorias(remote.map { it.toEntity() })
        }
    }
}
