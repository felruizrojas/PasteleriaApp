package com.example.pasteleriaapp.ui.state

import com.example.pasteleriaapp.domain.model.EstadoPedido
import com.example.pasteleriaapp.domain.model.Pedido
import com.example.pasteleriaapp.domain.model.Usuario

/**
 * Representa un pedido enriquecido con información del cliente para uso administrativo.
 */
data class PedidoAdminItem(
    val pedido: Pedido,
    val cliente: Usuario?
) {
    val idPedido: Int get() = pedido.idPedido
    val estado: EstadoPedido get() = pedido.estado
    val total: Double get() = pedido.total
    val nombreCliente: String
        get() = cliente?.let { "${it.nombre} ${it.apellidos}".trim() }
            ?.takeIf { it.isNotBlank() }
            ?: "Cliente #${pedido.idUsuario}"
    val correoCliente: String? get() = cliente?.correo
}

data class AdminPedidosUiState(
    val isLoading: Boolean = false,
    val isActionInProgress: Boolean = false,
    val pedidos: List<PedidoAdminItem> = emptyList(),
    val pedidosFiltrados: List<PedidoAdminItem> = emptyList(),
    val filtroEstado: EstadoPedido? = null,
    val mensaje: String? = null,
    val error: String? = null
)
