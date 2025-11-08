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
 * NOTA: Actualmente muestra TODOS los productos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoListScreen(
    viewModel: ProductoViewModel,
    onBackClick: () -> Unit, // Lambda para volver atrás
    onProductoClick: (Int) -> Unit // Lambda para ir al detalle
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos") }, // Debería ser el nombre de la categoría
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
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