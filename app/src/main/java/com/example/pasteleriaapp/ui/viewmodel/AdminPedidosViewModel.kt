package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.model.EstadoPedido
import com.example.pasteleriaapp.domain.model.Usuario
import com.example.pasteleriaapp.domain.model.displayName
import com.example.pasteleriaapp.domain.repository.PedidoRepository
import com.example.pasteleriaapp.domain.repository.UsuarioRepository
import com.example.pasteleriaapp.ui.state.AdminPedidosUiState
import com.example.pasteleriaapp.ui.state.PedidoAdminItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminPedidosViewModel(
    private val pedidoRepository: PedidoRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminPedidosUiState(isLoading = true))
    val uiState: StateFlow<AdminPedidosUiState> = _uiState.asStateFlow()

    private val usuariosCache = mutableMapOf<Int, Usuario?>()

    init {
        observarPedidos()
    }

    private fun observarPedidos() {
        viewModelScope.launch {
            pedidoRepository.obtenerTodosLosPedidos()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error al cargar los pedidos"
                        )
                    }
                }
                .collectLatest { pedidos ->
                    val items = mutableListOf<PedidoAdminItem>()
                    val ordenados = pedidos.sortedByDescending { it.fechaPedido }
                    for (pedido in ordenados) {
                        val usuario = obtenerUsuarioCached(pedido.idUsuario)
                        items += PedidoAdminItem(pedido = pedido, cliente = usuario)
                    }
                    val filtro = _uiState.value.filtroEstado
                    val filtrados = aplicarFiltro(filtro, items)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            pedidos = items,
                            pedidosFiltrados = filtrados,
                            error = null
                        )
                    }
                }
        }
    }

    private suspend fun obtenerUsuarioCached(idUsuario: Int): Usuario? {
        val cached = usuariosCache[idUsuario]
        if (cached != null || usuariosCache.containsKey(idUsuario)) {
            return cached
        }
        val usuario = usuarioRepository.obtenerUsuarioPorId(idUsuario)
        usuariosCache[idUsuario] = usuario
        return usuario
    }

    private fun aplicarFiltro(filtro: EstadoPedido?, pedidos: List<PedidoAdminItem>): List<PedidoAdminItem> {
        return if (filtro == null) pedidos else pedidos.filter { it.estado == filtro }
    }

    fun onFiltroEstadoChange(nuevoFiltro: EstadoPedido?) {
        _uiState.update { estadoActual ->
            val filtrados = aplicarFiltro(nuevoFiltro, estadoActual.pedidos)
            estadoActual.copy(filtroEstado = nuevoFiltro, pedidosFiltrados = filtrados)
        }
    }

    fun actualizarEstadoPedido(idPedido: Int, nuevoEstado: EstadoPedido) {
        val pedidoActual = _uiState.value.pedidos.firstOrNull { it.idPedido == idPedido } ?: return
        if (pedidoActual.estado == nuevoEstado) {
            _uiState.update { it.copy(mensaje = "El pedido ya se encuentra en ${nuevoEstado.displayName()}") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isActionInProgress = true, error = null, mensaje = null) }
            try {
                pedidoRepository.actualizarEstadoPedido(idPedido, nuevoEstado)
                _uiState.update {
                    it.copy(mensaje = "Estado actualizado a ${nuevoEstado.displayName()}")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Error al actualizar el estado del pedido")
                }
            } finally {
                _uiState.update { it.copy(isActionInProgress = false) }
            }
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensaje = null, error = null) }
    }
}

class AdminPedidosViewModelFactory(
    private val pedidoRepository: PedidoRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminPedidosViewModel::class.java)) {
            return AdminPedidosViewModel(pedidoRepository, usuarioRepository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
