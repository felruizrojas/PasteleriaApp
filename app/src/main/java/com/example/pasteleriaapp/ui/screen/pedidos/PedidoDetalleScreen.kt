package com.example.pasteleriaapp.ui.screen.pedidos

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.R
import com.example.pasteleriaapp.domain.model.EstadoPedido
import com.example.pasteleriaapp.domain.model.Pedido
import com.example.pasteleriaapp.domain.model.PedidoProducto
import com.example.pasteleriaapp.domain.model.descripcion
import com.example.pasteleriaapp.domain.model.displayName
import com.example.pasteleriaapp.domain.model.progressFraction
import com.example.pasteleriaapp.domain.model.progressStep
import com.example.pasteleriaapp.domain.model.trackingEstados
import com.example.pasteleriaapp.ui.components.CatalogImage
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

                        item {
                            EstadoPedidoTimeline(pedido.estado)
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
        InfoRow(label = "Estado:", value = pedido.estado.displayName())
        Text(
            pedido.estado.descripcion(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        InfoRow(label = "Fecha Pedido:", value = dateFormatter.format(Date(pedido.fechaPedido)))
        InfoRow(label = "Fecha Entrega:", value = pedido.fechaEntregaPreferida)
        val formatoMoneda = remember {
            java.text.NumberFormat.getNumberInstance(java.util.Locale("es", "CL")).apply {
                maximumFractionDigits = 0
                isGroupingUsed = true
            }
        }
        InfoRow(label = "Total Pagado:", value = "$${formatoMoneda.format(pedido.total)}")
    }
}

@Composable
private fun ProductoPedidoRow(producto: PedidoProducto) {
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
            CatalogImage(
                imagePath = producto.imagenProducto,
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

@Composable
private fun EstadoPedidoTimeline(estado: EstadoPedido) {
    val estados = trackingEstados()
    if (estado == EstadoPedido.CANCELADO) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
        }
        return
    }

    val pasoActual = estado.progressStep()
    LinearProgressIndicator(
        progress = estado.progressFraction(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        estados.forEachIndexed { index, estadoIterado ->
            val alcanzado = index <= pasoActual
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val icono = if (alcanzado) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked
                val color = if (alcanzado) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                }
                Icon(imageVector = icono, contentDescription = estadoIterado.displayName(), tint = color)
                Text(
                    estadoIterado.displayName(),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

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