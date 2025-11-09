package com.example.pasteleriaapp.domain.repository

import com.example.pasteleriaapp.domain.model.Usuario

interface UsuarioRepository {
    /**
     * Intenta iniciar sesión con correo y contraseña.
     * @return El [Usuario] si es exitoso, o null si las credenciales son incorrectas.
     */
    suspend fun login(correo: String, contrasena: String): Usuario?

    /**
     * Registra un nuevo usuario en la base de datos.
     * @throws Exception si el RUN o el correo ya están registrados.
     */
    suspend fun registrarUsuario(usuario: Usuario)
}