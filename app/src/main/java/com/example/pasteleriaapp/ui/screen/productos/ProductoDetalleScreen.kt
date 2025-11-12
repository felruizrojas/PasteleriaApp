package com.example.pasteleriaapp.ui.screen.productos

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.R
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.ui.components.AppScaffold
import com.example.pasteleriaapp.ui.components.AppTopBarActions
import com.example.pasteleriaapp.ui.screen.auth.VoiceTextField
import com.example.pasteleriaapp.ui.viewmodel.ProductoDetalleViewModel

@Composable
fun ProductoDetalleScreen(
    viewModel: ProductoDetalleViewModel,
    onBackClick: () -> Unit,
    onEditProductoClick: (Int) -> Unit,
    badgeCount: Int,
    isLoggedIn: Boolean,
    topBarActions: AppTopBarActions,
    onLogout: (() -> Unit)?
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.itemAgregado) {
        if (state.itemAgregado) {
            val nombre = state.producto?.nombreProducto ?: "Producto"
            Toast.makeText(
                context,
                "$nombre añadido al carrito",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.eventoItemAgregadoMostrado()
        }
    }

    AppScaffold(
        badgeCount = badgeCount,
        isLoggedIn = isLoggedIn,
        topBarActions = topBarActions,
        pageTitle = "Detalle del Producto",
        onBackClick = onBackClick,
        headerActions = {
            state.producto?.let { producto ->
                IconButton(onClick = { onEditProductoClick(producto.idProducto) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar Producto")
                }
            }
        },
        onLogout = onLogout
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            val producto = state.producto
            when {
                state.estaCargando -> CircularProgressIndicator()
                state.error != null -> Text("Error: ${state.error}")
                producto != null -> ProductoDetalle(
                    producto = producto,
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

    var mensajePersonalizado by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "Imagen de ${producto.nombreProducto}",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = producto.nombreProducto,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = "$${"%.0f".format(producto.precioProducto)}",
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
            Spacer(Modifier.height(24.dp))

            VoiceTextField(
                value = mensajePersonalizado,
                onValueChange = { mensajePersonalizado = it },
                label = "Mensaje Personalizado (Opcional)",
                modifier = Modifier.fillMaxWidth(),
                singleLine = false
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    onAgregarAlCarrito(mensajePersonalizado)
                    mensajePersonalizado = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Añadir al Carrito")
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    compartirProducto(context, producto, mensajePersonalizado)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Compartir")
            }
        }
    }
}

private fun compartirProducto(context: Context, producto: Producto, mensaje: String) {
    val textoCompartir = """
        ¡Mira este increíble producto de Pastelería Mil Sabores!

        ${producto.nombreProducto} - $${"%.0f".format(producto.precioProducto)}

        ${producto.descripcionProducto}

        ${if (mensaje.isNotBlank()) "Mi mensaje: $mensaje" else ""}
	""".trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Producto: ${producto.nombreProducto}")
        putExtra(Intent.EXTRA_TEXT, textoCompartir)
    }

    context.startActivity(Intent.createChooser(intent, "Compartir producto en..."))
}

@DrawableRes
private fun painterResourceFromName(context: Context, resName: String): Int {
    return try {
        val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
        if (resId == 0) R.drawable.ic_launcher_background else resId
    } catch (e: Exception) {
        R.drawable.ic_launcher_background
    }
}
