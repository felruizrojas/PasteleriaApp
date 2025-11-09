package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.domain.repository.ProductoRepository
import com.example.pasteleriaapp.ui.state.ProductoFormUiState
import com.example.pasteleriaapp.ui.state.toFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductoFormViewModel(
    private val repository: ProductoRepository,
    private val idProducto: Int,
    private val idCategoria: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoFormUiState())
    val uiState: StateFlow<ProductoFormUiState> = _uiState.asStateFlow()

    init {
        if (idProducto != 0) {
            // Es modo Edición
            cargarProducto(idProducto)
        } else {
            // Es modo Creación
            _uiState.update {
                it.copy(
                    idCategoria = idCategoria,
                    tituloPantalla = "Nuevo Producto"
                )
            }
        }
    }

    private fun cargarProducto(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }
            try {
                val producto = repository.obtenerProductoPorId(id)
                if (producto != null) {
                    _uiState.value = producto.toFormUiState()
                } else {
                    _uiState.update { it.copy(error = "Producto no encontrado") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(estaCargando = false) }
            }
        }
    }

    // --- Funciones para actualizar campos ---
    fun onNombreChange(valor: String) { _uiState.update { it.copy(nombre = valor) } }
    fun onPrecioChange(valor: String) { _uiState.update { it.copy(precio = valor) } }
    fun onDescripcionChange(valor: String) { _uiState.update { it.copy(descripcion = valor) } }
    fun onStockChange(valor: String) { _uiState.update { it.copy(stock = valor) } }
    fun onCodigoChange(valor: String) { _uiState.update { it.copy(codigo = valor) } }
    // ---

    fun guardarProducto() {
        val state = _uiState.value

        // Validación (puedes mejorarla mucho)
        val precioDouble = state.precio.toDoubleOrNull()
        val stockInt = state.stock.toIntOrNull()

        if (state.nombre.isBlank() || precioDouble == null || stockInt == null) {
            _uiState.update { it.copy(error = "Datos inválidos. Revisa el formulario.") }
            return
        }

        val producto = Producto(
            idProducto = state.idProducto, // 0 si es nuevo, >0 si es edición
            idCategoria = state.idCategoria,
            nombreProducto = state.nombre.trim(),
            precioProducto = precioDouble,
            descripcionProducto = state.descripcion.trim(),
            codigoProducto = state.codigo.trim(),
            stockProducto = stockInt,
            stockCriticoProducto = 10, // Valor por defecto
            imagenProducto = "" // Valor por defecto
        )

        viewModelScope.launch {
            try {
                if (producto.idProducto == 0) {
                    repository.insertarProducto(producto)
                } else {
                    repository.actualizarProducto(producto)
                }
                _uiState.update { it.copy(guardadoExitoso = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}

/**
 * Factory para crear el ProductoFormViewModel.
 * (Esta es la Factory que te faltaba)
 */
class ProductoFormViewModelFactory(
    private val repository: ProductoRepository,
    private val idProducto: Int,
    private val idCategoria: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoFormViewModel::class.java)) {
            return ProductoFormViewModel(repository, idProducto, idCategoria) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}