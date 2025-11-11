package com.example.pasteleriaapp.ui.screen.admin

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.domain.model.EstadoPedido
import com.example.pasteleriaapp.domain.model.descripcion
import com.example.pasteleriaapp.domain.model.displayName
import com.example.pasteleriaapp.ui.components.AppScaffold
import com.example.pasteleriaapp.ui.components.AppTopBarActions
import com.example.pasteleriaapp.ui.state.PedidoAdminItem
import com.example.pasteleriaapp.ui.viewmodel.AdminPedidosViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPedidosScreen(
    viewModel: AdminPedidosViewModel,
    onBackClick: () -> Unit,
    onVerDetalle: (Int) -> Unit,
    badgeCount: Int,
    isLoggedIn: Boolean,
    topBarActions: AppTopBarActions,
    onLogout: (() -> Unit)?
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.mensaje, state.error) {
        val mensaje = state.mensaje ?: state.error
        if (!mensaje.isNullOrBlank()) {
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
            viewModel.limpiarMensajes()
        }
    }

    AppScaffold(
        badgeCount = badgeCount,
        isLoggedIn = isLoggedIn,
        topBarActions = topBarActions,
        pageTitle = "Gestión de pedidos",
        onLogout = onLogout
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            TextButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                Spacer(Modifier.width(8.dp))
                Text("Volver")
            }

            if (state.isActionInProgress) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            } else {
                Spacer(Modifier.height(8.dp))
            }

            EstadoPedidoFilterRow(
                filtroSeleccionado = state.filtroEstado,
                onFiltroChange = viewModel::onFiltroEstadoChange
            )

            Spacer(Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.pedidosFiltrados.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay pedidos para mostrar con el filtro actual.")
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(state.pedidosFiltrados, key = { it.idPedido }) { item ->
                            PedidoAdminCard(
                                item = item,
                                onCambiarEstado = { estado -> viewModel.actualizarEstadoPedido(item.idPedido, estado) },
                                onVerDetalle = { onVerDetalle(item.idPedido) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EstadoPedidoFilterRow(
    filtroSeleccionado: EstadoPedido?,
    onFiltroChange: (EstadoPedido?) -> Unit
) {
    val opciones = remember { listOf<EstadoPedido?>(null) + EstadoPedido.values().toList() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        opciones.forEach { opcion ->
            val seleccionado = filtroSeleccionado == opcion
            val etiqueta = opcion?.displayName() ?: "Todos"
            FilterChip(
                selected = seleccionado,
                onClick = { onFiltroChange(if (seleccionado) null else opcion) },
                label = { Text(etiqueta) },
                modifier = Modifier,
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = seleccionado
                )
            )
        }
    }
}

@Composable
private fun PedidoAdminCard(
    item: PedidoAdminItem,
    onCambiarEstado: (EstadoPedido) -> Unit,
    onVerDetalle: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "ES")) }
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }
    var menuAbierto by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Pedido #${item.idPedido}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text("Cliente: ${item.nombreCliente}", style = MaterialTheme.typography.bodyMedium)
            item.correoCliente?.let {
                Text("Correo: $it", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Estado: ${item.estado.displayName()}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.estado.descripcion(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Total: ${currencyFormatter.format(item.total)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Pedido: ${dateFormatter.format(Date(item.pedido.fechaPedido))}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Entrega: ${item.pedido.fechaEntregaPreferida}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = { menuAbierto = true }) {
                    Text("Cambiar estado")
                }
                DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                    EstadoPedido.values().forEach { estado ->
                        DropdownMenuItem(
                            text = { Text(estado.displayName()) },
                            onClick = {
                                menuAbierto = false
                                onCambiarEstado(estado)
                            }
                        )
                    }
                }
                TextButton(onClick = onVerDetalle) {
                    Text("Ver detalle")
                }
            }
        }
    }
}
