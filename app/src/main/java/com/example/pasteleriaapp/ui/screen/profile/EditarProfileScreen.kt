package com.example.pasteleriaapp.ui.screen.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
// --- ¡IMPORT AÑADIDO! ---
// Importa la función que acabamos de hacer pública
import com.example.pasteleriaapp.ui.screen.profile.InfoRow
import com.example.pasteleriaapp.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProfileScreen(
    authViewModel: AuthViewModel,
    onEditSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Carga los datos en los campos del formulario la primera vez
    LaunchedEffect(Unit) {
        authViewModel.cargarDatosPerfil()
    }

    // Observa si la actualización fue exitosa
    LaunchedEffect(state.updateSuccess) {
        if (state.updateSuccess) {
            Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
            authViewModel.resetNavegacion()
            onEditSuccess()
        }
    }

    // Muestra errores
    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            authViewModel.resetNavegacion()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- ¡CORREGIDO! ---
                // Esta llamada ahora es válida gracias al import
                InfoRow(label = "RUN:", value = state.usuarioActual?.run ?: "")
                InfoRow(label = "Correo:", value = state.usuarioActual?.correo ?: "")
                Spacer(Modifier.height(16.dp))

                // Campos Editables
                OutlinedTextField(
                    value = state.profNombre,
                    onValueChange = authViewModel::onProfNombreChange,
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.profApellidos,
                    onValueChange = authViewModel::onProfApellidosChange,
                    label = { Text("Apellidos") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.profRegion,
                    onValueChange = authViewModel::onProfRegionChange,
                    label = { Text("Región") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.profComuna,
                    onValueChange = authViewModel::onProfComunaChange,
                    label = { Text("Comuna") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.profDireccion,
                    onValueChange = authViewModel::onProfDireccionChange,
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { authViewModel.guardarCambiosPerfil() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    Text("Guardar Cambios")
                }
            }
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}