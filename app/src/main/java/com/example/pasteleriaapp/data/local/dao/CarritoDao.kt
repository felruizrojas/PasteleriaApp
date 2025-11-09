package com.example.pasteleriaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pasteleriaapp.data.local.entity.CarritoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {
    @Query("SELECT * FROM carrito")
    fun obtenerItemsCarrito(): Flow<List<CarritoItemEntity>>

    @Query("SELECT * FROM carrito WHERE idProducto = :idProducto LIMIT 1")
    suspend fun obtenerItemPorProductoId(idProducto: Int): CarritoItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarItem(item: CarritoItemEntity)

    @Update
    suspend fun actualizarItem(item: CarritoItemEntity)

    @Delete
    suspend fun eliminarItem(item: CarritoItemEntity)

    @Query("DELETE FROM carrito")
    suspend fun limpiarCarrito()
}