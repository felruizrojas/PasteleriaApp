package com.example.pasteleriaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.pasteleriaapp.data.local.entity.PedidoEntity
import com.example.pasteleriaapp.data.local.entity.PedidoProductoEntity
import com.example.pasteleriaapp.domain.model.Pedido
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {

    // Inserta un pedido y DEVUELVE el ID autogenerado (en Long)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPedido(pedido: PedidoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPedidoProductos(productos: List<PedidoProductoEntity>)

    // --- Funciones para la UI ---

    @Query("SELECT * FROM pedido WHERE idUsuario = :idUsuario ORDER BY fechaPedido DESC")
    fun obtenerPedidosPorUsuario(idUsuario: Int): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedido WHERE idPedido = :idPedido")
    suspend fun obtenerPedidoPorId(idPedido: Int): PedidoEntity?

    @Query("SELECT * FROM pedido_producto WHERE idPedido = :idPedido")
    suspend fun obtenerProductosPorPedidoId(idPedido: Int): List<PedidoProductoEntity>
}