package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.repository.CarritoRepository
import com.example.pasteleriaapp.domain.repository.ProductoRepository
import com.example.pasteleriaapp.ui.state.ProductoDetalleUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductoDetalleViewModel(
    private val repository: ProductoRepository,
    private val carritoRepository: CarritoRepository,
    private val idProducto: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoDetalleUiState())
    val uiState: StateFlow<ProductoDetalleUiState> = _uiState.asStateFlow()

    init {
        cargarProducto()
    }

    private fun cargarProducto() {
        viewModelScope.launch {
            _uiState.value = ProductoDetalleUiState(estaCargando = true)
            try {
                val producto = repository.obtenerProductoPorId(idProducto)
                _uiState.value = ProductoDetalleUiState(producto = producto)
            } catch (e: Exception) {
                _uiState.value = ProductoDetalleUiState(error = e.message ?: "Error desconocido")
            }
        }
    }

    fun agregarAlCarrito(mensaje: String) {
        val producto = _uiState.value.producto ?: return

        viewModelScope.launch {
            try {
                carritoRepository.agregarAlCarrito(producto, 1, mensaje)
                _uiState.update { it.copy(itemAgregado = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun eventoItemAgregadoMostrado() {
        _uiState.update { it.copy(itemAgregado = false) }
    }
}


class ProductoDetalleViewModelFactory(
    private val productoRepository: ProductoRepository,
    private val carritoRepository: CarritoRepository, // <-- DEPENDENCIA AÑADIDA
    private val idProducto: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoDetalleViewModel::class.java)) {
            return ProductoDetalleViewModel(
                productoRepository,
                carritoRepository, // <-- PASAR DEPENDENCIA
                idProducto
            ) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}