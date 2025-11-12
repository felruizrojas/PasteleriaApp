package com.example.pasteleriaapp.ui.state

import com.example.pasteleriaapp.domain.model.CarritoItem

data class CarritoUiState(
    val estaCargando: Boolean = false,
    val items: List<CarritoItem> = emptyList(),
    val error: String? = null,
    val subtotal: Double = 0.0
) {
    val hayItems: Boolean get() = items.isNotEmpty()
    val totalArticulos: Int get() = items.sumOf { it.cantidad }
}