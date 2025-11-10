package com.example.pasteleriaapp.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pasteleriaapp.domain.model.TipoUsuario
import com.example.pasteleriaapp.ui.components.AppScaffold
import com.example.pasteleriaapp.ui.components.AppTopBarActions
import com.example.pasteleriaapp.ui.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onNavigateToEdit: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToMisPedidos: () -> Unit,
    badgeCount: Int,
    isLoggedIn: Boolean,
    topBarActions: AppTopBarActions,
    onLogout: (() -> Unit)?
) {
    val state by authViewModel.uiState.collectAsState()
    val usuario = state.usuarioActual

    AppScaffold(
        badgeCount = badgeCount,
        isLoggedIn = isLoggedIn,
        topBarActions = topBarActions,
        pageTitle = "Mi Perfil",
        onLogout = onLogout
    ) { paddingValues ->
        if (usuario == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No se encontró el usuario.")
            }
            return@AppScaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    Spacer(Modifier.width(8.dp))
                    Text("Volver")
                }
                TextButton(onClick = onNavigateToEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar Perfil")
                    Spacer(Modifier.width(8.dp))
                    Text("Editar")
                }
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (state.fotoUri != null) {
                    AsyncImage(
                        model = state.fotoUri,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Sin foto de perfil",
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "${usuario.nombre} ${usuario.apellidos}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Datos Personales", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
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