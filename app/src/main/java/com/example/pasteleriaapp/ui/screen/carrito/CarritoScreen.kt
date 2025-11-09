package com.example.pasteleriaapp.ui.screen.carrito

import android.content.Context
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pasteleriaapp.R
import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.ui.viewmodel.CarritoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    viewModel: CarritoViewModel,
    onBackClick: () -> Unit,
    onNavigateToCheckout: () -> Unit // <-- ¡CAMBIO IMPORTANTE! (Paso 8)
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    if (state.hayItems) {
                        IconButton(onClick = { viewModel.limpiarCarrito() }) {
                            Icon(Icons.Default.Delete, "Vaciar carrito")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (state.hayItems) {
                CarritoBottomBar(
                    total = state.precioTotal,
                    onPagarClick = onNavigateToCheckout // <-- ¡CAMBIO IMPORTANTE! (Paso 8)
                )
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
                state.estaCargando -> CircularProgressIndicator()
                state.error != null -> Text("Error: ${state.error}")
                state.hayItems -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.items) { item ->
                            CarritoItemRow(
                                item = item,
                                onActualizarCantidad = { nuevaCantidad ->
                                    viewModel.actualizarCantidad(item, nuevaCantidad)
                                },
                                onEliminar = {
                                    viewModel.eliminarItem(item)
                                }
                            )
                        }
                    }
                }
                else -> {
                    Text("Tu carrito está vacío.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun CarritoItemRow(
    item: CarritoItem,
    onActualizarCantidad: (Int) -> Unit,
    onEliminar: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = painterResourceFromName(context, item.imagenProducto)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = item.nombreProducto,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            // Nombre y Precio
            Column(modifier = Modifier.weight(1f)) {
                Text(item.nombreProducto, style = MaterialTheme.typography.titleMedium)
                Text("$${item.precioProducto}", style = MaterialTheme.typography.bodyMedium)
                if (item.mensajePersonalizado.isNotBlank()) {
                    Text(
                        "\"${item.mensajePersonalizado}\"",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            // Controles de Cantidad
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onActualizarCantidad(item.cantidad - 1) },
                    modifier = Modifier
                        .size(28.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(Icons.Default.Remove, "Quitar 1", tint = MaterialTheme.colorScheme.primary)
                }

                Text(
                    "${item.cantidad}",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                IconButton(
                    onClick = { onActualizarCantidad(item.cantidad + 1) },
                    modifier = Modifier
                        .size(28.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(Icons.Default.Add, "Añadir 1", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun CarritoBottomBar(
    total: Double,
    onPagarClick: () -> Unit // <-- ¡LA DEFINICIÓN ESTÁ AQUÍ Y ES CORRECTA!
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Total: $${"%.0f".format(total)}", // Formatea el total
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = onPagarClick) {
                Text("Pagar")
            }
        }
    }
}

// --- Función auxiliar para imágenes ---
@DrawableRes
@Composable
private fun painterResourceFromName(context: Context, resName: String): Int {
    return try {
        val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
        if (resId == 0) R.drawable.ic_launcher_background else resId
    } catch (e: Exception) {
        R.drawable.ic_launcher_background
    }
}