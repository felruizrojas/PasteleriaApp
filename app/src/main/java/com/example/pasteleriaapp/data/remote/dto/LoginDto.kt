package com.example.pasteleriaapp.data.remote.dto

data class LoginRequestDto(
    val correo: String,
    val contrasena: String
)

data class LoginResponseDto(
    val usuario: UsuarioDto,
    val mensaje: String
)
