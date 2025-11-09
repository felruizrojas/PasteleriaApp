package com.example.pasteleriaapp.ui.state

import com.example.pasteleriaapp.domain.model.Producto

data class ProductoDetalleUiState(
    val estaCargando: Boolean = false,
    val producto: Producto? = null,
    val error: String? = null,
    val itemAgregado: Boolean = false // <-- CAMPO AÑADIDO
)