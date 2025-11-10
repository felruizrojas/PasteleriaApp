package com.example.pasteleriaapp.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import android.app.DatePickerDialog
import java.util.Calendar
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.ui.components.AppScaffold
import com.example.pasteleriaapp.ui.components.AppTopBarActions
import com.example.pasteleriaapp.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit,
    badgeCount: Int,
    isLoggedIn: Boolean,
    topBarActions: AppTopBarActions,
    onLogout: (() -> Unit)?
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

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
            viewModel.resetNavegacion()
        }
    }

    AppScaffold(
        badgeCount = badgeCount,
        isLoggedIn = isLoggedIn,
        topBarActions = topBarActions,
        pageTitle = "Registro",
        onLogout = onLogout
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    Spacer(Modifier.width(8.dp))
                    Text("Volver")
                }
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regRun,
                    onValueChange = viewModel::onRegRunChange,
                    label = "RUN (ej: 12345678-9)",
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.regRunError != null) {
                    Text(text = state.regRunError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp))
                }
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regNombre,
                    onValueChange = viewModel::onRegNombreChange,
                    label = "Nombre",
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.regNombreError != null) {
                    Text(text = state.regNombreError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp))
                }
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regApellidos,
                    onValueChange = viewModel::onRegApellidosChange,
                    label = "Apellidos",
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.regApellidosError != null) {
                    Text(text = state.regApellidosError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp))
                }
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regCorreo,
                    onValueChange = viewModel::onRegCorreoChange,
                    label = "Correo Electrónico",
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.regCorreoError != null) {
                    Text(text = state.regCorreoError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp))
                }
                Spacer(Modifier.height(8.dp))

                var showDatePicker by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = state.regFechaNacimiento,
                    onValueChange = {},
                    label = { Text("Fecha Nacimiento (DD-MM-AAAA)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    readOnly = true,
                    isError = state.regFechaNacimientoError != null,
                    singleLine = true
                )
                if (state.regFechaNacimientoError != null) {
                    Text(
                        text = state.regFechaNacimientoError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))

                if (showDatePicker) {
                    val hoy = Calendar.getInstance()
                    DatePickerDialog(
                        LocalContext.current,
                        { _, ano, mes, dia ->
                            // mes viene 0-based
                            val formatted = String.format("%02d-%02d-%04d", dia, mes + 1, ano)
                            viewModel.onRegFechaNacimientoChange(formatted)
                            showDatePicker = false
                        },
                        hoy.get(Calendar.YEAR),
                        hoy.get(Calendar.MONTH),
                        hoy.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }

                VoiceTextField(
                    value = state.regRegion,
                    onValueChange = viewModel::onRegRegionChange,
                    label = "Región",
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.regRegionError != null) {
                    Text(text = state.regRegionError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp))
                }
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regComuna,
                    onValueChange = viewModel::onRegComunaChange,
                    label = "Comuna",
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.regComunaError != null) {
                    Text(text = state.regComunaError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp))
                }
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regDireccion,
                    onValueChange = viewModel::onRegDireccionChange,
                    label = "Dirección",
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.regDireccionError != null) {
                    Text(text = state.regDireccionError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp))
                }
                Spacer(Modifier.height(16.dp))

                PasswordTextField(
                    value = state.regContrasena,
                    onValueChange = viewModel::onRegContrasenaChange,
                    label = "Contraseña",
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.regContrasenaError != null) {
                    Text(text = state.regContrasenaError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp))
                }
                Spacer(Modifier.height(8.dp))

                PasswordTextField(
                    value = state.regRepetirContrasena,
                    onValueChange = viewModel::onRegRepetirContrasenaChange,
                    label = "Repetir Contraseña",
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.regRepetirContrasenaError != null
                )
                if (state.regRepetirContrasenaError != null) {
                    Text(text = state.regRepetirContrasenaError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp))
                }
                Spacer(Modifier.height(16.dp))

                VoiceTextField(
                    value = state.regCodigoPromo,
                    onValueChange = viewModel::onRegCodigoPromoChange,
                    label = "Código Promocional (Opcional)",
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.regCodigoPromoError != null) {
                    Text(text = state.regCodigoPromoError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp))
                }
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