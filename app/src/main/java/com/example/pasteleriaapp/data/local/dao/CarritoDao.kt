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
    @Query("SELECT * FROM carrito WHERE idUsuario = :idUsuario")
    fun obtenerItemsCarrito(idUsuario: Int): Flow<List<CarritoItemEntity>>

    @Query("SELECT * FROM carrito WHERE idUsuario = :idUsuario AND idProducto = :idProducto AND mensajePersonalizado = :mensaje LIMIT 1")
    suspend fun obtenerItemPorProductoYMensaje(idUsuario: Int, idProducto: Int, mensaje: String): CarritoItemEntity?

    @Query("SELECT * FROM carrito WHERE idCarrito = :idCarrito LIMIT 1")
    suspend fun obtenerItemPorId(idCarrito: Int): CarritoItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarItem(item: CarritoItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarItems(items: List<CarritoItemEntity>)

    @Update
    suspend fun actualizarItem(item: CarritoItemEntity)

    @Delete
    suspend fun eliminarItem(item: CarritoItemEntity)

    @Query("UPDATE carrito SET mensajePersonalizado = :mensaje WHERE idCarrito = :idCarrito")
    suspend fun actualizarMensajeItem(idCarrito: Int, mensaje: String)

    @Query("DELETE FROM carrito WHERE idUsuario = :idUsuario")
    suspend fun limpiarCarrito(idUsuario: Int)
}