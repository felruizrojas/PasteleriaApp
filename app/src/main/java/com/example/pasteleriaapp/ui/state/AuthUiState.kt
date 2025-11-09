package com.example.pasteleriaapp.ui.state

import com.example.pasteleriaapp.domain.model.TipoUsuario
import com.example.pasteleriaapp.domain.model.Usuario

/**
 * Estado que unifica la lógica de Login y Registro.
 */
data class AuthUiState(
    // Estado general
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false,
    val registerSuccess: Boolean = false,
    val usuarioActual: Usuario? = null,
    val logoutSuccess: Boolean = false,    // <-- AÑADIDO
    val updateSuccess: Boolean = false,    // <-- AÑADIDO

    // --- Campos de Login ---
    val loginCorreo: String = "",
    val loginContrasena: String = "",

    // --- Campos de Registro ---
    val regRun: String = "",
    val regNombre: String = "",
    val regApellidos: String = "",
    val regCorreo: String = "",
    val regFechaNacimiento: String = "",
    val regRegion: String = "",
    val regComuna: String = "",
    val regDireccion: String = "",
    val regContrasena: String = "",
    val regRepetirContrasena: String = "",

    val profNombre: String = "",
    val profApellidos: String = "",
    val profRegion: String = "",
    val profComuna: String = "",
    val profDireccion: String = ""

)