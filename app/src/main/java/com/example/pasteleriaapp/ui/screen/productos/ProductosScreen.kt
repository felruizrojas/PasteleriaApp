package com.example.pasteleriaapp.ui.screen.productos

// --- IMPORTS NECESARIOS (Añadidos y modificados) ---
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.R // <-- Importar R
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.ui.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoListScreen(
    viewModel: ProductoViewModel,
    onBackClick: () -> Unit,
    onProductoClick: (Int) -> Unit,
    onAddProductoClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos") }, // Puedes hacerlo dinámico
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
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProductoClick) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
            }
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
                    // --- MODIFICADO: Usamos una Grilla ---
                    ProductosGrid(
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

/**
 * --- NUEVO COMPOSABLE ---
 * Muestra la grilla de productos.
 */
@Composable
private fun ProductosGrid(
    productos: List<Producto>,
    onProductoClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Grilla de 2 columnas
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(productos) { producto ->
            ProductoCard(
                producto = producto,
                onClick = { onProductoClick(producto.idProducto) }
            )
        }
    }
}

/**
 * --- NUEVO COMPOSABLE ---
 * Representa una sola Card de producto, mostrando solo imagen y nombre.
 */
@Composable
private fun ProductoCard(
    producto: Producto,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    // Usamos el nombre de la imagen del producto
    val imageResId = painterResourceFromName(context, producto.imagenProducto)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Imagen de ${producto.nombreProducto}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f), // Imagen cuadrada
                contentScale = ContentScale.Crop
            )
            Text(
                text = producto.nombreProducto, // <-- Solo el nombre
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * --- FUNCIÓN AUXILIAR (copiada de CategoriasScreen) ---
 * Obtiene un ID de drawable a partir de su nombre (String).
 */
@DrawableRes
@Composable
private fun painterResourceFromName(context: Context, resName: String): Int {
    return try {
        val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
        if (resId == 0) {
            // Imagen de fallback por si no se encuentra
            R.drawable.ic_launcher_background
        } else {
            resId
        }
    } catch (e: Exception) {
        R.drawable.ic_launcher_background // Fallback en caso de error
    }
}