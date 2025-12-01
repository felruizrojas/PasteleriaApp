package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.domain.repository.CategoriaRepository
import com.example.pasteleriaapp.domain.repository.ProductoRepository
import com.example.pasteleriaapp.ui.state.ProductoFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductoFormViewModel(
    private val productoRepository: ProductoRepository,
    private val categoriaRepository: CategoriaRepository,
    private val idProducto: Int,
    private val initialCategoriaId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoFormUiState())
    val uiState: StateFlow<ProductoFormUiState> = _uiState.asStateFlow()

    init {
        observarCategorias()
        if (idProducto != 0) {
            // Es modo Edición
            cargarProducto(idProducto)
        } else {
            // Es modo Creación
            _uiState.update {
                it.copy(
                    idCategoria = initialCategoriaId,
                    tituloPantalla = "Nuevo Producto"
                )
            }
        }
    }

    private fun cargarProducto(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }
            try {
                val producto = productoRepository.obtenerProductoPorId(id)
                if (producto != null) {
                    // Actualizamos el estado con todos los datos del producto
                    _uiState.update {
                        it.copy(
                            idProducto = producto.idProducto,
                            idCategoria = producto.idCategoria,
                            nombre = producto.nombreProducto,
                            precio = producto.precioProducto.toString(),
                            descripcion = producto.descripcionProducto, // <-- Aquí cargará la descripción
                            stock = producto.stockProducto.toString(),
                            codigo = producto.codigoProducto,
                            imagen = producto.imagenProducto,
                            estaBloqueado = producto.estaBloqueado,
                            tituloPantalla = "Editar Producto",
                            estaCargando = false // <-- Marcamos como 'no cargando'
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(error = "Producto no encontrado", estaCargando = false)
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message, estaCargando = false)
                }
            }
        }
    }

    private fun observarCategorias() {
        viewModelScope.launch {
            categoriaRepository.obtenerCategoriasAdmin()
                .catch { error ->
                    _uiState.update {
                        it.copy(error = error.message ?: "Error al cargar categorías")
                    }
                }
                .collect { categorias ->
                    _uiState.update { estadoActual ->
                        val selectedId = when {
                            estadoActual.idCategoria != 0 && categorias.any { it.idCategoria == estadoActual.idCategoria } -> estadoActual.idCategoria
                            initialCategoriaId != 0 && categorias.any { it.idCategoria == initialCategoriaId } -> initialCategoriaId
                            categorias.isNotEmpty() -> categorias.first().idCategoria
                            else -> 0
                        }
                        estadoActual.copy(
                            categoriasDisponibles = categorias,
                            idCategoria = selectedId
                        )
                    }
                }
        }
    }

    // --- Funciones para actualizar campos (sin cambios) ---
    fun onNombreChange(valor: String) { _uiState.update { it.copy(nombre = valor) } }
    fun onPrecioChange(valor: String) { _uiState.update { it.copy(precio = valor) } }
    fun onDescripcionChange(valor: String) { _uiState.update { it.copy(descripcion = valor) } }
    fun onStockChange(valor: String) { _uiState.update { it.copy(stock = valor) } }
    fun onCodigoChange(valor: String) { _uiState.update { it.copy(codigo = valor) } }
    fun onImagenChange(valor: String) { _uiState.update { it.copy(imagen = valor) } }
    fun onCategoriaSeleccionada(idCategoria: Int) { _uiState.update { it.copy(idCategoria = idCategoria) } }

    fun mostrarError(mensaje: String) {
        _uiState.update { it.copy(error = mensaje) }
    }
    // ---

    fun guardarProducto() {
        val state = _uiState.value

        val precioDouble = state.precio.toDoubleOrNull()
        val stockInt = state.stock.toIntOrNull()

        if (state.nombre.isBlank()) {
            _uiState.update { it.copy(error = "El nombre es obligatorio.") }
            return
        }
        if (state.codigo.isBlank()) {
            _uiState.update { it.copy(error = "El código es obligatorio.") }
            return
        }
        if (state.descripcion.isBlank()) {
            _uiState.update { it.copy(error = "La descripción es obligatoria.") }
            return
        }
        if (state.imagen.isBlank()) {
            _uiState.update { it.copy(error = "La imagen es obligatoria.") }
            return
        }
        if (state.idCategoria == 0) {
            _uiState.update { it.copy(error = "Debes seleccionar una categoría.") }
            return
        }
        if (precioDouble == null || stockInt == null) {
            _uiState.update { it.copy(error = "Precio y stock deben ser numéricos.") }
            return
        }

        val producto = Producto(
            idProducto = state.idProducto,
            idCategoria = state.idCategoria,
            nombreProducto = state.nombre.trim(),
            precioProducto = precioDouble,
            descripcionProducto = state.descripcion.trim(),
            codigoProducto = state.codigo.trim(),
            stockProducto = stockInt,
            stockCriticoProducto = 10,
            imagenProducto = state.imagen.trim(),
            estaBloqueado = state.estaBloqueado
        )

        viewModelScope.launch {
            try {
                if (producto.idProducto == 0) {
                    productoRepository.insertarProducto(producto)
                } else {
                    // TODO: Necesitamos preservar la imagen y otros campos
                    // que no están en el formulario
                    productoRepository.actualizarProducto(producto)
                }
                _uiState.update { it.copy(guardadoExitoso = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}

// --- Factory (Sin cambios) ---
class ProductoFormViewModelFactory(
    private val productoRepository: ProductoRepository,
    private val categoriaRepository: CategoriaRepository,
    private val idProducto: Int,
    private val idCategoria: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoFormViewModel::class.java)) {
            return ProductoFormViewModel(productoRepository, categoriaRepository, idProducto, idCategoria) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}