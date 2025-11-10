package com.example.pasteleriaapp.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.model.TipoUsuario
import com.example.pasteleriaapp.domain.model.Usuario
import com.example.pasteleriaapp.domain.repository.UsuarioRepository
import com.example.pasteleriaapp.ui.state.AuthUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar

class AuthViewModel(
    val repository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ... (Todos los eventos on...Change sin cambios) ...
    fun onLoginCorreoChange(valor: String) { _uiState.update { it.copy(loginCorreo = valor) } }
    fun onLoginContrasenaChange(valor: String) { _uiState.update { it.copy(loginContrasena = valor) } }
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
    fun onRegCodigoPromoChange(valor: String) { _uiState.update { it.copy(regCodigoPromo = valor) } }
    fun onProfNombreChange(valor: String) { _uiState.update { it.copy(profNombre = valor) } }
    fun onProfApellidosChange(valor: String) { _uiState.update { it.copy(profApellidos = valor) } }
    fun onProfRegionChange(valor: String) { _uiState.update { it.copy(profRegion = valor) } }
    fun onProfComunaChange(valor: String) { _uiState.update { it.copy(profComuna = valor) } }
    fun onProfDireccionChange(valor: String) { _uiState.update { it.copy(profDireccion = valor) } }


    // --- FUNCIÓN LOGIN (ACTUALIZADA) ---
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
                            usuarioActual = usuario,
                            // Cargamos la foto de perfil al iniciar sesión
                            fotoUri = usuario.fotoUrl?.toUri()
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

    // ... (registrarUsuario y calcularEdad sin cambios) ...
    fun registrarUsuario() {
        val state = _uiState.value
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
        val edad = calcularEdad(state.regFechaNacimiento.trim())
        val flagDescuentoEdad = edad > 50
        val flagDescuentoCodigo = state.regCodigoPromo.trim().equals("FELICES50", ignoreCase = true)
        val correo = state.regCorreo.trim().lowercase()
        val flagEsEstudianteDuoc = correo.endsWith("@duoc.cl") || correo.endsWith("@profesor.duoc.cl")

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val nuevoUsuario = Usuario(
                    run = state.regRun.trim(),
                    nombre = state.regNombre.trim(),
                    apellidos = state.regApellidos.trim(),
                    correo = correo,
                    fechaNacimiento = state.regFechaNacimiento.trim(),
                    region = state.regRegion.trim(),
                    comuna = state.regComuna.trim(),
                    direccion = state.regDireccion.trim(),
                    contrasena = state.regContrasena,
                    tipoUsuario = TipoUsuario.Cliente,
                    tieneDescuentoEdad = flagDescuentoEdad,
                    tieneDescuentoCodigo = flagDescuentoCodigo,
                    esEstudianteDuoc = flagEsEstudianteDuoc,
                    fotoUrl = null // Foto nula al registrarse
                )
                repository.registrarUsuario(nuevoUsuario)
                _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    private fun calcularEdad(fechaNacimiento: String): Int {
        try {
            val partes = fechaNacimiento.split("-")
            if (partes.size != 3) return 0
            val dia = partes[0].toInt()
            val mes = partes[1].toInt()
            val ano = partes[2].toInt()
            val hoy = Calendar.getInstance()
            val nacimiento = Calendar.getInstance()
            nacimiento.set(ano, mes - 1, dia)
            var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
            if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
                edad--
            }
            return edad
        } catch (e: Exception) {
            return 0
        }
    }

    // --- FUNCIÓN NUEVA: GUARDAR FOTO ---
    /**
     * Guarda el bitmap de la cámara en el almacenamiento interno y actualiza la BD.
     */
    fun guardarFotoPerfil(bitmap: Bitmap, context: Context) {
        val usuario = _uiState.value.usuarioActual ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Guardamos en un hilo de IO (Entrada/Salida)
                val fotoUrl = withContext(Dispatchers.IO) {
                    saveBitmapToInternalStorage(bitmap, context, "user_${usuario.idUsuario}.jpg")
                }

                // Actualizamos el usuario en la BD con la nueva URL
                val usuarioActualizado = usuario.copy(fotoUrl = fotoUrl)
                repository.actualizarUsuario(usuarioActualizado)

                // Actualizamos el estado de la UI
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        usuarioActual = usuarioActualizado,
                        fotoUri = fotoUrl?.toUri() // Actualiza la URI en la UI
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al guardar la foto: ${e.message}") }
            }
        }
    }

    /**
     * Función auxiliar para guardar el bitmap y devolver la ruta (String)
     */
    @Throws(IOException::class)
    private fun saveBitmapToInternalStorage(bitmap: Bitmap, context: Context, filename: String): String {
        val fileDir = File(context.filesDir, "profile_images")
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        val file = File(fileDir, filename)

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos) // Comprime a JPEG
            fos.flush()
        } finally {
            fos?.close()
        }
        return file.toUri().toString() // Devuelve la URI como String
    }


    // --- OTRAS FUNCIONES (sin cambios) ---
    fun cargarDatosPerfil() {
        _uiState.value.usuarioActual?.let { usuario ->
            _uiState.update {
                it.copy(
                    profNombre = usuario.nombre,
                    profApellidos = usuario.apellidos,
                    profRegion = usuario.region,
                    profComuna = usuario.comuna,
                    profDireccion = usuario.direccion,
                    fotoUri = usuario.fotoUrl?.toUri() // Carga la foto existente
                )
            }
        }
    }

    fun guardarCambiosPerfil() {
        // ... (Esta función ahora solo guarda texto, la foto se guarda aparte)
        val state = _uiState.value
        val usuario = state.usuarioActual ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Solo actualiza los campos de texto
                val usuarioActualizado = usuario.copy(
                    nombre = state.profNombre.trim(),
                    apellidos = state.profApellidos.trim(),
                    region = state.profRegion.trim(),
                    comuna = state.profComuna.trim(),
                    direccion = state.profDireccion.trim()
                )
                repository.actualizarUsuario(usuarioActualizado)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        usuarioActual = usuarioActualizado,
                        updateSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun logout() {
        _uiState.value = AuthUiState(logoutSuccess = true) // Resetea todo
    }

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

// ... (AuthViewModelFactory sin cambios) ...
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