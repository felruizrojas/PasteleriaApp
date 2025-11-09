package com.example.pasteleriaapp.domain.repository

import com.example.pasteleriaapp.domain.model.Usuario

interface UsuarioRepository {
    suspend fun login(correo: String, contrasena: String): Usuario?
    suspend fun registrarUsuario(usuario: Usuario)

    suspend fun actualizarUsuario(usuario: Usuario)
}