package com.example.pasteleriaapp.ui.screen.productos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.ui.viewmodel.ProductoFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    viewModel: ProductoFormViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Navega hacia atrás automáticamente cuando el guardado es exitoso
    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.tituloPantalla) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->

        if (state.estaCargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Campo Nombre
            OutlinedTextField(
                value = state.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))

            // Campo Código
            OutlinedTextField(
                value = state.codigo,
                onValueChange = viewModel::onCodigoChange,
                label = { Text("Código") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))

            // Fila para Precio y Stock
            Row(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.precio,
                    onValueChange = viewModel::onPrecioChange,
                    label = { Text("Precio") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Text("$") }
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = state.stock,
                    onValueChange = viewModel::onStockChange,
                    label = { Text("Stock") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Spacer(Modifier.height(8.dp))

            // Campo Descripción
            OutlinedTextField(
                value = state.descripcion,
                onValueChange = viewModel::onDescripcionChange,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
            )
            Spacer(Modifier.height(16.dp))

            // Botón Guardar
            Button(
                onClick = viewModel::guardarProducto,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            // Mostrar error si existe
            state.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}