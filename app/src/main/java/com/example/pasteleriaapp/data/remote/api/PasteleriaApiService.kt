package com.example.pasteleriaapp.data.remote.api

import com.example.pasteleriaapp.data.remote.dto.ActualizarEstadoPedidoDto
import com.example.pasteleriaapp.data.remote.dto.ActualizarCantidadCarritoDto
import com.example.pasteleriaapp.data.remote.dto.ActualizarMensajeCarritoDto
import com.example.pasteleriaapp.data.remote.dto.CarritoItemDto
import com.example.pasteleriaapp.data.remote.dto.CarritoItemPayloadDto
import com.example.pasteleriaapp.data.remote.dto.CategoriaDto
import com.example.pasteleriaapp.data.remote.dto.CrearPedidoPayloadDto
import com.example.pasteleriaapp.data.remote.dto.LoginRequestDto
import com.example.pasteleriaapp.data.remote.dto.LoginResponseDto
import com.example.pasteleriaapp.data.remote.dto.PedidoDto
import com.example.pasteleriaapp.data.remote.dto.ProductoDto
import com.example.pasteleriaapp.data.remote.dto.UsuarioDto
import com.example.pasteleriaapp.data.remote.dto.UsuarioPayloadDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PasteleriaApiService {

    // Categorías
    @GET("categorias")
    suspend fun obtenerCategoriasPublicas(): List<CategoriaDto>

    @GET("categorias/admin")
    suspend fun obtenerCategoriasAdmin(): List<CategoriaDto>

    @GET("categorias/{id}")
    suspend fun obtenerCategoria(@Path("id") id: Int): CategoriaDto

    @POST("categorias")
    suspend fun crearCategoria(@Body categoria: CategoriaDto): CategoriaDto

    @PUT("categorias/{id}")
    suspend fun actualizarCategoria(@Path("id") id: Int, @Body categoria: CategoriaDto): CategoriaDto

    @PATCH("categorias/{id}/bloqueo")
    suspend fun actualizarEstadoCategoria(
        @Path("id") id: Int,
        @Query("estaBloqueada") estaBloqueada: Boolean
    ): CategoriaDto

    @DELETE("categorias/{id}")
    suspend fun eliminarCategoria(@Path("id") id: Int)

    // Productos
    @GET("productos")
    suspend fun obtenerProductos(): List<ProductoDto>

    @GET("productos/{id}")
    suspend fun obtenerProductoPorId(@Path("id") id: Int): ProductoDto

    @GET("productos/categoria/{idCategoria}")
    suspend fun obtenerProductosPorCategoria(@Path("idCategoria") idCategoria: Int): List<ProductoDto>

    @GET("productos/categoria/{idCategoria}/admin")
    suspend fun obtenerProductosPorCategoriaAdmin(@Path("idCategoria") idCategoria: Int): List<ProductoDto>

    @POST("productos")
    suspend fun crearProducto(@Body producto: ProductoDto): ProductoDto

    @PUT("productos/{id}")
    suspend fun actualizarProducto(@Path("id") id: Int, @Body producto: ProductoDto): ProductoDto

    @PATCH("productos/{id}/bloqueo")
    suspend fun actualizarEstadoProducto(
        @Path("id") id: Int,
        @Query("estaBloqueado") estaBloqueado: Boolean
    ): ProductoDto

    @DELETE("productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Int)

    // Usuarios
    @GET("usuarios")
    suspend fun obtenerUsuarios(): List<UsuarioDto>

    @GET("usuarios/{id}")
    suspend fun obtenerUsuario(@Path("id") id: Int): UsuarioDto

    @PUT("usuarios/{id}")
    suspend fun actualizarUsuario(@Path("id") id: Int, @Body payload: UsuarioPayloadDto): UsuarioDto

    @PATCH("usuarios/{id}/tipo")
    suspend fun actualizarTipoUsuario(
        @Path("id") id: Int,
        @Query("tipo") tipo: String
    ): UsuarioDto

    @PATCH("usuarios/{id}/bloqueo")
    suspend fun actualizarEstadoUsuario(
        @Path("id") id: Int,
        @Query("estaBloqueado") estaBloqueado: Boolean
    ): UsuarioDto

    @DELETE("usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Int)

    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("auth/register")
    suspend fun registrar(@Body payload: UsuarioPayloadDto): UsuarioDto

    // Pedidos
    @POST("pedidos")
    suspend fun crearPedido(@Body payload: CrearPedidoPayloadDto): PedidoDto

    @GET("pedidos/usuario/{idUsuario}")
    suspend fun obtenerPedidosUsuario(@Path("idUsuario") idUsuario: Int): List<PedidoDto>

    @GET("pedidos")
    suspend fun obtenerPedidos(): List<PedidoDto>

    @PATCH("pedidos/{idPedido}/estado")
    suspend fun actualizarEstadoPedido(
        @Path("idPedido") idPedido: Int,
        @Body body: ActualizarEstadoPedidoDto
    ): PedidoDto

    // Carrito
    @GET("usuarios/{idUsuario}/carrito")
    suspend fun obtenerCarrito(@Path("idUsuario") idUsuario: Int): List<CarritoItemDto>

    @POST("usuarios/{idUsuario}/carrito")
    suspend fun agregarAlCarrito(
        @Path("idUsuario") idUsuario: Int,
        @Body payload: CarritoItemPayloadDto
    ): CarritoItemDto

    @PATCH("usuarios/{idUsuario}/carrito/{idCarrito}/cantidad")
    suspend fun actualizarCantidadCarrito(
        @Path("idUsuario") idUsuario: Int,
        @Path("idCarrito") idCarrito: Int,
        @Body payload: ActualizarCantidadCarritoDto
    ): CarritoItemDto

    @PATCH("usuarios/{idUsuario}/carrito/{idCarrito}/mensaje")
    suspend fun actualizarMensajeCarrito(
        @Path("idUsuario") idUsuario: Int,
        @Path("idCarrito") idCarrito: Int,
        @Body payload: ActualizarMensajeCarritoDto
    ): CarritoItemDto

    @DELETE("usuarios/{idUsuario}/carrito/{idCarrito}")
    suspend fun eliminarItemCarrito(
        @Path("idUsuario") idUsuario: Int,
        @Path("idCarrito") idCarrito: Int
    )

    @DELETE("usuarios/{idUsuario}/carrito")
    suspend fun limpiarCarrito(@Path("idUsuario") idUsuario: Int)
}
