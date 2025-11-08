package com.example.pasteleriaapp.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Pantalla de inicio (HomeScreen).
 * Es la primera pantalla que ve el usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // Lambda para ser llamado cuando el usuario quiera ver las categorías
    onVerCategoriasClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Pastelería Mil Sabores") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp), // Padding interno adicional
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¡Bienvenido!",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Explora nuestro delicioso catálogo de productos.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onVerCategoriasClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Categorías de Productos")
            }

            // Aquí podrías agregar más botones, como "Ver Carrito" o "Mi Cuenta"
        }
    }
}