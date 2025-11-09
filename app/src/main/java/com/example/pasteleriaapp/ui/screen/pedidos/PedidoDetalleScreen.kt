package com.example.pasteleriaapp.ui.screen.pedidos

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.R
import com.example.pasteleriaapp.domain.model.Pedido
import com.example.pasteleriaapp.domain.model.PedidoProducto
import com.example.pasteleriaapp.ui.viewmodel.PedidoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidoDetalleScreen(
    idPedido: Int,
    pedidoViewModel: PedidoViewModel,
    onBackClick: () -> Unit
) {
    val state by pedidoViewModel.pedidoDetalleState.collectAsState()
    val pedido = state.pedido

    LaunchedEffect(idPedido) {
        pedidoViewModel.cargarDetallePedido(idPedido)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (pedido != null) "Detalle Pedido #${pedido.idPedido}" else "Detalle del Pedido") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
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
                state.estaCargando -> CircularProgressIndicator()
                state.error != null -> Text("Error: ${state.error}")
                pedido != null -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // --- Sección 1: Detalles del Pedido ---
                        item {
                            DetallesPedidoHeader(pedido)
                        }

                        // --- Sección 2: Productos ---
                        item {
                            Text(
                                "Productos Incluidos",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                        items(state.productos) { producto ->
                            ProductoPedidoRow(producto)
                        }
                    }
                }
                else -> Text("No se encontró el pedido.")
            }
        }
    }
}

@Composable
private fun DetallesPedidoHeader(pedido: Pedido) {
    val dateFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("es", "ES"))

    Column {
        Text("Resumen del Pedido", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        InfoRow(label = "Estado:", value = pedido.estado.name)
        InfoRow(label = "Fecha Pedido:", value = dateFormatter.format(Date(pedido.fechaPedido)))
        InfoRow(label = "Fecha Entrega:", value = pedido.fechaEntregaPreferida)
        InfoRow(label = "Total Pagado:", value = "$${"%.0f".format(pedido.total)}")
    }
}

@Composable
private fun ProductoPedidoRow(producto: PedidoProducto) {
    val context = LocalContext.current
    val imageResId = painterResourceFromName(context, producto.imagenProducto)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = producto.nombreProducto,
                modifier = Modifier
                    .size(60.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${producto.cantidad}x ${producto.nombreProducto}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("Precio unit: $${producto.precioProducto}")
                if (producto.mensajePersonalizado.isNotBlank()) {
                    Text(
                        "\"${producto.mensajePersonalizado}\"",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}

// --- Funciones auxiliares (duplicadas de otras pantallas) ---
@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(bottom = 4.dp)) {
        Text(
            text = label,
            modifier = Modifier.width(120.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

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