package com.example.pasteleriaapp.ui.screen.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.domain.model.TipoUsuario
import com.example.pasteleriaapp.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onNavigateToEdit: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToMisPedidos: () -> Unit
) {
    val state by authViewModel.uiState.collectAsState()
    val usuario = state.usuarioActual

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(Icons.Default.Edit, "Editar Perfil")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (usuario == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No se encontró el usuario.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Datos Personales", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            InfoRow(label = "Nombre:", value = "${usuario.nombre} ${usuario.apellidos}")
            InfoRow(label = "RUN:", value = usuario.run)
            InfoRow(label = "Correo:", value = usuario.correo)
            InfoRow(label = "Fecha Nacimiento:", value = usuario.fechaNacimiento)

            Spacer(Modifier.height(24.dp))

            Text("Dirección", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            InfoRow(label = "Región:", value = usuario.region)
            InfoRow(label = "Comuna:", value = usuario.comuna)
            InfoRow(label = "Dirección:", value = usuario.direccion)

            Spacer(Modifier.height(32.dp))

            OutlinedButton(
                onClick = onNavigateToMisPedidos,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mis Pedidos")
            }
            Spacer(Modifier.height(8.dp))

            // --- LÓGICA CONDICIONAL (Tu solicitud) ---
            when (usuario.tipoUsuario) {
                TipoUsuario.superAdmin, TipoUsuario.Administrador, TipoUsuario.Vendedor -> {
                    Button(
                        onClick = { /* TODO: Navegar al Panel de Admin */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Panel Administración")
                    }
                }
                TipoUsuario.Cliente -> {
                    InfoRow(label = "Tipo de Usuario:", value = "Cliente")
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}