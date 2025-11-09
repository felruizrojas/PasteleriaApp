package com.example.pasteleriaapp.ui.screen.pedidos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.domain.model.Pedido
import com.example.pasteleriaapp.ui.viewmodel.AuthViewModel
import com.example.pasteleriaapp.ui.viewmodel.PedidoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPedidosScreen(
    authViewModel: AuthViewModel,
    pedidoViewModel: PedidoViewModel,
    onPedidoClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val pedidosState by pedidoViewModel.misPedidosState.collectAsState()

    // Cargar pedidos cuando la pantalla se inicia
    LaunchedEffect(authState.usuarioActual) {
        authState.usuarioActual?.let {
            pedidoViewModel.cargarMisPedidos(it.idUsuario)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Pedidos") },
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
                pedidosState.estaCargando -> CircularProgressIndicator()
                pedidosState.error != null -> Text("Error: ${pedidosState.error}")
                pedidosState.pedidos.isNotEmpty() -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(pedidosState.pedidos) { pedido ->
                            PedidoRow(pedido = pedido, onClick = { onPedidoClick(pedido.idPedido) })
                        }
                    }
                }
                else -> {
                    Text("Aún no has realizado pedidos.")
                }
            }
        }
    }
}

@Composable
fun PedidoRow(pedido: Pedido, onClick: () -> Unit) {
    val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale("es", "ES"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Pedido #${pedido.idPedido}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            InfoRow(label = "Estado:", value = pedido.estado.name)
            InfoRow(label = "Fecha Pedido:", value = dateFormatter.format(Date(pedido.fechaPedido)))
            InfoRow(label = "Entrega:", value = pedido.fechaEntregaPreferida)
            InfoRow(label = "Total:", value = "$${"%.0f".format(pedido.total)}")
        }
    }
}

// (Podríamos mover esto a un archivo 'shared' si lo usamos mucho)
@Composable
private fun InfoRow(label: String, value: String) {
    Row {
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