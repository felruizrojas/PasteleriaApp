package com.example.pasteleriaapp.ui.screen.pedidos

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.pasteleriaapp.core.pricing.PricingCalculator
import com.example.pasteleriaapp.core.pricing.PricingSummary
import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.model.Usuario
import com.example.pasteleriaapp.ui.state.CarritoUiState
import com.example.pasteleriaapp.ui.components.AppScaffold
import com.example.pasteleriaapp.ui.components.AppTopBarActions
import com.example.pasteleriaapp.ui.viewmodel.AuthViewModel
import com.example.pasteleriaapp.ui.viewmodel.PedidoViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CheckoutScreen(
    authViewModel: AuthViewModel,
    pedidoViewModel: PedidoViewModel,
    carritoState: CarritoUiState,
    onPedidoCreado: () -> Unit,
    onBackClick: () -> Unit,
    badgeCount: Int,
    isLoggedIn: Boolean,
    topBarActions: AppTopBarActions,
    onLogout: (() -> Unit)?
) {
    val authState by authViewModel.uiState.collectAsState()
    val checkoutState by pedidoViewModel.checkoutState.collectAsState()
    val usuario = authState.usuarioActual
    val context = LocalContext.current
    val pricing = remember(carritoState.items, usuario) {
        PricingCalculator.calcularResumen(carritoState.items, usuario)
    }

    var mostrarOpcionesBoleta by remember { mutableStateOf(false) }
    var uriBoleta by remember { mutableStateOf<Uri?>(null) }
    var itemsParaBoleta by remember { mutableStateOf<List<CarritoItem>>(emptyList()) }

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
    datePickerDialog.datePicker.minDate = System.currentTimeMillis()

    LaunchedEffect(checkoutState.pedidoCreadoId) {
        val idGenerado = checkoutState.pedidoCreadoId
        if (idGenerado != null && usuario != null) {
            val uri = generarBoletaPdf(
                context = context,
                pedidoId = idGenerado,
                usuario = usuario,
                items = itemsParaBoleta
            )
            if (uri != null) {
                uriBoleta = uri
            }
            mostrarOpcionesBoleta = true
            Toast.makeText(context, "¡Pedido realizado con éxito!", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(checkoutState.error) {
        checkoutState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            pedidoViewModel.resetCheckoutState()
        }
    }

    AppScaffold(
        badgeCount = badgeCount,
        isLoggedIn = isLoggedIn,
        topBarActions = topBarActions,
        pageTitle = "Finalizar Compra",
        onBackClick = onBackClick,
        onLogout = onLogout
    ) { paddingValues ->
        if (usuario == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: Debes iniciar sesión para comprar.")
            }
            return@AppScaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Resumen del Pedido", style = MaterialTheme.typography.titleLarge)
            ResumenPago(pricing = pricing)

            Text("Información de Entrega", style = MaterialTheme.typography.titleLarge)
            Text("Dirección: ${usuario.direccion}, ${usuario.comuna}, ${usuario.region}")
            Text("Recibe: ${usuario.nombre} ${usuario.apellidos}")

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

            Text("Datos de Pago", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = formatCardNumber(checkoutState.numeroTarjeta),
                onValueChange = { pedidoViewModel.onNumeroTarjetaChange(it) },
                label = { Text("Número de tarjeta") },
                placeholder = { Text("1234 5678 9012 3456") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = checkoutState.nombreTitular,
                onValueChange = { pedidoViewModel.onNombreTitularChange(it.uppercase()) },
                label = { Text("Nombre titular") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = checkoutState.mesExpiracion,
                    onValueChange = { pedidoViewModel.onMesExpiracionChange(it) },
                    label = { Text("Mes") },
                    placeholder = { Text("MM") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = checkoutState.anioExpiracion,
                    onValueChange = { pedidoViewModel.onAnioExpiracionChange(it) },
                    label = { Text("Año") },
                    placeholder = { Text("AAAA") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = checkoutState.cvv,
                    onValueChange = { pedidoViewModel.onCvvChange(it) },
                    label = { Text("CVV") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            Text(
                "Los datos de pago se utilizan solo para esta simulación y no se almacenan.",
                style = MaterialTheme.typography.bodySmall
            )

            Button(
                onClick = {
                    itemsParaBoleta = carritoState.items.map { it.copy() }
                    pedidoViewModel.crearPedido(
                        idUsuario = usuario.idUsuario,
                        items = carritoState.items,
                        total = pricing.total
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !checkoutState.estaCargando
            ) {
                if (checkoutState.estaCargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Confirmar Pedido y Generar Boleta")
                }
            }
        }
    }

    if (mostrarOpcionesBoleta) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Boleta generada") },
            text = {
                Text(
                    "Tu boleta está lista. ¿Qué deseas hacer?",
                    textAlign = TextAlign.Start
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val uri = uriBoleta
                    if (uri != null) {
                        compartirBoleta(context, uri)
                    } else {
                        Toast.makeText(context, "No fue posible compartir la boleta", Toast.LENGTH_LONG).show()
                    }
                }) {
                    Text("Compartir")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        val uri = uriBoleta
                        if (uri != null && usuario != null) {
                            enviarBoletaPorCorreo(context, uri, usuario.correo)
                        } else {
                            Toast.makeText(context, "No fue posible adjuntar la boleta", Toast.LENGTH_LONG).show()
                        }
                    }) {
                        Text("Enviar correo")
                    }
                    TextButton(onClick = {
                        mostrarOpcionesBoleta = false
                        pedidoViewModel.resetCheckoutState()
                        onPedidoCreado()
                    }) {
                        Text("Listo")
                    }
                }
            }
        )
    }
}

@Composable
private fun ResumenPago(pricing: PricingSummary) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            "Subtotal: $${pricing.subtotalFormateado}",
            style = MaterialTheme.typography.bodyLarge
        )
        if (pricing.tieneDescuento) {
            Text(
                "Descuentos: -$${pricing.descuentoFormateado}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Text(
            "Total a pagar: $${pricing.totalFormateado}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatCardNumber(numero: String): String {
    return numero.chunked(4).joinToString(" ")
}

private fun generarBoletaPdf(
    context: Context,
    pedidoId: Long,
    usuario: Usuario,
    items: List<CarritoItem>
): Uri? {
    if (items.isEmpty()) return null
    val pricing = PricingCalculator.calcularResumen(items, usuario)
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    val titlePaint = Paint().apply {
        textSize = 22f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val bodyPaint = Paint().apply {
        textSize = 14f
    }

    var y = 60f
    canvas.drawText("Pastelería Mil Sabores", 40f, y, titlePaint)
    y += 30f
    canvas.drawText("Boleta Pedido #$pedidoId", 40f, y, titlePaint)
    y += 40f

    canvas.drawText("Cliente: ${usuario.nombre} ${usuario.apellidos}", 40f, y, bodyPaint)
    y += 20f
    canvas.drawText("RUN: ${usuario.run}", 40f, y, bodyPaint)
    y += 20f
    canvas.drawText("Correo: ${usuario.correo}", 40f, y, bodyPaint)
    y += 20f
    canvas.drawText("Dirección: ${usuario.direccion}, ${usuario.comuna}", 40f, y, bodyPaint)
    y += 30f

    canvas.drawText("Detalle de productos:", 40f, y, bodyPaint)
    y += 20f
    items.forEach { item ->
        canvas.drawText("${item.cantidad}x ${item.nombreProducto} - $${"%.0f".format(item.precioProducto)}", 40f, y, bodyPaint)
        y += 20f
        if (item.mensajePersonalizado.isNotBlank()) {
            canvas.drawText("Dedicatoria: ${item.mensajePersonalizado}", 60f, y, bodyPaint)
            y += 20f
        }
    }

    y += 10f
    canvas.drawText("Subtotal: $${pricing.subtotalFormateado}", 40f, y, bodyPaint)
    y += 20f
    if (pricing.tieneDescuento) {
        canvas.drawText("Descuentos: -$${pricing.descuentoFormateado}", 40f, y, bodyPaint)
        y += 20f
    }
    canvas.drawText("Total pagado: $${pricing.totalFormateado}", 40f, y, bodyPaint)

    pdfDocument.finishPage(page)

    val boletasDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "boletas")
    if (!boletasDir.exists()) {
        boletasDir.mkdirs()
    }

    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
    val file = File(boletasDir, "boleta_pedido_${pedidoId}_$timestamp.pdf")

    return try {
        FileOutputStream(file).use { output ->
            pdfDocument.writeTo(output)
        }
        val authority = "${context.packageName}.fileprovider"
        FileProvider.getUriForFile(context, authority, file)
    } catch (e: IOException) {
        null
    } finally {
        pdfDocument.close()
    }
}

private fun compartirBoleta(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir boleta"))
}

private fun enviarBoletaPorCorreo(context: Context, uri: Uri, correoDestino: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(correoDestino))
        putExtra(Intent.EXTRA_SUBJECT, "Boleta Pastelería Mil Sabores")
        putExtra(Intent.EXTRA_TEXT, "Adjuntamos la boleta de tu compra. ¡Gracias por preferirnos!")
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Enviar boleta por correo"))
}