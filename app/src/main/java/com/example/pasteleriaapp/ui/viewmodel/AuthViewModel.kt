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

    fun onLoginCorreoChange(valor: String) { _uiState.update { it.copy(loginCorreo = valor) } }
    fun onLoginContrasenaChange(valor: String) { _uiState.update { it.copy(loginContrasena = valor) } }
    fun onRegRunChange(valor: String) { _uiState.update { it.copy(regRun = valor, regRunError = null) } }
    fun onRegNombreChange(valor: String) { _uiState.update { it.copy(regNombre = valor, regNombreError = null) } }
    fun onRegApellidosChange(valor: String) { _uiState.update { it.copy(regApellidos = valor, regApellidosError = null) } }
    fun onRegCorreoChange(valor: String) { _uiState.update { it.copy(regCorreo = valor) } }
    fun onRegCorreoErrorChange(mensaje: String?) { _uiState.update { it.copy(regCorreoError = mensaje) } }
    fun onRegFechaNacimientoChange(valor: String) { _uiState.update { it.copy(regFechaNacimiento = valor, regFechaNacimientoError = null) } }
    fun onRegRegionChange(valor: String) { _uiState.update { it.copy(regRegion = valor, regRegionError = null) } }
    fun onRegComunaChange(valor: String) { _uiState.update { it.copy(regComuna = valor, regComunaError = null) } }
    fun onRegDireccionChange(valor: String) { _uiState.update { it.copy(regDireccion = valor, regDireccionError = null) } }
    fun onRegContrasenaChange(valor: String) { _uiState.update { it.copy(regContrasena = valor, regContrasenaError = null) } }
    fun onRegRepetirContrasenaChange(valor: String) { _uiState.update { it.copy(regRepetirContrasena = valor, regRepetirContrasenaError = null) } }
    fun onRegCodigoPromoChange(valor: String) { _uiState.update { it.copy(regCodigoPromo = valor, regCodigoPromoError = null) } }
    fun onProfNombreChange(valor: String) { _uiState.update { it.copy(profNombre = valor) } }
    fun onProfApellidosChange(valor: String) { _uiState.update { it.copy(profApellidos = valor) } }
    fun onProfRegionChange(valor: String) { _uiState.update { it.copy(profRegion = valor) } }
    fun onProfComunaChange(valor: String) { _uiState.update { it.copy(profComuna = valor) } }
    fun onProfDireccionChange(valor: String) { _uiState.update { it.copy(profDireccion = valor) } }

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

    fun persistSession(context: Context) {
        val correo = _uiState.value.usuarioActual?.correo ?: return
        val prefs = context.getSharedPreferences("pasteleria_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("logged_user_correo", correo).apply()
    }

    fun restoreSession(context: Context) {
        viewModelScope.launch {
            try {
                val prefs = context.getSharedPreferences("pasteleria_prefs", Context.MODE_PRIVATE)
                val correo = prefs.getString("logged_user_correo", null)
                if (!correo.isNullOrBlank()) {
                    val usuario = repository.obtenerUsuarioPorCorreo(correo)
                    if (usuario != null) {
                        if (usuario.estaBloqueado) {
                            val prefs = context.getSharedPreferences("pasteleria_prefs", Context.MODE_PRIVATE)
                            prefs.edit().remove("logged_user_correo").apply()
                            _uiState.update { it.copy(usuarioActual = null, fotoUri = null) }
                        } else {
                            _uiState.update { it.copy(usuarioActual = usuario, fotoUri = usuario.fotoUrl?.toUri()) }
                        }
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun registrarUsuario() {
        val state = _uiState.value
        var hasError = false
        val run = state.regRun.trim()
        val nombre = state.regNombre.trim()
        val apellidos = state.regApellidos.trim()
        val correoRaw = state.regCorreo.trim()
        val fecha = state.regFechaNacimiento.trim()
        val region = state.regRegion.trim()
        val comuna = state.regComuna.trim()
        val direccion = state.regDireccion.trim()
        val contrasena = state.regContrasena
        val repetir = state.regRepetirContrasena
        val codigo = state.regCodigoPromo.trim()

        if (run.isBlank()) { _uiState.update { it.copy(regRunError = "RUN es obligatorio") }; hasError = true }
        if (nombre.isBlank()) { _uiState.update { it.copy(regNombreError = "Nombre es obligatorio") }; hasError = true }
        if (apellidos.isBlank()) { _uiState.update { it.copy(regApellidosError = "Apellidos son obligatorios") }; hasError = true }
        if (correoRaw.isBlank()) { _uiState.update { it.copy(regCorreoError = "Correo es obligatorio") }; hasError = true }
        if (fecha.isBlank()) { _uiState.update { it.copy(regFechaNacimientoError = "Fecha de nacimiento es obligatoria") }; hasError = true }
        if (region.isBlank()) { _uiState.update { it.copy(regRegionError = "Región es obligatoria") }; hasError = true }
        if (comuna.isBlank()) { _uiState.update { it.copy(regComunaError = "Comuna es obligatoria") }; hasError = true }
        if (direccion.isBlank()) { _uiState.update { it.copy(regDireccionError = "Dirección es obligatoria") }; hasError = true }
        if (contrasena.isBlank()) { _uiState.update { it.copy(regContrasenaError = "Contraseña es obligatoria") }; hasError = true }
        if (repetir.isBlank()) { _uiState.update { it.copy(regRepetirContrasenaError = "Repite la contraseña") }; hasError = true }
        if (hasError) return

        if (!validarRun(run)) {
            _uiState.update { it.copy(regRunError = "RUN inválido") }
            return
        }

        val tieneDigitos = Regex(".*\\d.*")
        if (tieneDigitos.containsMatchIn(nombre)) { _uiState.update { it.copy(regNombreError = "No se permiten números") }; return }
        if (tieneDigitos.containsMatchIn(apellidos)) { _uiState.update { it.copy(regApellidosError = "No se permiten números") }; return }
        if (tieneDigitos.containsMatchIn(region)) { _uiState.update { it.copy(regRegionError = "No se permiten números") }; return }
        if (tieneDigitos.containsMatchIn(comuna)) { _uiState.update { it.copy(regComunaError = "No se permiten números") }; return }

        if (!correoRaw.contains("@")) {
            _uiState.update { it.copy(regCorreoError = "Correo inválido") }
            return
        }
        val correo = correoRaw.lowercase()

        val regexContrasena = Regex("^[A-Za-z0-9]+$")
        if (!regexContrasena.matches(contrasena)) {
            _uiState.update { it.copy(regContrasenaError = "Contraseña solo letras y números") }
            return
        }

        if (contrasena != repetir) {
            _uiState.update { it.copy(regRepetirContrasenaError = "Las contraseñas no coinciden") }
            return
        }

        val edad = calcularEdad(fecha)
        val flagDescuentoEdad = edad >= 50
        val flagDescuentoCodigo = codigo.equals("PROMO10", ignoreCase = true) || codigo.equals("FELICES50", ignoreCase = true)
        val flagEsEstudianteDuoc = correo.endsWith("@duoc.cl") || correo.endsWith("@profesor.duoc.cl")

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val nuevoUsuario = Usuario(
                    run = run,
                    nombre = nombre,
                    apellidos = apellidos,
                    correo = correo,
                    fechaNacimiento = fecha,
                    region = region,
                    comuna = comuna,
                    direccion = direccion,
                    contrasena = contrasena,
                    tipoUsuario = TipoUsuario.Cliente,
                    tieneDescuentoEdad = flagDescuentoEdad,
                    tieneDescuentoCodigo = flagDescuentoCodigo,
                    esEstudianteDuoc = flagEsEstudianteDuoc,
                    fotoUrl = null
                )
                repository.registrarUsuario(nuevoUsuario)
                _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
            } catch (e: Exception) {
                val msg = e.message ?: "Error al registrar"
                when {
                    msg.contains("RUN", ignoreCase = true) -> _uiState.update { it.copy(isLoading = false, regRunError = msg) }
                    msg.contains("correo", ignoreCase = true) -> _uiState.update { it.copy(isLoading = false, regCorreoError = msg) }
                    else -> _uiState.update { it.copy(isLoading = false, error = msg) }
                }
            }
        }
    }

    private fun validarRun(rawRun: String): Boolean {
        try {
            val run = rawRun.replace(".", "").trim().lowercase()
            val parts = run.split("-")
            if (parts.size != 2) return false
            val body = parts[0]
            var dv = parts[1]
            if (body.isEmpty() || dv.isEmpty()) return false

            var suma = 0
            var multip = 2
            for (i in body.reversed()) {
                suma += Character.getNumericValue(i) * multip
                multip = if (multip == 7) 2 else multip + 1
            }
            val resto = 11 - (suma % 11)
            val dvCalc = when (resto) {
                11 -> "0"
                10 -> "k"
                else -> resto.toString()
            }
            if (dv == "K") dv = "k"
            return dv == dvCalc
        } catch (e: Exception) {
            return false
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

    fun guardarFotoPerfil(bitmap: Bitmap, context: Context) {
        val usuario = _uiState.value.usuarioActual ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val fotoUrl = withContext(Dispatchers.IO) {
                    saveBitmapToInternalStorage(bitmap, context, "user_${usuario.idUsuario}.jpg")
                }

                val usuarioActualizado = usuario.copy(fotoUrl = fotoUrl)
                repository.actualizarUsuario(usuarioActualizado)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        usuarioActual = usuarioActualizado,
                        fotoUri = fotoUrl?.toUri()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al guardar la foto: ${e.message}") }
            }
        }
    }

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
        return file.toUri().toString()
    }

    fun cargarDatosPerfil() {
        _uiState.value.usuarioActual?.let { usuario ->
            _uiState.update {
                it.copy(
                    profNombre = usuario.nombre,
                    profApellidos = usuario.apellidos,
                    profRegion = usuario.region,
                    profComuna = usuario.comuna,
                    profDireccion = usuario.direccion,
                    fotoUri = usuario.fotoUrl?.toUri()
                )
            }
        }
    }

    fun guardarCambiosPerfil() {
        val state = _uiState.value
        val usuario = state.usuarioActual ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
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
    fun logout(context: Context? = null) {
        context?.let {
            val prefs = it.getSharedPreferences("pasteleria_prefs", Context.MODE_PRIVATE)
            prefs.edit().remove("logged_user_correo").apply()
        }
        _uiState.value = AuthUiState(logoutSuccess = true)
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