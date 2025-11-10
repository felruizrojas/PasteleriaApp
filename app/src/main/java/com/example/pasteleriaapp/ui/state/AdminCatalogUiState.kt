package com.example.pasteleriaapp.ui.state

import com.example.pasteleriaapp.domain.model.Categoria
import com.example.pasteleriaapp.domain.model.Producto

/** Estado de la sección de administración de catálogo (categorías + productos). */
data class AdminCatalogUiState(
    val isLoading: Boolean = true,
    val isActionInProgress: Boolean = false,
    val categorias: List<Categoria> = emptyList(),
    val categoriaSeleccionadaId: Int? = null,
    val productosDeCategoria: List<Producto> = emptyList(),
    val mensaje: String? = null,
    val error: String? = null
) {
    val categoriaSeleccionada: Categoria? = categorias.firstOrNull { it.idCategoria == categoriaSeleccionadaId }
}
