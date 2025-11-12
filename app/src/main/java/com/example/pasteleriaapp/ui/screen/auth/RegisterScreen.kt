package com.example.pasteleriaapp.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import android.app.DatePickerDialog
import android.content.Context
import java.util.Calendar
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
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
        onBackClick = onBackClick,
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
                VoiceTextField(
                    value = state.regRun,
                    onValueChange = { raw ->
                        val sanitized = sanitizeRunInput(raw)
                        viewModel.onRegRunChange(sanitized)
                    },
                    label = "RUN (ej: 12345678-9)",
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.regRunError != null) {
                    Text(text = state.regRunError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp))
                }
                Spacer(Modifier.height(10.dp))

                VoiceTextField(
                    value = state.regNombre,
                    onValueChange = { raw ->
                        viewModel.onRegNombreChange(sanitizeAlphabeticInput(raw))
                    },
                    label = "Nombre",
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.regNombreError != null) {
                    Text(text = state.regNombreError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp))
                }
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regApellidos,
                    onValueChange = { raw ->
                        viewModel.onRegApellidosChange(sanitizeAlphabeticInput(raw))
                    },
                    label = "Apellidos",
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.regApellidosError != null) {
                    Text(text = state.regApellidosError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp))
                }
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regCorreo,
                    onValueChange = { raw ->
                        viewModel.onRegCorreoChange(raw)
                        if (!raw.contains('@')) {
                            viewModel.onRegCorreoErrorChange("El correo debe contener '@'")
                        } else {
                            viewModel.onRegCorreoErrorChange(null)
                        }
                    },
                    label = "Correo Electrónico",
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.regCorreoError != null) {
                    Text(text = state.regCorreoError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp))
                }
                Spacer(Modifier.height(8.dp))

                val interactionSource = remember { MutableInteractionSource() }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = state.regFechaNacimiento,
                        onValueChange = {},
                        label = { Text("Fecha Nacimiento (DD-MM-AAAA)") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        isError = state.regFechaNacimientoError != null,
                        singleLine = true
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                showDatePickerDialog(
                                    context = context,
                                    currentValue = state.regFechaNacimiento,
                                    onDateSelected = viewModel::onRegFechaNacimientoChange
                                )
                            }
                    )
                }
                if (state.regFechaNacimientoError != null) {
                    Text(
                        text = state.regFechaNacimientoError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regRegion,
                    onValueChange = { raw ->
                        viewModel.onRegRegionChange(sanitizeAlphabeticInput(raw))
                    },
                    label = "Región",
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.regRegionError != null) {
                    Text(text = state.regRegionError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp))
                }
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.regComuna,
                    onValueChange = { raw ->
                        viewModel.onRegComunaChange(sanitizeAlphabeticInput(raw))
                    },
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

private fun sanitizeRunInput(input: String): String {
    val numbers = StringBuilder()
    var hyphenRequested = false
    var verifier: String? = null

    input.forEach { char ->
        when {
            char.isDigit() && !hyphenRequested -> {
                if (numbers.length < 8) {
                    numbers.append(char)
                }
            }

            (char == '-' || char == '–') && numbers.isNotEmpty() && !hyphenRequested -> {
                hyphenRequested = true
            }

            hyphenRequested && verifier == null -> {
                when {
                    char.isDigit() -> verifier = char.toString()
                    char.equals('k', ignoreCase = true) -> verifier = "K"
                }
            }
        }
    }

    return buildString {
        append(numbers)
        if (hyphenRequested && numbers.isNotEmpty()) {
            append('-')
            verifier?.let { append(it) }
        }
    }
}

private fun sanitizeAlphabeticInput(input: String): String {
    val sanitized = StringBuilder()
    var lastWasSpace = false

    input.forEach { char ->
        when {
            char.isLetter() -> {
                sanitized.append(char)
                lastWasSpace = false
            }
            char.isWhitespace() -> {
                if (sanitized.isNotEmpty() && !lastWasSpace) {
                    sanitized.append(' ')
                    lastWasSpace = true
                }
            }
        }
    }

    return sanitized.toString().trimEnd()
}
private fun showDatePickerDialog(
    context: Context,
    currentValue: String,
    onDateSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    if (currentValue.isNotBlank()) {
        val parts = currentValue.split("-")
        if (parts.size == 3) {
            val day = parts[0].toIntOrNull()
            val month = parts[1].toIntOrNull()
            val year = parts[2].toIntOrNull()
            if (day != null && month != null && year != null) {
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month - 1)
                calendar.set(Calendar.DAY_OF_MONTH, day)
            }
        }
    }

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formatted = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year)
            onDateSelected(formatted)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}