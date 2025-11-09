package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.model.EstadoPedido
import com.example.pasteleriaapp.domain.model.Pedido
import com.example.pasteleriaapp.domain.model.PedidoProducto
import com.example.pasteleriaapp.domain.repository.CarritoRepository
import com.example.pasteleriaapp.domain.repository.PedidoRepository
import com.example.pasteleriaapp.ui.state.CarritoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

// --- ESTADO PARA LA LISTA DE PEDIDOS ---
data class MisPedidosUiState(
    val estaCargando: Boolean = false,
    val pedidos: List<Pedido> = emptyList(),
    val error: String? = null
)

// --- ESTADO PARA EL DETALLE DE UN PEDIDO ---
data class PedidoDetalleUiState(
    val estaCargando: Boolean = false,
    val pedido: Pedido? = null,
    val productos: List<PedidoProducto> = emptyList(),
    val error: String? = null
)

// --- ESTADO PARA EL CHECKOUT ---
data class CheckoutUiState(
    val estaCargando: Boolean = false,
    val fechaEntrega: String = "",
    val error: String? = null,
    val pedidoCreadoId: Int? = null // Para navegar al detalle del pedido
)

class PedidoViewModel(
    private val pedidoRepository: PedidoRepository,
    private val carritoRepository: CarritoRepository // Para limpiar el carrito
) : ViewModel() {

    // Estado para la pantalla "Mis Pedidos"
    private val _misPedidosState = MutableStateFlow(MisPedidosUiState())
    val misPedidosState: StateFlow<MisPedidosUiState> = _misPedidosState.asStateFlow()

    // Estado para la pantalla "Detalle del Pedido"
    private val _pedidoDetalleState = MutableStateFlow(PedidoDetalleUiState())
    val pedidoDetalleState: StateFlow<PedidoDetalleUiState> = _pedidoDetalleState.asStateFlow()

    // Estado para la pantalla "Checkout"
    private val _checkoutState = MutableStateFlow(CheckoutUiState())
    val checkoutState: StateFlow<CheckoutUiState> = _checkoutState.asStateFlow()

    // --- Lógica para "Mis Pedidos" ---
    fun cargarMisPedidos(idUsuario: Int) {
        viewModelScope.launch {
            _misPedidosState.update { it.copy(estaCargando = true) }
            pedidoRepository.obtenerPedidosPorUsuario(idUsuario)
                .catch { e ->
                    _misPedidosState.update { it.copy(estaCargando = false, error = e.message) }
                }
                .collect { pedidos ->
                    _misPedidosState.update { it.copy(estaCargando = false, pedidos = pedidos) }
                }
        }
    }

    // --- Lógica para "Detalle del Pedido" ---
    fun cargarDetallePedido(idPedido: Int) {
        viewModelScope.launch {
            _pedidoDetalleState.update { it.copy(estaCargando = true) }
            try {
                val (pedido, productos) = pedidoRepository.obtenerDetallePedido(idPedido)
                _pedidoDetalleState.update {
                    it.copy(estaCargando = false, pedido = pedido, productos = productos)
                }
            } catch (e: Exception) {
                _pedidoDetalleState.update { it.copy(estaCargando = false, error = e.message) }
            }
        }
    }

    // --- Lógica para "Checkout" ---
    fun onFechaEntregaChange(fecha: String) {
        _checkoutState.update { it.copy(fechaEntrega = fecha) }
    }

    fun crearPedido(idUsuario: Int, items: List<CarritoUiState>, total: Double) {
        val fechaEntrega = _checkoutState.value.fechaEntrega
        if (fechaEntrega.isBlank()) {
            _checkoutState.update { it.copy(error = "Debe seleccionar una fecha de entrega") }
            return
        }

        viewModelScope.launch {
            _checkoutState.update { it.copy(estaCargando = true, error = null) }
            try {
                // 1. Creamos el objeto Pedido
                val nuevoPedido = Pedido(
                    idUsuario = idUsuario,
                    fechaPedido = Date().time, // Fecha/hora actual
                    fechaEntregaPreferida = fechaEntrega,
                    estado = EstadoPedido.PENDIENTE,
                    total = total
                )

                // 2. Mapeamos los items del CarritoUiState a CarritoItem (modelo de dominio)
                val itemsDominio = items.map { it.items }.flatten()

                // 3. Llamamos al repositorio (que hace la transacción)
                pedidoRepository.crearPedido(nuevoPedido, itemsDominio)

                // 4. Actualizamos el estado
                // (Nota: crearPedido ya limpia el carrito, pero lo hacemos explícito)
                carritoRepository.limpiarCarrito()
                _checkoutState.update {
                    // Guardamos el ID (aunque 'crearPedido' no lo devuelve,
                    // en un caso real lo haría para redirigir)
                    it.copy(estaCargando = false, pedidoCreadoId = 1) // ID Ficticio
                }

            } catch (e: Exception) {
                _checkoutState.update { it.copy(estaCargando = false, error = e.message) }
            }
        }
    }

    fun resetCheckoutState() {
        _checkoutState.value = CheckoutUiState()
    }
}

// Factory para el PedidoViewModel
class PedidoViewModelFactory(
    private val pedidoRepository: PedidoRepository,
    private val carritoRepository: CarritoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PedidoViewModel::class.java)) {
            return PedidoViewModel(pedidoRepository, carritoRepository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}