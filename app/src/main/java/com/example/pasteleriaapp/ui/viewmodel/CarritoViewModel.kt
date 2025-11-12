package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.repository.CarritoRepository
import com.example.pasteleriaapp.ui.state.CarritoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CarritoViewModel(
    private val repository: CarritoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CarritoUiState())
    val uiState: StateFlow<CarritoUiState> = _uiState.asStateFlow()

    init {
        cargarItemsCarrito()
    }

    private fun cargarItemsCarrito() {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }

            repository.obtenerItemsCarrito()
                .map { items ->
                    val subtotal = items.sumOf { it.precioProducto * it.cantidad }
                    CarritoUiState(estaCargando = false, items = items, subtotal = subtotal)
                }
                .catch { e ->
                    _uiState.value = CarritoUiState(estaCargando = false, error = e.message)
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun actualizarCantidad(item: CarritoItem, nuevaCantidad: Int) {
        viewModelScope.launch {
            repository.actualizarCantidadItem(item, nuevaCantidad)
        }
    }

    fun eliminarItem(item: CarritoItem) {
        viewModelScope.launch {
            repository.eliminarItem(item)
        }
    }

    fun limpiarCarrito() {
        viewModelScope.launch {
            repository.limpiarCarrito()
        }
    }

    fun actualizarMensaje(item: CarritoItem, nuevoMensaje: String) {
        viewModelScope.launch {
            repository.actualizarMensajeItem(item.idCarrito, nuevoMensaje)
        }
    }
}

class CarritoViewModelFactory(
    private val repository: CarritoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarritoViewModel::class.java)) {
            return CarritoViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}