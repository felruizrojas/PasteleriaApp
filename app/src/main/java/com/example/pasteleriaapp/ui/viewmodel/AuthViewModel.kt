package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.model.TipoUsuario
import com.example.pasteleriaapp.domain.model.Usuario
import com.example.pasteleriaapp.domain.repository.UsuarioRepository
import com.example.pasteleriaapp.ui.state.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // --- Eventos de Login ---
    fun onLoginCorreoChange(valor: String) { _uiState.update { it.copy(loginCorreo = valor) } }
    fun onLoginContrasenaChange(valor: String) { _uiState.update { it.copy(loginContrasena = valor) } }

    // --- Eventos de Registro ---
    fun onRegRunChange(valor: String) { _uiState.update { it.copy(regRun = valor) } }
    fun onRegNombreChange(valor: String) { _uiState.update { it.copy(regNombre = valor) } }
    fun onRegApellidosChange(valor: String) { _uiState.update { it.copy(regApellidos = valor) } }
    fun onRegCorreoChange(valor: String) { _uiState.update { it.copy(regCorreo = valor) } }
    fun onRegFechaNacimientoChange(valor: String) { _uiState.update { it.copy(regFechaNacimiento = valor) } }
    fun onRegRegionChange(valor: String) { _uiState.update { it.copy(regRegion = valor) } }
    fun onRegComunaChange(valor: String) { _uiState.update { it.copy(regComuna = valor) } }
    fun onRegDireccionChange(valor: String) { _uiState.update { it.copy(regDireccion = valor) } }
    fun onRegContrasenaChange(valor: String) { _uiState.update { it.copy(regContrasena = valor) } }
    fun onRegRepetirContrasenaChange(valor: String) { _uiState.update { it.copy(regRepetirContrasena = valor) } }

    // --- NUEVOS Eventos de Edición de Perfil ---
    fun onProfNombreChange(valor: String) { _uiState.update { it.copy(profNombre = valor) } }
    fun onProfApellidosChange(valor: String) { _uiState.update { it.copy(profApellidos = valor) } }
    fun onProfRegionChange(valor: String) { _uiState.update { it.copy(profRegion = valor) } }
    fun onProfComunaChange(valor: String) { _uiState.update { it.copy(profComuna = valor) } }
    fun onProfDireccionChange(valor: String) { _uiState.update { it.copy(profDireccion = valor) } }

    // --- Lógica de Negocio ---

    fun login() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val usuario = repository.login(state.loginCorreo.trim(), state.loginContrasena)
                if (usuario != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginSuccess = true,
                            usuarioActual = usuario
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Correo o contraseña incorrectos.")
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun registrarUsuario() {
        val state = _uiState.value

        // --- Validaciones ---
        if (state.regRun.isBlank() || state.regNombre.isBlank() || state.regApellidos.isBlank() ||
            state.regCorreo.isBlank() || state.regFechaNacimiento.isBlank() || state.regRegion.isBlank() ||
            state.regComuna.isBlank() || state.regDireccion.isBlank() || state.regContrasena.isBlank()) {
            _uiState.update { it.copy(error = "Todos los campos son obligatorios.") }
            return
        }
        if (state.regContrasena != state.regRepetirContrasena) {
            _uiState.update { it.copy(error = "Las contraseñas no coinciden.") }
            return
        }
        // (Aquí podrías añadir más validaciones: formato de RUN, formato de correo, etc.)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val nuevoUsuario = Usuario(
                    run = state.regRun.trim(),
                    nombre = state.regNombre.trim(),
                    apellidos = state.regApellidos.trim(),
                    correo = state.regCorreo.trim().lowercase(),
                    fechaNacimiento = state.regFechaNacimiento.trim(),
                    region = state.regRegion.trim(),
                    comuna = state.regComuna.trim(),
                    direccion = state.regDireccion.trim(),
                    contrasena = state.regContrasena,
                    tipoUsuario = TipoUsuario.Cliente // Por defecto, todos se registran como Clientes
                )
                repository.registrarUsuario(nuevoUsuario)
                _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }



    // --- FUNCIÓN NUEVA: Cargar datos en el formulario de edición ---
    fun cargarDatosPerfil() {
        _uiState.value.usuarioActual?.let { usuario ->
            _uiState.update {
                it.copy(
                    profNombre = usuario.nombre,
                    profApellidos = usuario.apellidos,
                    profRegion = usuario.region,
                    profComuna = usuario.comuna,
                    profDireccion = usuario.direccion
                )
            }
        }
    }

    // --- FUNCIÓN NUEVA: Guardar cambios del perfil ---
    fun guardarCambiosPerfil() {
        val state = _uiState.value
        val usuario = state.usuarioActual ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Creamos el usuario actualizado
                val usuarioActualizado = usuario.copy(
                    nombre = state.profNombre.trim(),
                    apellidos = state.profApellidos.trim(),
                    region = state.profRegion.trim(),
                    comuna = state.profComuna.trim(),
                    direccion = state.profDireccion.trim()
                )
                repository.actualizarUsuario(usuarioActualizado)
                // Actualizamos el estado global
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        usuarioActual = usuarioActualizado, // <-- Clave
                        updateSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // --- FUNCIÓN NUEVA: Cerrar Sesión ---
    fun logout() {
        _uiState.value = AuthUiState(logoutSuccess = true) // Resetea todo
    }

    // --- MODIFICADO: resetNavegacion ---
    fun resetNavegacion() {
        _uiState.update {
            it.copy(
                loginSuccess = false,
                registerSuccess = false,
                error = null,
                logoutSuccess = false,
                updateSuccess = false
            )
        }
    }
}







// Factory para el AuthViewModel
class AuthViewModelFactory(
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}