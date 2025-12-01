package com.example.pasteleriaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.pasteleriaapp.data.local.entity.PedidoEntity
import com.example.pasteleriaapp.data.local.entity.PedidoProductoEntity
import com.example.pasteleriaapp.domain.model.EstadoPedido
import com.example.pasteleriaapp.domain.model.Pedido
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPedido(pedido: PedidoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPedidos(pedidos: List<PedidoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPedidoProductos(productos: List<PedidoProductoEntity>)

    @Query("SELECT * FROM pedido WHERE idUsuario = :idUsuario ORDER BY fechaPedido DESC")
    fun obtenerPedidosPorUsuario(idUsuario: Int): Flow<List<PedidoEntity>>

    @Query("SELECT idPedido FROM pedido WHERE idUsuario = :idUsuario")
    suspend fun obtenerIdsPedidosPorUsuario(idUsuario: Int): List<Int>

    @Query("SELECT * FROM pedido WHERE idPedido = :idPedido")
    suspend fun obtenerPedidoPorId(idPedido: Int): PedidoEntity?

    @Query("SELECT * FROM pedido_producto WHERE idPedido = :idPedido")
    suspend fun obtenerProductosPorPedidoId(idPedido: Int): List<PedidoProductoEntity>

    @Query("SELECT * FROM pedido ORDER BY fechaPedido DESC")
    fun obtenerTodosLosPedidos(): Flow<List<PedidoEntity>>

    @Query("SELECT idPedido FROM pedido")
    suspend fun obtenerTodosLosIds(): List<Int>

    @Query("UPDATE pedido SET estado = :estado WHERE idPedido = :idPedido")
    suspend fun actualizarEstadoPedido(idPedido: Int, estado: EstadoPedido)

    @Query("DELETE FROM pedido")
    suspend fun eliminarTodosLosPedidos()

    @Query("DELETE FROM pedido_producto")
    suspend fun eliminarTodosLosProductosDePedido()

    @Query("DELETE FROM pedido WHERE idPedido IN (:ids)")
    suspend fun eliminarPedidosPorIds(ids: List<Int>)

    @Query("DELETE FROM pedido_producto WHERE idPedido IN (:ids)")
    suspend fun eliminarProductosPorPedidos(ids: List<Int>)

    @Query("DELETE FROM pedido_producto WHERE idPedido = :idPedido")
    suspend fun eliminarProductosPorPedido(idPedido: Int)
}