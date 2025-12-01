package com.example.pasteleriaapp.ui.screen.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.domain.model.Categoria
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.ui.viewmodel.AdminCatalogViewModel
import com.example.pasteleriaapp.util.ImageStorageManager
import java.io.File
import kotlinx.coroutines.launch

@Composable
fun AdminCatalogContent(
    viewModel: AdminCatalogViewModel,
    onAgregarProducto: (Int) -> Unit,
    onEditarProducto: (Int, Int) -> Unit,
    onNavigateAdmin: (String) -> Unit = {},
    modifier: Modifier = Modifier
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

    var mostrarDialogoCategoria by remember { mutableStateOf(false) }
    var categoriaEnEdicion by remember { mutableStateOf<Categoria?>(null) }
    var nombreCategoria by remember { mutableStateOf("") }
    var imagenCategoria by remember { mutableStateOf("") }

    fun abrirDialogoNuevaCategoria() {
        categoriaEnEdicion = null
        nombreCategoria = ""
        imagenCategoria = ""
        mostrarDialogoCategoria = true
    }

    fun abrirDialogoEditarCategoria(categoria: Categoria) {
        categoriaEnEdicion = categoria
        nombreCategoria = categoria.nombreCategoria
        imagenCategoria = categoria.imagenCategoria
        mostrarDialogoCategoria = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Gestión de catálogo",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Botón hamburguesa para opciones de administración
        var menuExpanded by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú administración"
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(text = { Text("Usuarios") }, onClick = {
                    menuExpanded = false
                    onNavigateAdmin("usuarios")
                })
                DropdownMenuItem(text = { Text("Productos") }, onClick = {
                    menuExpanded = false
                    onNavigateAdmin("productos")
                })
                DropdownMenuItem(text = { Text("Pedidos") }, onClick = {
                    menuExpanded = false
                    onNavigateAdmin("pedidos")
                })
                DropdownMenuItem(text = { Text("Tienda") }, onClick = {
                    menuExpanded = false
                    onNavigateAdmin("tienda")
                })
            }
        }

        if (state.isActionInProgress) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        } else {
            Spacer(Modifier.height(12.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categorías (${state.categorias.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            OutlinedButton(onClick = { abrirDialogoNuevaCategoria() }) {
                Text("Nueva categoría")
            }
        }

        Spacer(Modifier.height(12.dp))

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.categorias.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay categorías registradas.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(state.categorias, key = { it.idCategoria }) { categoria ->
                    CategoriaAdminCard(
                        categoria = categoria,
                        seleccionada = categoria.idCategoria == state.categoriaSeleccionadaId,
                        onSeleccionar = { viewModel.seleccionarCategoria(categoria.idCategoria) },
                        onEditar = { abrirDialogoEditarCategoria(categoria) },
                        onAlternarBloqueo = { viewModel.alternarBloqueoCategoria(categoria) }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        val categoriaSeleccionada = state.categoriaSeleccionada
        if (categoriaSeleccionada == null) {
            Text("Selecciona una categoría para gestionar sus productos.")
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Productos (${state.productosDeCategoria.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                OutlinedButton(onClick = { onAgregarProducto(categoriaSeleccionada.idCategoria) }) {
                    Text("Nuevo producto")
                }
            }

            Spacer(Modifier.height(8.dp))

            if (state.productosDeCategoria.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Esta categoría no tiene productos registrados.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.productosDeCategoria, key = { it.idProducto }) { producto ->
                        ProductoAdminRow(
                            producto = producto,
                            onEditar = { onEditarProducto(producto.idProducto, producto.idCategoria) },
                            onAlternarBloqueo = { viewModel.alternarBloqueoProducto(producto) }
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogoCategoria) {
        DialogoCategoria(
            titulo = if (categoriaEnEdicion == null) "Nueva categoría" else "Editar categoría",
            nombre = nombreCategoria,
            imagen = imagenCategoria,
            onNombreChange = { nombreCategoria = it },
            onImagenChange = { imagenCategoria = it },
            onDismiss = { mostrarDialogoCategoria = false },
            onConfirm = {
                if (categoriaEnEdicion == null) {
                    viewModel.crearCategoria(nombreCategoria, imagenCategoria)
                } else {
                    viewModel.actualizarCategoria(categoriaEnEdicion!!, nombreCategoria, imagenCategoria)
                }
                mostrarDialogoCategoria = false
            },
            confirmLabel = if (categoriaEnEdicion == null) "Crear" else "Guardar",
            enabled = !state.isActionInProgress
        )
    }
}

@Composable
private fun CategoriaAdminCard(
    categoria: Categoria,
    seleccionada: Boolean,
    onSeleccionar: () -> Unit,
    onEditar: () -> Unit,
    onAlternarBloqueo: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Transparent)
            .clickable(onClick = onSeleccionar),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = if (seleccionada) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = categoria.nombreCategoria,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (categoria.estaBloqueada) {
                        Text(
                            text = "Bloqueada",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable(onClick = onEditar)
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onSeleccionar) {
                    Text("Ver productos")
                }
                OutlinedButton(onClick = onAlternarBloqueo) {
                    Text(if (categoria.estaBloqueada) "Desbloquear" else "Bloquear")
                }
            }
        }
    }
}

@Composable
private fun ProductoAdminRow(
    producto: Producto,
    onEditar: () -> Unit,
    onAlternarBloqueo: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = producto.nombreProducto,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Código: ${producto.codigoProducto}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (producto.estaBloqueado) {
                        Text(
                            text = "Bloqueado",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                Text(
                    text = "$${producto.precioProducto}" ,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onEditar) {
                    Text("Editar")
                }
                OutlinedButton(onClick = onAlternarBloqueo) {
                    Text(if (producto.estaBloqueado) "Desbloquear" else "Bloquear")
                }
            }
        }
    }
}

@Composable
private fun DialogoCategoria(
    titulo: String,
    nombre: String,
    imagen: String,
    onNombreChange: (String) -> Unit,
    onImagenChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmLabel: String,
    enabled: Boolean
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var guardandoImagenLocal by remember { mutableStateOf(false) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            guardandoImagenLocal = true
            coroutineScope.launch {
                val savedPath = ImageStorageManager.saveImageFromUri(context, uri, "categorias")
                guardandoImagenLocal = false
                if (savedPath != null) {
                    val storedUri = Uri.fromFile(File(savedPath)).toString()
                    onImagenChange(storedUri)
                } else {
                    Toast.makeText(context, "No se pudo guardar la imagen seleccionada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = { if (enabled) onDismiss() },
        title = { Text(titulo) },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = onNombreChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = imagen,
                    onValueChange = onImagenChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    label = { Text("Imagen (recurso o URL)") },
                    singleLine = true
                )
                TextButton(
                    onClick = {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    enabled = enabled && !guardandoImagenLocal
                ) {
                    Text(if (guardandoImagenLocal) "Guardando imagen..." else "Seleccionar imagen del dispositivo")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = enabled) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = enabled) {
                Text("Cancelar")
            }
        }
    )
}
