package com.example.pasteleriaapp.ui.screen.productos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.domain.model.Categoria
import com.example.pasteleriaapp.ui.viewmodel.CategoriaViewModel

/**
 * Pantalla principal que muestra la lista de categorías.
 * Recibe el ViewModel y un lambda para manejar los clics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
    viewModel: CategoriaViewModel,
    onCategoriaClick: (Int) -> Unit // Lambda para navegar
) {
    // Observa el uiState del ViewModel
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Pastelería Mil Sabores") })
        }
    ) { paddingValues ->
        // Contenido de la pantalla, aplica el padding del Scaffold
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                // 1. Estado de Cargando
                state.estaCargando -> {
                    CircularProgressIndicator()
                }

                // 2. Estado de Error
                state.error != null -> {
                    Text(text = "Error: ${state.error}")
                }

                // 3. Estado de Éxito
                state.hayCategorias -> {
                    CategoriasList(
                        categorias = state.categorias,
                        onCategoriaClick = onCategoriaClick
                    )
                }

                // 4. Estado vacío
                else -> {
                    Text(text = "No hay categorías disponibles.")
                }
            }
        }
    }
}

/**
 * Muestra la lista de categorías en un LazyColumn.
 */
@Composable
private fun CategoriasList(
    categorias: List<Categoria>,
    onCategoriaClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        items(categorias) { categoria ->
            CategoriaItem(
                categoria = categoria,
                onClick = { onCategoriaClick(categoria.idCategoria) }
            )
        }
    }
}

/**
 * Representa un solo ítem de categoría en la lista.
 */
@Composable
private fun CategoriaItem(
    categoria: Categoria,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(categoria.nombreCategoria) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}