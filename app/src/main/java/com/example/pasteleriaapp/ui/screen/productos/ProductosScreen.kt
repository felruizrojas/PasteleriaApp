package com.example.pasteleriaapp.ui.screen.productos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
// --- IMPORTS NUEVOS ---
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
// --- FIN IMPORTS NUEVOS ---
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.ui.viewmodel.ProductoViewModel

/**
 * Pantalla que muestra la lista de productos.
 * (Comentario original corregido)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoListScreen(
    viewModel: ProductoViewModel,
    onBackClick: () -> Unit,
    onProductoClick: (Int) -> Unit,
    onAddProductoClick: () -> Unit // <-- ¡ESTE ES EL PARÁMETRO QUE FALTABA!
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos") }, // Puedes hacerlo dinámico si quieres
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        // --- CÓDIGO AÑADIDO ---
        // Este es el botón "+" para añadir productos
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProductoClick) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
            }
        }
        // --- FIN DE CÓDIGO AÑADIDO ---
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.estaCargando -> {
                    CircularProgressIndicator()
                }

                state.error != null -> {
                    Text(text = "Error: ${state.error}")
                }

                state.hayProductos -> {
                    ProductosList(
                        productos = state.productos,
                        onProductoClick = onProductoClick
                    )
                }

                else -> {
                    Text(text = "No hay productos en esta categoría.")
                }
            }
        }
    }
}

@Composable
private fun ProductosList(
    productos: List<Producto>,
    onProductoClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        items(productos) { producto ->
            ProductoItem(
                producto = producto,
                onClick = { onProductoClick(producto.idProducto) }
            )
        }
    }
}

@Composable
private fun ProductoItem(
    producto: Producto,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(producto.nombreProducto) },
        supportingContent = { Text("Precio: $${producto.precioProducto}") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}