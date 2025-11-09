package com.example.pasteleriaapp.ui.screen.pedidos

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.ui.state.CarritoUiState
import com.example.pasteleriaapp.ui.viewmodel.AuthViewModel
import com.example.pasteleriaapp.ui.viewmodel.PedidoViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    authViewModel: AuthViewModel,
    pedidoViewModel: PedidoViewModel,
    carritoState: CarritoUiState, // Recibimos el estado del carrito
    onPedidoCreado: () -> Unit,
    onBackClick: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val checkoutState by pedidoViewModel.checkoutState.collectAsState()
    val usuario = authState.usuarioActual
    val context = LocalContext.current

    // --- Lógica del DatePicker ---
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            pedidoViewModel.onFechaEntregaChange("$day/${month + 1}/$year")
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    // No permitir fechas pasadas
    datePickerDialog.datePicker.minDate = System.currentTimeMillis()
    // --- Fin Lógica DatePicker ---

    LaunchedEffect(checkoutState.pedidoCreadoId) {
        if (checkoutState.pedidoCreadoId != null) {
            Toast.makeText(context, "¡Pedido realizado con éxito!", Toast.LENGTH_LONG).show()
            pedidoViewModel.resetCheckoutState()
            onPedidoCreado() // Navega a Home
        }
    }

    LaunchedEffect(checkoutState.error) {
        checkoutState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            pedidoViewModel.resetCheckoutState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finalizar Compra") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (usuario == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: Debes iniciar sesión para comprar.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Resumen del Pedido", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Total a Pagar: $${"%.0f".format(carritoState.precioTotal)}", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(24.dp))

            Text("Información de Entrega", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Dirección: ${usuario.direccion}, ${usuario.comuna}, ${usuario.region}")
            Text("Recibe: ${usuario.nombre} ${usuario.apellidos}")

            Spacer(Modifier.height(24.dp))

            // --- Selector de Fecha ---
            OutlinedTextField(
                value = checkoutState.fechaEntrega,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de Entrega Preferida") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, "Seleccionar fecha")
                    }
                }
            )
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    pedidoViewModel.crearPedido(
                        idUsuario = usuario.idUsuario,
                        items = listOf(carritoState), // Pasamos el estado
                        total = carritoState.precioTotal
                    )
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !checkoutState.estaCargando
            ) {
                if (checkoutState.estaCargando) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Confirmar Pedido y Pagar")
                }
            }
        }
    }
}