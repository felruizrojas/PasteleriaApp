package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pasteleriaapp.MainDispatcherRule
import com.example.pasteleriaapp.domain.model.Categoria
import com.example.pasteleriaapp.domain.repository.CategoriaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun init_cargaCategorias_y_actualizaEstado() = runTest {
        val categorias = listOf(
            Categoria(idCategoria = 1, nombreCategoria = "Tortas", imagenCategoria = "tortas.jpg"),
            Categoria(idCategoria = 2, nombreCategoria = "Cupcakes", imagenCategoria = "cupcakes.jpg")
        )
        val viewModel = CategoriaViewModel(FakeCategoriaRepository(flowOf(categorias)))

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.estaCargando)
        assertEquals(categorias, state.categorias)
        assertEquals(null, state.error)
    }

    @Test
    fun cargarCategorias_propagateErrorEnUiState() = runTest {
        val expected = IllegalStateException("fallo api")
        val viewModel = CategoriaViewModel(FakeCategoriaRepository(flow { throw expected }))

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.estaCargando)
        assertEquals(expected.message, state.error)
        assertTrue(state.categorias.isEmpty())
    }

    @Test
    fun onSearchQueryChange_filtraBasadoEnNombre() = runTest {
        val categorias = listOf(
            Categoria(idCategoria = 1, nombreCategoria = "Tortas", imagenCategoria = "tortas.jpg"),
            Categoria(idCategoria = 2, nombreCategoria = "Cupcakes", imagenCategoria = "cupcakes.jpg"),
            Categoria(idCategoria = 3, nombreCategoria = "Cheesecake", imagenCategoria = "cheese.jpg")
        )
        val viewModel = CategoriaViewModel(FakeCategoriaRepository(flowOf(categorias)))

        advanceUntilIdle()

        viewModel.onSearchQueryChange("cup")
        val filtrado = viewModel.uiState.value
        assertEquals(listOf(categorias[1]), filtrado.categorias)
        assertEquals("cup", filtrado.searchQuery)

        viewModel.onSearchQueryChange("")
        val fullList = viewModel.uiState.value
        assertEquals(categorias, fullList.categorias)
    }

    @Test
    fun factory_create_devuelveViewModelEsperado() {
        val repo = FakeCategoriaRepository(flowOf(emptyList()))
        val factory = CategoriaViewModelFactory(repo)
        val viewModel = factory.create(CategoriaViewModel::class.java)
        assertEquals(CategoriaViewModel::class.java, viewModel::class.java)
    }

    @Test
    fun factory_create_lanzaErrorEnClaseDesconocida() {
        val repo = FakeCategoriaRepository(flowOf(emptyList()))
        val factory = CategoriaViewModelFactory(repo)

        assertThrows(IllegalArgumentException::class.java) {
            factory.create(ObjetoDummyViewModel::class.java)
        }
    }

    private class ObjetoDummyViewModel : ViewModel()

    private class FakeCategoriaRepository(
        private val categoriasFlow: Flow<List<Categoria>>
    ) : CategoriaRepository {
        override fun obtenerCategorias(): Flow<List<Categoria>> = categoriasFlow
        override fun obtenerCategoriasAdmin(): Flow<List<Categoria>> = emptyFlow()
        override suspend fun obtenerCategoriaPorId(idCategoria: Int) = null
        override suspend fun insertarCategoria(categoria: Categoria) = Unit
        override suspend fun insertarCategorias(categorias: List<Categoria>) = Unit
        override suspend fun actualizarCategoria(categoria: Categoria) = Unit
        override suspend fun eliminarCategoria(categoria: Categoria) = Unit
        override suspend fun eliminarTodasLasCategorias() = Unit
        override suspend fun actualizarEstadoBloqueo(idCategoria: Int, estaBloqueada: Boolean) = Unit
    }
}
