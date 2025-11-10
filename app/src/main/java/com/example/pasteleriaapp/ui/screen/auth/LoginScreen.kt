package com.example.pasteleriaapp.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // ... (LaunchedEffects sin cambios) ...
    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            Toast.makeText(context, "¡Bienvenido/a ${state.usuarioActual?.nombre}!", Toast.LENGTH_SHORT).show()
            viewModel.resetNavegacion()
            onLoginSuccess()
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetNavegacion() // Limpia el error
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Inicio de Sesión") })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Ingresa a tu cuenta", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(24.dp))

                // --- CAMPO DE CORREO ACTUALIZADO A VoiceTextField ---
                VoiceTextField(
                    value = state.loginCorreo,
                    onValueChange = viewModel::onLoginCorreoChange,
                    label = "Correo Electrónico",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                // --- CAMPO DE CONTRASEÑA (Sin cambios) ---
                PasswordTextField(
                    value = state.loginContrasena,
                    onValueChange = viewModel::onLoginContrasenaChange,
                    label = "Contraseña",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    Text("Ingresar")
                }
                TextButton(
                    onClick = onNavigateToRegister,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("¿No tienes cuenta? Regístrate")
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}