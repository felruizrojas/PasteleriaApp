package com.example.pasteleriaapp.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    onLogoutSuccess: () -> Unit
) {
    val state by authViewModel.uiState.collectAsState()
    val usuario = state.usuarioActual

    // Maneja el evento de logout
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
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (usuario != null) "¡Hola, ${usuario.nombre}!" else "¡Bienvenido!",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onNavigateToCatalogo,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Catálogo de Productos")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateToNosotros,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Nosotros")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Botón Condicional de Login ---
            if (usuario == null) {
                Button(
                    onClick = onNavigateToAuth,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Inicio de Sesión")
                }
            }
        }
    }
}