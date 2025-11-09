package com.example.pasteleriaapp.data.repository

import com.example.pasteleriaapp.data.local.dao.UsuarioDao
import com.example.pasteleriaapp.data.local.entity.toUsuario
import com.example.pasteleriaapp.data.local.entity.toUsuarioEntity
import com.example.pasteleriaapp.domain.model.Usuario
import com.example.pasteleriaapp.domain.repository.UsuarioRepository

class UsuarioRepositoryImpl(
    private val dao: UsuarioDao
) : UsuarioRepository {

    override suspend fun login(correo: String, contrasena: String): Usuario? {
        val usuarioEntity = dao.obtenerUsuarioPorCorreo(correo)

        // 1. Verifica si el usuario existe
        if (usuarioEntity == null) {
            return null
        }

        // 2. Verifica si la contraseña coincide
        if (usuarioEntity.contrasena == contrasena) {
            return usuarioEntity.toUsuario()
        }

        // 3. Si la contraseña es incorrecta
        return null
    }

    override suspend fun registrarUsuario(usuario: Usuario) {
        // 1. Verificar si el RUN ya existe
        val existeRun = dao.obtenerUsuarioPorRun(usuario.run)
        if (existeRun != null) {
            throw Exception("El RUN ingresado ya se encuentra registrado.")
        }

        // 2. Verificar si el Correo ya existe
        val existeCorreo = dao.obtenerUsuarioPorCorreo(usuario.correo)
        if (existeCorreo != null) {
            throw Exception("El correo ingresado ya se encuentra registrado.")
        }

        // 3. Si no existe, insertar
        dao.insertarUsuario(usuario.toUsuarioEntity())
    }
}