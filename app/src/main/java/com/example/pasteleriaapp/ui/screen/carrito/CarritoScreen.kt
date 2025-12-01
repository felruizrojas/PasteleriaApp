package com.example.pasteleriaapp.ui.screen.carrito

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pasteleriaapp.R
import com.example.pasteleriaapp.core.pricing.PricingCalculator
import com.example.pasteleriaapp.core.pricing.PricingSummary
import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.model.Usuario
import com.example.pasteleriaapp.ui.components.AppScaffold
import com.example.pasteleriaapp.ui.components.AppTopBarActions
import com.example.pasteleriaapp.ui.components.CatalogImage
import com.example.pasteleriaapp.ui.viewmodel.CarritoViewModel

@Composable
fun CarritoScreen(
    viewModel: CarritoViewModel,
    onBackClick: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    badgeCount: Int,
    isLoggedIn: Boolean,
    topBarActions: AppTopBarActions,
    onLogout: (() -> Unit)?,
    usuario: Usuario?,
    onRequireLogin: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var itemEnEdicion by remember { mutableStateOf<CarritoItem?>(null) }
    var mensajeTemporal by remember { mutableStateOf("") }
    val pricing = remember(state.items, usuario) {
        PricingCalculator.calcularResumen(state.items, usuario)
    }

    AppScaffold(
        badgeCount = badgeCount,
        isLoggedIn = isLoggedIn,
        topBarActions = topBarActions,
        pageTitle = "Mi Carrito",
        onBackClick = onBackClick,
        onLogout = onLogout,
        bottomBar = {
            if (state.hayItems) {
                CarritoBottomBar(
                    pricing = pricing,
                    onPagarClick = onNavigateToCheckout
                )
            }
        }
    ) { paddingValues ->
        if (state.requiereAutenticacion) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Debes iniciar sesión para ver tu carrito.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(onClick = onRequireLogin) {
                        Text("Iniciar sesión")
                    }
                }
            }
            return@AppScaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.hayItems) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { viewModel.limpiarCarrito() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Vaciar carrito")
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
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
                            items(state.items, key = { it.idCarrito }) { item ->
                                CarritoItemRow(
                                    item = item,
                                    onActualizarCantidad = { nuevaCantidad ->
                                        viewModel.actualizarCantidad(item, nuevaCantidad)
                                    },
                                    onEliminar = {
                                        viewModel.eliminarItem(item)
                                    },
                                    onEditarMensaje = {
                                        itemEnEdicion = item
                                        mensajeTemporal = item.mensajePersonalizado
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

    if (itemEnEdicion != null) {
        AlertDialog(
            onDismissRequest = { itemEnEdicion = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        itemEnEdicion?.let { viewModel.actualizarMensaje(it, mensajeTemporal) }
                        itemEnEdicion = null
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemEnEdicion = null }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Editar dedicatoria") },
            text = {
                OutlinedTextField(
                    value = mensajeTemporal,
                    onValueChange = { mensajeTemporal = it.take(120) },
                    label = { Text("Mensaje personalizado") },
                    supportingText = { Text("Máx. 120 caracteres. Deja vacío para quitar dedicatoria.") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}

@Composable
fun CarritoItemRow(
    item: CarritoItem,
    onActualizarCantidad: (Int) -> Unit,
    onEliminar: () -> Unit,
    onEditarMensaje: () -> Unit
) {
    val formatoMoneda = remember {
        java.text.NumberFormat.getNumberInstance(java.util.Locale("es", "CL")).apply {
            maximumFractionDigits = 0
            isGroupingUsed = true
        }
    }

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
            CatalogImage(
                imagePath = item.imagenProducto,
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
                Text("$${formatoMoneda.format(item.precioProducto)}", style = MaterialTheme.typography.bodyMedium)
                val mensaje = item.mensajePersonalizado
                AssistChip(
                    onClick = onEditarMensaje,
                    label = {
                        Text(
                            if (mensaje.isBlank()) "Agregar dedicatoria" else "\"$mensaje\"",
                            maxLines = 1
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                )
            }

            Spacer(Modifier.width(16.dp))

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
    pricing: PricingSummary,
    onPagarClick: () -> Unit
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
            Column {
                Text(
                    "Subtotal: $${pricing.subtotalFormateado}",
                    style = MaterialTheme.typography.titleMedium
                )
                if (pricing.tieneDescuento) {
                    Text(
                        "Descuentos: -$${pricing.descuentoFormateado}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Text(
                    "Total: $${pricing.totalFormateado}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(onClick = onPagarClick) {
                Text("Pagar")
            }
        }
    }
}