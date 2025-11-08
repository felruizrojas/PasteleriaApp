package com.example.pasteleriaapp.ui.state

import com.example.pasteleriaapp.domain.model.Categoria

data class CategoriaUiState(
    val estaCargando: Boolean = false,
    val categorias: List<Categoria> = emptyList(),
    val error: String? = null
) {
    val hayCategorias: Boolean
        get() = categorias.isNotEmpty()
}