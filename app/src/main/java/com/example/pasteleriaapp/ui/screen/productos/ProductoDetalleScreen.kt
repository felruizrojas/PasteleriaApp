package com.example.pasteleriaapp.ui.screen.productos

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.pasteleriaapp.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.ui.viewmodel.ProductoDetalleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoDetalleScreen(
    viewModel: ProductoDetalleViewModel,
    onBackClick: () -> Unit,
    onEditProductoClick: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // LaunchedEffect para mostrar el Toast
    LaunchedEffect(state.itemAgregado) {
        if (state.itemAgregado) {
            Toast.makeText(
                context,
                "${state.producto?.nombreProducto} añadido al carrito",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.eventoItemAgregadoMostrado() // Resetea el evento
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.producto?.nombreProducto ?: "Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    state.producto?.let {
                        IconButton(onClick = { onEditProductoClick(it.idProducto) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Producto")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            when {
                state.estaCargando -> CircularProgressIndicator()
                state.error != null -> Text("Error: ${state.error}")
                state.producto != null -> ProductoDetalle(
                    state.producto!!,
                    // Pasamos la función del ViewModel
                    onAgregarAlCarrito = { mensaje ->
                        viewModel.agregarAlCarrito(mensaje)
                    }
                )
                else -> Text("Producto no encontrado.")
            }
        }
    }
}

@Composable
private fun ProductoDetalle(
    producto: Producto,
    onAgregarAlCarrito: (String) -> Unit
) {
    val context = LocalContext.current
    val imageResId = painterResourceFromName(context, producto.imagenProducto)

    // --- CORREGIDO: Solo hay UNA declaración ---
    var mensajePersonalizado by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()), // Permite scrollear
        horizontalAlignment = Alignment.Start
    ) {

        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "Imagen de ${producto.nombreProducto}",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f), // Aspecto 16:9
            contentScale = ContentScale.Crop
        )

        // Columna para el contenido de texto con padding
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = producto.nombreProducto,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = "$${producto.precioProducto}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = producto.descripcionProducto,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Stock disponible: ${producto.stockProducto} unidades",
                style = MaterialTheme.typography.bodySmall
            )

            // --- INICIO DE NUEVOS COMPONENTES ---

            Spacer(Modifier.height(24.dp)) // Un espacio extra

            // 1. Área de Mensaje Personalizado
            OutlinedTextField(
                value = mensajePersonalizado,
                onValueChange = { mensajePersonalizado = it },
                label = { Text("Mensaje Personalizado (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true // Cambia a false si quieres múltiples líneas
            )

            Spacer(Modifier.height(16.dp))

            // 2. Botón Añadir al Carrito
            Button(
                onClick = {
                    onAgregarAlCarrito(mensajePersonalizado)
                    mensajePersonalizado = "" // Limpia el campo
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Añadir al Carrito")
            }

            Spacer(Modifier.height(8.dp))

            // 3. Botón Compartir
            OutlinedButton(
                onClick = {
                    // Llama a la función auxiliar para compartir
                    compartirProducto(context, producto, mensajePersonalizado)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Compartir")
            }

            // --- FIN DE NUEVOS COMPONENTES ---
        }
    }
}

/**
 * --- NUEVA FUNCIÓN AUXILIAR ---
 * Crea un Intent de Android para compartir el texto del producto.
 */
private fun compartirProducto(context: Context, producto: Producto, mensaje: String) {
    val textoCompartir = """
        ¡Mira este increíble producto de Pastelería Mil Sabores!
        
        ${producto.nombreProducto} - $${producto.precioProducto}
        
        ${producto.descripcionProducto}
        
        ${if (mensaje.isNotBlank()) "Mi mensaje: $mensaje" else ""}
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Producto: ${producto.nombreProducto}")
        putExtra(Intent.EXTRA_TEXT, textoCompartir)
    }

    context.startActivity(
        Intent.createChooser(intent, "Compartir producto en...")
    )
}

/**
 * Función auxiliar para obtener un ID de drawable a partir de su nombre (String).
 */
@DrawableRes
@Composable
private fun painterResourceFromName(context: Context, resName: String): Int {
    return try {
        val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
        if (resId == 0) {
            R.drawable.ic_launcher_background
        } else {
            resId
        }
    } catch (e: Exception) {
        R.drawable.ic_launcher_background
    }
}