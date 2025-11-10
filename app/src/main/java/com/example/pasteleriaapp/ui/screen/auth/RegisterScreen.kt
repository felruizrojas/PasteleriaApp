package com.example.pasteleriaapp.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // ... (LaunchedEffects sin cambios) ...
    LaunchedEffect(state.registerSuccess) {
        if (state.registerSuccess) {
            Toast.makeText(context, "Registro exitoso. Inicia sesión.", Toast.LENGTH_LONG).show()
            viewModel.resetNavegacion()
            onRegisterSuccess()
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.resetNavegacion() // Limpia el error
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Usuario") },
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
                // --- CAMPOS DE TEXTO ACTUALIZADOS A VoiceTextField ---
                VoiceTextField(
                    value = state.regRun,
                    onValueChange = viewModel::onRegRunChange,
                    label = "RUN (ej: 12345678-9)",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regNombre,
                    onValueChange = viewModel::onRegNombreChange,
                    label = "Nombre",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regApellidos,
                    onValueChange = viewModel::onRegApellidosChange,
                    label = "Apellidos",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regCorreo,
                    onValueChange = viewModel::onRegCorreoChange,
                    label = "Correo Electrónico",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regFechaNacimiento,
                    onValueChange = viewModel::onRegFechaNacimientoChange,
                    label = "Fecha Nacimiento (DD-MM-AAAA)",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regRegion,
                    onValueChange = viewModel::onRegRegionChange,
                    label = "Región",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regComuna,
                    onValueChange = viewModel::onRegComunaChange,
                    label = "Comuna",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regDireccion,
                    onValueChange = viewModel::onRegDireccionChange,
                    label = "Dirección",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                // --- CAMPOS DE CONTRASEÑA (Sin cambios) ---
                PasswordTextField(
                    value = state.regContrasena,
                    onValueChange = viewModel::onRegContrasenaChange,
                    label = "Contraseña",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                PasswordTextField(
                    value = state.regRepetirContrasena,
                    onValueChange = viewModel::onRegRepetirContrasenaChange,
                    label = "Repetir Contraseña",
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.regContrasena != state.regRepetirContrasena
                )
                Spacer(Modifier.height(16.dp))

                // --- CAMPO DE CÓDIGO (Actualizado) ---
                VoiceTextField(
                    value = state.regCodigoPromo,
                    onValueChange = viewModel::onRegCodigoPromoChange,
                    label = "Código Promocional (Opcional)",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.registrarUsuario() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    Text("Crear Cuenta")
                }
            }
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}