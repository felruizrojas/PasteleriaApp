package com.example.pasteleriaapp.ui.state

import com.example.pasteleriaapp.domain.model.CarritoItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CarritoUiStateTest {

    @Test
    fun hayItems_isFalseWhenListIsEmpty() {
        val state = CarritoUiState()
        assertFalse(state.hayItems)
        assertEquals(0, state.totalArticulos)
        assertEquals(0.0, state.subtotal, 0.0)
    }

    @Test
    fun totalArticulos_sumsEveryItemQuantity() {
        val items = listOf(
            CarritoItem(
                usuarioId = 1,
                idProducto = 10,
                nombreProducto = "Torta 1",
                precioProducto = 1000.0,
                imagenProducto = "img1",
                cantidad = 2,
                mensajePersonalizado = "Feliz"
            ),
            CarritoItem(
                usuarioId = 1,
                idProducto = 11,
                nombreProducto = "Torta 2",
                precioProducto = 2000.0,
                imagenProducto = "img2",
                cantidad = 3,
                mensajePersonalizado = "Cumple"
            )
        )

        val state = CarritoUiState(items = items, subtotal = 5000.0)
        assertTrue(state.hayItems)
        assertEquals(5, state.totalArticulos)
        assertEquals(items, state.items)
    }
}
