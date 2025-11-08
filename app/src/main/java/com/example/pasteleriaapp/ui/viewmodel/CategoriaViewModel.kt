package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.model.Categoria
import com.example.pasteleriaapp.domain.repository.CategoriaRepository
import com.example.pasteleriaapp.ui.state.CategoriaUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Gestiona el estado y la lógica de la pantalla de categorías.
 */
class CategoriaViewModel(
    private val repositorio: CategoriaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriaUiState())
    val uiState: StateFlow<CategoriaUiState> = _uiState.asStateFlow()

    init {
        cargarCategorias()
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            _uiState.value = CategoriaUiState(estaCargando = true)

            repositorio.obtenerCategorias()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        estaCargando = false,
                        error = e.message
                    )
                }
                .collect { categorias ->
                    _uiState.value = _uiState.value.copy(
                        estaCargando = false,
                        categorias = categorias,
                        error = null
                    )
                }
        }
    }

    // Aquí puedes agregar funciones como agregarCategoria, etc.
}

/**
 * Factory para crear el CategoriaViewModel con su repositorio.
 */
class CategoriaViewModelFactory(
    private val repositorio: CategoriaRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriaViewModel::class.java)) {
            return CategoriaViewModel(repositorio) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}