package com.example.pasteleriaapp.ui.state

import com.example.pasteleriaapp.domain.model.Categoria
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CategoriaUiStateTest {

    @Test
    fun hayCategorias_esFalseCuandoListaVacia() {
        val state = CategoriaUiState()
        assertFalse(state.hayCategorias)
        assertEquals("", state.searchQuery)
        assertTrue(state.categorias.isEmpty())
    }

    @Test
    fun hayCategorias_esTrueCuandoHayItems() {
        val categorias = listOf(Categoria(idCategoria = 1, nombreCategoria = "Tortas", imagenCategoria = "img.jpg"))
        val state = CategoriaUiState(categorias = categorias, searchQuery = "tor")
        assertTrue(state.hayCategorias)
        assertEquals(categorias, state.categorias)
    }
}
