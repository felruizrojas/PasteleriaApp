package com.example.pasteleriaapp.data.repository

import com.example.pasteleriaapp.core.security.PasswordHasher
import com.example.pasteleriaapp.data.local.dao.UsuarioDao
import com.example.pasteleriaapp.data.local.entity.UsuarioEntity
import com.example.pasteleriaapp.data.local.entity.toUsuario
import com.example.pasteleriaapp.data.remote.api.PasteleriaApiService
import com.example.pasteleriaapp.data.remote.dto.LoginRequestDto
import com.example.pasteleriaapp.data.remote.mapper.toEntity
import com.example.pasteleriaapp.data.remote.mapper.toPayloadDto
import com.example.pasteleriaapp.domain.model.TipoUsuario
import com.example.pasteleriaapp.domain.model.Usuario
import com.example.pasteleriaapp.domain.repository.UsuarioRepository
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException

class UsuarioRepositoryImpl(
    private val dao: UsuarioDao,
    private val apiService: PasteleriaApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UsuarioRepository {

    override suspend fun login(correo: String, contrasena: String): Usuario? = withContext(ioDispatcher) {
        try {
            val response = apiService.login(LoginRequestDto(correo, contrasena))
            val localHash = PasswordHasher.hash(contrasena)
            val entity = response.usuario.toEntity(localHash)
            dao.insertarUsuarios(listOf(entity))

            if (entity.estaBloqueado) {
                throw IllegalStateException("Tu cuenta está bloqueada. Contacta al administrador.")
            }
            entity.toUsuario()
        } catch (http: HttpException) {
            val error = extractErrorMessage(http)
            when {
                http.code() == 400 && error.isNullOrBlank() -> null
                http.code() == 400 && error?.contains("credenciales", ignoreCase = true) == true -> null
                http.code() == 400 && error?.contains("bloqueada", ignoreCase = true) == true ->
                    throw IllegalStateException(error)
                http.code() >= 500 -> fallbackOfflineLogin(correo, contrasena) ?: throw http
                else -> throw IllegalStateException(error ?: "Error al iniciar sesión (${http.code()})")
            }
        } catch (io: IOException) {
            fallbackOfflineLogin(correo, contrasena) ?: throw io
        }
    }

    override suspend fun registrarUsuario(usuario: Usuario) = withContext(ioDispatcher) {
        val existeRun = dao.obtenerUsuarioPorRun(usuario.run)
        if (existeRun != null) {
            throw Exception("El RUN ingresado ya se encuentra registrado.")
        }

        val existeCorreo = dao.obtenerUsuarioPorCorreo(usuario.correo)
        if (existeCorreo != null) {
            throw Exception("El correo ingresado ya se encuentra registrado.")
        }

        try {
            val payload = usuario.toPayloadDto(password = usuario.contrasena)
            val remote = apiService.registrar(payload)
            val hashed = PasswordHasher.hash(usuario.contrasena)
            dao.insertarUsuarios(listOf(remote.toEntity(hashed)))
        } catch (http: HttpException) {
            val error = extractErrorMessage(http)
            throw Exception(error ?: "Error al registrar usuario (${http.code()})")
        }
    }

    override suspend fun actualizarUsuario(usuario: Usuario) = withContext(ioDispatcher) {
        val existingPassword = dao.obtenerUsuarioPorId(usuario.idUsuario)?.contrasena
        val payload = usuario.toPayloadDto(password = null)
        val remote = apiService.actualizarUsuario(usuario.idUsuario, payload)
        dao.insertarUsuarios(listOf(remote.toEntity(existingPassword)))
    }

    override suspend fun obtenerUsuarioPorCorreo(correo: String): Usuario? = withContext(ioDispatcher) {
        dao.obtenerUsuarioPorCorreo(correo)?.toUsuario()
            ?: run {
                syncUsuariosRemotos()
                dao.obtenerUsuarioPorCorreo(correo)?.toUsuario()
            }
    }

    override suspend fun obtenerUsuarioPorId(idUsuario: Int): Usuario? = withContext(ioDispatcher) {
        runCatching {
            val remote = apiService.obtenerUsuario(idUsuario)
            val existingPassword = dao.obtenerUsuarioPorId(idUsuario)?.contrasena
            val entity = remote.toEntity(existingPassword)
            dao.insertarUsuarios(listOf(entity))
            entity.toUsuario()
        }.getOrElse {
            dao.obtenerUsuarioPorId(idUsuario)?.toUsuario()
        }
    }

    override fun observarUsuarios(): Flow<List<Usuario>> {
        return dao.observarUsuarios()
            .onStart { syncUsuariosRemotos() }
            .map { lista -> lista.map { it.toUsuario() } }
    }

    override suspend fun actualizarTipoUsuario(idUsuario: Int, tipoUsuario: TipoUsuario) = withContext(ioDispatcher) {
        val remote = apiService.actualizarTipoUsuario(idUsuario, tipoUsuario.name)
        val existingPassword = dao.obtenerUsuarioPorId(idUsuario)?.contrasena
        dao.insertarUsuarios(listOf(remote.toEntity(existingPassword)))
    }

    override suspend fun actualizarEstadoBloqueo(idUsuario: Int, estaBloqueado: Boolean) = withContext(ioDispatcher) {
        val remote = apiService.actualizarEstadoUsuario(idUsuario, estaBloqueado)
        val existingPassword = dao.obtenerUsuarioPorId(idUsuario)?.contrasena
        dao.insertarUsuarios(listOf(remote.toEntity(existingPassword)))
    }

    override suspend fun eliminarUsuario(idUsuario: Int) = withContext(ioDispatcher) {
        apiService.eliminarUsuario(idUsuario)
        dao.eliminarUsuario(idUsuario)
    }

    private suspend fun fallbackOfflineLogin(correo: String, contrasena: String): Usuario? {
        val usuarioEntity = dao.obtenerUsuarioPorCorreo(correo) ?: return null
        if (usuarioEntity.estaBloqueado) {
            throw IllegalStateException("Tu cuenta está bloqueada. Contacta al administrador.")
        }
        return if (PasswordHasher.verify(contrasena, usuarioEntity.contrasena)) {
            usuarioEntity.toUsuario()
        } else {
            null
        }
    }

    private suspend fun syncUsuariosRemotos() = withContext(ioDispatcher) {
        runCatching {
            val remote = apiService.obtenerUsuarios()
            if (remote.isEmpty()) return@runCatching
            val passwordSnapshot = dao.obtenerUsuariosSnapshot().associate { it.idUsuario to it.contrasena }
            val entities = remote.map { dto ->
                val storedPassword = passwordSnapshot[dto.idUsuario]
                dto.toEntity(storedPassword)
            }
            dao.insertarUsuarios(entities)
        }
    }

    private fun extractErrorMessage(httpException: HttpException): String? {
        val raw = try {
            httpException.response()?.errorBody()?.string()
        } catch (ignored: Exception) {
            null
        }
        return parseErrorMessage(raw)
    }

    private fun parseErrorMessage(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        return try {
            JSONObject(raw).optString("message").takeIf { it.isNotBlank() } ?: raw
        } catch (_: Exception) {
            raw
        }
    }
}