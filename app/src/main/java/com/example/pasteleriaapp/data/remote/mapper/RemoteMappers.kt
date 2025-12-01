package com.example.pasteleriaapp.data.remote.mapper

import com.example.pasteleriaapp.data.local.entity.CarritoItemEntity
import com.example.pasteleriaapp.data.local.entity.CategoriaEntity
import com.example.pasteleriaapp.data.local.entity.PedidoEntity
import com.example.pasteleriaapp.data.local.entity.PedidoProductoEntity
import com.example.pasteleriaapp.data.local.entity.ProductoEntity
import com.example.pasteleriaapp.data.local.entity.UsuarioEntity
import com.example.pasteleriaapp.data.remote.dto.CarritoItemDto
import com.example.pasteleriaapp.data.remote.dto.CategoriaDto
import com.example.pasteleriaapp.data.remote.dto.PedidoDto
import com.example.pasteleriaapp.data.remote.dto.PedidoProductoDto
import com.example.pasteleriaapp.data.remote.dto.PedidoProductoRequestDto
import com.example.pasteleriaapp.data.remote.dto.ProductoDto
import com.example.pasteleriaapp.data.remote.dto.UsuarioDto
import com.example.pasteleriaapp.data.remote.dto.UsuarioPayloadDto
import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.model.Categoria
import com.example.pasteleriaapp.domain.model.Pedido
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.domain.model.Usuario

fun CategoriaDto.toDomain() = Categoria(
    idCategoria = idCategoria ?: 0,
    nombreCategoria = nombreCategoria,
    imagenCategoria = imagenCategoria,
    estaBloqueada = estaBloqueada
)

fun CategoriaDto.toEntity() = CategoriaEntity(
    idCategoria = idCategoria ?: 0,
    nombreCategoria = nombreCategoria,
    imagenCategoria = imagenCategoria,
    estaBloqueada = estaBloqueada
)

fun Categoria.toRemoteDto() = CategoriaDto(
    idCategoria = idCategoria.takeIf { it != 0 },
    nombreCategoria = nombreCategoria,
    imagenCategoria = imagenCategoria,
    estaBloqueada = estaBloqueada
)

fun ProductoDto.toDomain() = Producto(
    idProducto = idProducto ?: 0,
    idCategoria = idCategoria,
    codigoProducto = codigoProducto,
    nombreProducto = nombreProducto,
    precioProducto = precioProducto,
    descripcionProducto = descripcionProducto,
    imagenProducto = imagenProducto,
    stockProducto = stockProducto,
    stockCriticoProducto = stockCriticoProducto,
    estaBloqueado = estaBloqueado
)

fun ProductoDto.toEntity() = ProductoEntity(
    idProducto = idProducto ?: 0,
    idCategoria = idCategoria,
    codigoProducto = codigoProducto,
    nombreProducto = nombreProducto,
    precioProducto = precioProducto,
    descripcionProducto = descripcionProducto,
    imagenProducto = imagenProducto,
    stockProducto = stockProducto,
    stockCriticoProducto = stockCriticoProducto,
    estaBloqueado = estaBloqueado
)

fun Producto.toRemoteDto() = ProductoDto(
    idProducto = idProducto.takeIf { it != 0 },
    idCategoria = idCategoria,
    codigoProducto = codigoProducto,
    nombreProducto = nombreProducto,
    precioProducto = precioProducto,
    descripcionProducto = descripcionProducto,
    imagenProducto = imagenProducto,
    stockProducto = stockProducto,
    stockCriticoProducto = stockCriticoProducto,
    estaBloqueado = estaBloqueado
)

fun UsuarioDto.toDomain() = Usuario(
    idUsuario = idUsuario,
    run = run,
    nombre = nombre,
    apellidos = apellidos,
    correo = correo,
    fechaNacimiento = fechaNacimiento,
    tipoUsuario = tipoUsuario,
    region = region,
    comuna = comuna,
    direccion = direccion,
    contrasena = "",
    tieneDescuentoEdad = tieneDescuentoEdad,
    tieneDescuentoCodigo = tieneDescuentoCodigo,
    esEstudianteDuoc = esEstudianteDuoc,
    fotoUrl = fotoUrl,
    estaBloqueado = estaBloqueado
)

fun UsuarioDto.toEntity(existingPassword: String?) = UsuarioEntity(
    idUsuario = idUsuario,
    run = run,
    nombre = nombre,
    apellidos = apellidos,
    correo = correo,
    fechaNacimiento = fechaNacimiento,
    tipoUsuario = tipoUsuario,
    region = region,
    comuna = comuna,
    direccion = direccion,
    contrasena = existingPassword ?: "",
    tieneDescuentoEdad = tieneDescuentoEdad,
    tieneDescuentoCodigo = tieneDescuentoCodigo,
    esEstudianteDuoc = esEstudianteDuoc,
    estaBloqueado = estaBloqueado,
    fotoUrl = fotoUrl
)

fun Usuario.toPayloadDto(password: String?) = UsuarioPayloadDto(
    idUsuario = idUsuario.takeIf { it != 0 },
    run = run,
    nombre = nombre,
    apellidos = apellidos,
    correo = correo,
    fechaNacimiento = fechaNacimiento,
    tipoUsuario = tipoUsuario,
    region = region,
    comuna = comuna,
    direccion = direccion,
    contrasena = password,
    tieneDescuentoEdad = tieneDescuentoEdad,
    tieneDescuentoCodigo = tieneDescuentoCodigo,
    esEstudianteDuoc = esEstudianteDuoc,
    fotoUrl = fotoUrl,
    estaBloqueado = estaBloqueado
)

fun PedidoDto.toPedidoEntity() = PedidoEntity(
    idPedido = idPedido,
    idUsuario = idUsuario,
    fechaPedido = fechaPedido,
    fechaEntregaPreferida = fechaEntregaPreferida,
    estado = estado,
    total = total
)

fun PedidoProductoDto.toEntity() = PedidoProductoEntity(
    idPedidoProducto = idPedidoProducto,
    idPedido = idPedido,
    idProducto = idProducto,
    nombreProducto = nombreProducto,
    precioProducto = precioProducto,
    imagenProducto = imagenProducto,
    cantidad = cantidad,
    mensajePersonalizado = mensajePersonalizado
)

fun Pedido.toPedidoEntity() = PedidoEntity(
    idPedido = idPedido,
    idUsuario = idUsuario,
    fechaPedido = fechaPedido,
    fechaEntregaPreferida = fechaEntregaPreferida,
    estado = estado,
    total = total
)

fun CarritoItem.toPedidoProductoRequestDto() = PedidoProductoRequestDto(
    idProducto = idProducto,
    nombreProducto = nombreProducto,
    precioProducto = precioProducto,
    imagenProducto = imagenProducto,
    cantidad = cantidad,
    mensajePersonalizado = mensajePersonalizado
)

fun CarritoItemDto.toEntity() = CarritoItemEntity(
    idCarrito = idCarrito,
    idUsuario = idUsuario,
    idProducto = idProducto,
    nombreProducto = nombreProducto,
    precioProducto = precioProducto,
    imagenProducto = imagenProducto,
    cantidad = cantidad,
    mensajePersonalizado = mensajePersonalizado
)

fun CarritoItemDto.toDomain() = CarritoItem(
    idCarrito = idCarrito,
    usuarioId = idUsuario,
    idProducto = idProducto,
    nombreProducto = nombreProducto,
    precioProducto = precioProducto,
    imagenProducto = imagenProducto,
    cantidad = cantidad,
    mensajePersonalizado = mensajePersonalizado
)
