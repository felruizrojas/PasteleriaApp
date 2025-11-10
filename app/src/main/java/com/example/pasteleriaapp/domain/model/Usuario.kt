package com.example.pasteleriaapp.domain.model

data class Usuario(
    val idUsuario: Int = 0,
    val run: String,
    val nombre: String,
    val apellidos: String,
    val correo: String,
    val fechaNacimiento: String,
    val tipoUsuario: TipoUsuario,
    val region: String,
    val comuna: String,
    val direccion: String,
    val contrasena: String, // Usamos 'contrasena' para no colisionar con 'password'

    val tieneDescuentoEdad: Boolean = false,
    val tieneDescuentoCodigo: Boolean = false,
    val esEstudianteDuoc: Boolean = false,

    val fotoUrl: String? = null
)