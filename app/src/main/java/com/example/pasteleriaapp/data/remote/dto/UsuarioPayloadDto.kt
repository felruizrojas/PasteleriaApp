package com.example.pasteleriaapp.data.remote.dto

import com.example.pasteleriaapp.domain.model.TipoUsuario

/**
 * Payload que coincide con el contrato del backend para crear/actualizar usuarios.
 */
data class UsuarioPayloadDto(
    val idUsuario: Int? = null,
    val run: String,
    val nombre: String,
    val apellidos: String,
    val correo: String,
    val fechaNacimiento: String,
    val tipoUsuario: TipoUsuario,
    val region: String,
    val comuna: String,
    val direccion: String,
    val contrasena: String?,
    val tieneDescuentoEdad: Boolean,
    val tieneDescuentoCodigo: Boolean,
    val esEstudianteDuoc: Boolean,
    val fotoUrl: String?,
    val estaBloqueado: Boolean
)
