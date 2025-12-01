package com.example.pasteleriaapp.ui.screen.productos

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.pasteleriaapp.util.ImageStorageManager
import com.example.pasteleriaapp.ui.viewmodel.ProductoFormViewModel
import java.io.File
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    viewModel: ProductoFormViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var guardandoImagenLocal by remember { mutableStateOf(false) }

    val localImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            guardandoImagenLocal = true
            coroutineScope.launch {
                val savedPath = ImageStorageManager.saveImageFromUri(context, uri, "productos")
                guardandoImagenLocal = false
                if (savedPath != null) {
                    val storedUri = Uri.fromFile(File(savedPath)).toString()
                    viewModel.onImagenChange(storedUri)
                } else {
                    viewModel.mostrarError("No se pudo guardar la imagen seleccionada.")
                }
            }
        }
    }

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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
            val categorias = state.categoriasDisponibles
            val categoriaSeleccionada = categorias.firstOrNull { it.idCategoria == state.idCategoria }

            if (categorias.isEmpty()) {
                Text(
                    text = "No hay categorías disponibles. Debes crear una antes de registrar productos.",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(12.dp))
            }

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    if (categorias.isNotEmpty()) {
                        expanded = !expanded
                    }
                }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada?.nombreCategoria ?: "Selecciona una categoría",
                    onValueChange = {},
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    readOnly = true,
                    enabled = categorias.isNotEmpty(),
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.nombreCategoria) },
                            onClick = {
                                expanded = false
                                viewModel.onCategoriaSeleccionada(categoria.idCategoria)
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = state.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.codigo,
                onValueChange = viewModel::onCodigoChange,
                label = { Text("Código") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))

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

            OutlinedTextField(
                value = state.descripcion,
                onValueChange = viewModel::onDescripcionChange,
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.imagen,
                onValueChange = viewModel::onImagenChange,
                label = { Text("Imagen (URL o recurso)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = {
                    localImagePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                enabled = !guardandoImagenLocal
            ) {
                Text(if (guardandoImagenLocal) "Guardando imagen..." else "Seleccionar imagen del dispositivo")
            }
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = viewModel::guardarProducto,
                modifier = Modifier.fillMaxWidth(),
                enabled = categorias.isNotEmpty()
            ) {
                Text("Guardar")
            }

            state.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}