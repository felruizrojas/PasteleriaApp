package com.example.pasteleriaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pasteleriaapp.data.local.entity.CategoriaEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categoria WHERE estaBloqueada = 0 ORDER BY nombreCategoria ASC")
    fun obtenerCategorias(): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM categoria ORDER BY nombreCategoria ASC")
    fun obtenerCategoriasAdmin(): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM categoria WHERE idCategoria = :idCategoria")
    suspend fun obtenerCategoriaPorId(idCategoria: Int): CategoriaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCategoria(categoria: CategoriaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCategorias(categorias: List<CategoriaEntity>)

    @Update
    suspend fun actualizarCategoria(categoria: CategoriaEntity)

    @Delete
    suspend fun eliminarCategoria(categoria: CategoriaEntity)

    @Query("DELETE FROM categoria")
    suspend fun eliminarTodasLasCategorias()

    @Query("UPDATE categoria SET estaBloqueada = :estaBloqueada WHERE idCategoria = :idCategoria")
    suspend fun actualizarEstadoBloqueo(idCategoria: Int, estaBloqueada: Boolean)
}
