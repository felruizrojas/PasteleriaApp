package com.example.pasteleriaapp.ui.state

import com.example.pasteleriaapp.domain.model.Producto

data class ProductoUiState(
    val estaCargando : Boolean = false,
    val productos : List<Producto> = emptyList(),
    val error: String? = null
) {
    val hayProductos : Boolean
        get() = productos.isNotEmpty()
}
