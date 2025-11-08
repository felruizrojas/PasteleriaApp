package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pasteleriaapp.data.repository.ProductoRepositoryImpl
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.ui.state.ProductoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProductoViewModel (
    private val repositoryImpl: ProductoRepositoryImpl
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductoUiState())
    val uiState: StateFlow<ProductoUiState> = _uiState.asStateFlow()

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(estaCargando = true)

            repositoryImpl.obtenerProductos()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        estaCargando = false,
                        error = exception.message ?: "Error desconocido"
                    )
                }
                .collect { productos ->
                    _uiState.value = _uiState.value.copy(
                        estaCargando = false,
                        productos = productos,
                        error = null
                    )
                }
        }
    }

    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            repositoryImpl.insertarProducto(producto)
        }
    }

    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            repositoryImpl.actualizarProducto(producto)
        }
    }

    fun eliminarProducto(producto: Producto) {
        viewModelScope.launch {
            repositoryImpl.eliminarProducto(producto)
        }
    }
}
/**
 * Factory para crear el ProductoViewModel, ya que necesita
 * que le inyectemos el RepositorioProductos.
 */
    class ProductoViewModelFactory(
        private val repositoryImpl: ProductoRepositoryImpl
    ) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            return ProductoViewModel(repositoryImpl) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}