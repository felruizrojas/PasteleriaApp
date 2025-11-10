package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.model.Categoria
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.domain.repository.CategoriaRepository
import com.example.pasteleriaapp.domain.repository.ProductoRepository
import com.example.pasteleriaapp.ui.state.AdminCatalogUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminCatalogViewModel(
    private val categoriaRepository: CategoriaRepository,
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminCatalogUiState())
    val uiState: StateFlow<AdminCatalogUiState> = _uiState.asStateFlow()

    private val categoriaSeleccionadaId = MutableStateFlow<Int?>(null)

    init {
        observarCategorias()
        observarProductosPorCategoria()
    }

    private fun observarCategorias() {
        viewModelScope.launch {
            categoriaRepository.obtenerCategoriasAdmin()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error al cargar categorías"
                        )
                    }
                }
                .collect { categorias ->
                    val nuevoSeleccionado = when {
                        categorias.isEmpty() -> null
                        categoriaSeleccionadaId.value == null -> categorias.first().idCategoria
                        categorias.none { it.idCategoria == categoriaSeleccionadaId.value } -> categorias.first().idCategoria
                        else -> categoriaSeleccionadaId.value
                    }

                    categoriaSeleccionadaId.value = nuevoSeleccionado

                    _uiState.update {
                        it.copy(
                            categorias = categorias,
                            categoriaSeleccionadaId = nuevoSeleccionado,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun observarProductosPorCategoria() {
        viewModelScope.launch {
            categoriaSeleccionadaId
                .flatMapLatest { idCategoria ->
                    if (idCategoria == null) flowOf(emptyList())
                    else productoRepository.obtenerProductosPorCategoriaAdmin(idCategoria)
                }
                .catch { error ->
                    _uiState.update {
                        it.copy(error = error.message ?: "Error al cargar productos")
                    }
                }
                .collect { productos ->
                    _uiState.update {
                        it.copy(productosDeCategoria = productos)
                    }
                }
        }
    }

    fun seleccionarCategoria(idCategoria: Int) {
        if (categoriaSeleccionadaId.value != idCategoria) {
            categoriaSeleccionadaId.value = idCategoria
            _uiState.update { it.copy(categoriaSeleccionadaId = idCategoria) }
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensaje = null, error = null) }
    }

    fun crearCategoria(nombre: String, imagen: String) {
        val nombreTrim = nombre.trim()
        if (nombreTrim.isEmpty()) {
            _uiState.update { it.copy(mensaje = "El nombre es obligatorio") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isActionInProgress = true) }
            try {
                val nuevaCategoria = Categoria(
                    nombreCategoria = nombreTrim,
                    imagenCategoria = imagen.trim()
                )
                categoriaRepository.insertarCategoria(nuevaCategoria)
                _uiState.update { it.copy(mensaje = "Categoría creada correctamente") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error al crear categoría") }
            } finally {
                _uiState.update { it.copy(isActionInProgress = false) }
            }
        }
    }

    fun actualizarCategoria(categoria: Categoria, nombre: String, imagen: String) {
        val nombreTrim = nombre.trim()
        if (nombreTrim.isEmpty()) {
            _uiState.update { it.copy(mensaje = "El nombre es obligatorio") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isActionInProgress = true) }
            try {
                val actualizada = categoria.copy(
                    nombreCategoria = nombreTrim,
                    imagenCategoria = imagen.trim()
                )
                categoriaRepository.actualizarCategoria(actualizada)
                _uiState.update { it.copy(mensaje = "Categoría actualizada") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error al actualizar categoría") }
            } finally {
                _uiState.update { it.copy(isActionInProgress = false) }
            }
        }
    }

    fun alternarBloqueoCategoria(categoria: Categoria) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionInProgress = true) }
            val nuevoEstado = !categoria.estaBloqueada
            try {
                categoriaRepository.actualizarEstadoBloqueo(categoria.idCategoria, nuevoEstado)
                val mensaje = if (nuevoEstado) "Categoría bloqueada" else "Categoría desbloqueada"
                _uiState.update { it.copy(mensaje = mensaje) }

                if (nuevoEstado && categoriaSeleccionadaId.value == categoria.idCategoria) {
                    // Si se bloquea la categoría seleccionada, mueve la selección a la primera disponible
                    val disponible = _uiState.value.categorias.firstOrNull { !it.estaBloqueada && it.idCategoria != categoria.idCategoria }
                    categoriaSeleccionadaId.value = disponible?.idCategoria
                    _uiState.update { it.copy(categoriaSeleccionadaId = disponible?.idCategoria) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error al actualizar estado de la categoría") }
            } finally {
                _uiState.update { it.copy(isActionInProgress = false) }
            }
        }
    }

    fun alternarBloqueoProducto(producto: Producto) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionInProgress = true) }
            val nuevoEstado = !producto.estaBloqueado
            try {
                productoRepository.actualizarEstadoBloqueo(producto.idProducto, nuevoEstado)
                val mensaje = if (nuevoEstado) "Producto bloqueado" else "Producto desbloqueado"
                _uiState.update { it.copy(mensaje = mensaje) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error al actualizar estado del producto") }
            } finally {
                _uiState.update { it.copy(isActionInProgress = false) }
            }
        }
    }
}

class AdminCatalogViewModelFactory(
    private val categoriaRepository: CategoriaRepository,
    private val productoRepository: ProductoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminCatalogViewModel::class.java)) {
            return AdminCatalogViewModel(categoriaRepository, productoRepository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
