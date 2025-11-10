package com.example.pasteleriaapp.ui.screen.home

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home // <-- NUEVO ICONO para Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.R
import com.example.pasteleriaapp.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onNavigateToAuth: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToCatalogo: () -> Unit,
    onNavigateToNosotros: () -> Unit,
    onNavigateToCarrito: () -> Unit,
    onNavigateToBlog: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val state by authViewModel.uiState.collectAsState()
    val usuario = state.usuarioActual

    LaunchedEffect(state.logoutSuccess) {
        if (state.logoutSuccess) {
            authViewModel.resetNavegacion()
            onLogoutSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pastelería Mil Sabores") },
                actions = {
                    // --- Botón de Carrito (siempre visible) ---
                    IconButton(onClick = onNavigateToCarrito) {
                        Icon(Icons.Default.ShoppingCart, "Ver carrito")
                    }

                    if (usuario != null) {
                        // --- Botón de Perfil (si está logueado) ---
                        IconButton(onClick = onNavigateToPerfil) {
                            Icon(Icons.Default.Person, "Mi Perfil")
                        }
                        // --- Botón de Cerrar Sesión (si está logueado) ---
                        IconButton(onClick = { authViewModel.logout() }) {
                            Icon(Icons.Default.ExitToApp, "Cerrar Sesión")
                        }
                    } else {
                        // --- Botón de Inicio de Sesión/Registro (si no está logueado) ---
                        IconButton(onClick = onNavigateToAuth) {
                            Icon(Icons.Default.Person, "Iniciar Sesión") // O un icono de 'login' si existe
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()) // Permite el scroll si hay mucho contenido
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre las tarjetas
        ) {
            Text(
                text = if (usuario != null) "¡Hola, ${usuario.nombre}!" else "¡Bienvenido!",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // --- Tarjeta para "Catálogo de Productos" ---
            ActionButtonCard(
                imageName = "home", // Usamos 'home.jpg' para Catálogo
                text = "Catálogo de Productos",
                onClick = onNavigateToCatalogo
            )

            // --- Tarjeta para "Blog de Repostería" ---
            ActionButtonCard(
                imageName = "blog", // Usamos 'blog.jpg'
                text = "Blog de Repostería",
                onClick = onNavigateToBlog
            )

            // --- Tarjeta para "Nosotros" ---
            ActionButtonCard(
                imageName = "nosotros", // Usamos 'nosotros.jpg'
                text = "Nosotros",
                onClick = onNavigateToNosotros
            )

            // Ya no se necesita un botón de login aquí porque está en la TopBar
            // Si quieres un mensaje adicional o un CTA, podrías ponerlo aquí.
        }
    }
}

/**
 * Composable reutilizable para crear las tarjetas con imagen y texto superpuesto.
 */
@Composable
fun ActionButtonCard(
    imageName: String,
    text: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = painterResourceFromName(context, imageName)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // Altura fija para las tarjetas
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart // Alinea el texto a abajo-izquierda
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = text,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Degradado para que el texto sea legible
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp) // Altura del degradado
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


/**
 * Función auxiliar para obtener un ID de drawable a partir de su nombre (String).
 */
@DrawableRes
@Composable
private fun painterResourceFromName(context: Context, resName: String): Int {
    return try {
        val cleanResName = resName.substringBefore(".")
        val resId = context.resources.getIdentifier(cleanResName, "drawable", context.packageName)
        if (resId == 0) {
            R.drawable.ic_launcher_background
        } else {
            resId
        }
    } catch (e: Exception) {
        R.drawable.ic_launcher_background
    }
}