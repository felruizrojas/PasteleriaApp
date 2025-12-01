package com.example.pasteleriaapp.data.remote.dto

import com.example.pasteleriaapp.domain.model.TipoUsuario

data class UsuarioDto(
    val idUsuario: Int,
    val run: String,
    val nombre: String,
    val apellidos: String,
    val correo: String,
    val fechaNacimiento: String,
    val tipoUsuario: TipoUsuario,
    val region: String,
    val comuna: String,
    val direccion: String,
    val tieneDescuentoEdad: Boolean,
    val tieneDescuentoCodigo: Boolean,
    val esEstudianteDuoc: Boolean,
    val fotoUrl: String?,
    val estaBloqueado: Boolean
)
