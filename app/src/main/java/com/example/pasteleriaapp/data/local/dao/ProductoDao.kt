package com.example.pasteleriaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pasteleriaapp.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM producto WHERE estaBloqueado = 0 ORDER BY nombreProducto ASC")
    fun obtenerProductos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM producto WHERE idCategoria = :idCategoria AND estaBloqueado = 0 ORDER BY nombreProducto ASC")
    fun obtenerProductosPorCategoria(idCategoria: Int): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM producto WHERE idCategoria = :idCategoria ORDER BY nombreProducto ASC")
    fun obtenerProductosPorCategoriaAdmin(idCategoria: Int): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM producto WHERE idProducto = :idProducto")
    suspend fun obtenerProductoPorId(idProducto: Int): ProductoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: ProductoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProductos(productos: List<ProductoEntity>)

    @Update
    suspend fun actualizarProducto(producto: ProductoEntity)

    @Delete
    suspend fun eliminarProducto(producto: ProductoEntity)

    @Delete
    suspend fun eliminarProductos(productos: List<ProductoEntity>)

    @Query("DELETE FROM producto")
    suspend fun eliminarTodosLosProductos()

    @Query("UPDATE producto SET estaBloqueado = :estaBloqueado WHERE idProducto = :idProducto")
    suspend fun actualizarEstadoBloqueo(idProducto: Int, estaBloqueado: Boolean)
}
