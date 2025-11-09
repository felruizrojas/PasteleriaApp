package com.example.pasteleriaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pasteleriaapp.domain.model.EstadoPedido
import com.example.pasteleriaapp.domain.model.Pedido

@Entity(tableName = "pedido")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true)
    val idPedido: Int = 0,
    val idUsuario: Int,
    val fechaPedido: Long,
    val fechaEntregaPreferida: String,
    val estado: EstadoPedido, // Room usará el TypeConverter
    val total: Double
)

fun PedidoEntity.toPedido() = Pedido(
    idPedido = idPedido,
    idUsuario = idUsuario,
    fechaPedido = fechaPedido,
    fechaEntregaPreferida = fechaEntregaPreferida,
    estado = estado,
    total = total
)

fun Pedido.toPedidoEntity() = PedidoEntity(
    idPedido = idPedido,
    idUsuario = idUsuario,
    fechaPedido = fechaPedido,
    fechaEntregaPreferida = fechaEntregaPreferida,
    estado = estado,
    total = total
)