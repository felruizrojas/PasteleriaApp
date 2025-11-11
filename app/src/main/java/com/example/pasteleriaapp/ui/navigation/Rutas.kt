package com.example.pasteleriaapp.ui.navigation

object Rutas {
    const val ARG_ID_CATEGORIA = "idCategoria"
    const val ARG_ID_PRODUCTO = "idProducto"
    const val HOME = "home"
    const val CATEGORIAS = "categorias"
    const val NOSOTROS = "nosotros"
    const val LOGIN = "login"
    const val CARRITO = "carrito"
    const val REGISTRO = "registro"
    const val AUTH_FLOW = "auth_flow"
    const val PERFIL = "perfil"
    const val EDITAR_PERFIL = "editar_perfil"
    const val CHECKOUT = "checkout"
    const val MIS_PEDIDOS = "pedidos"
    const val ADMIN_USUARIOS = "admin_usuarios"
    const val ADMIN_CATALOGO = "admin_catalogo"
    const val ADMIN_PEDIDOS = "admin_pedidos"
    const val ADMIN_PANEL = "admin_panel"
    const val BLOG = "blog"
    const val ARG_POST_ID = "postId"
    const val BLOG_DETALLE_RUTA = "$BLOG/{$ARG_POST_ID}"
    private const val PRODUCTOS = "productos"
    const val PRODUCTOS_RUTA = "$PRODUCTOS/{$ARG_ID_CATEGORIA}"
    const val DETALLE_PRODUCTO_RUTA = "$PRODUCTOS/detalle/{$ARG_ID_PRODUCTO}"
    const val FORMULARIO_PRODUCTO =
        "$PRODUCTOS/formulario?$ARG_ID_PRODUCTO={$ARG_ID_PRODUCTO}&$ARG_ID_CATEGORIA={$ARG_ID_CATEGORIA}"

    const val PEDIDO_DETALLE_RUTA = "pedidos/{idPedido}"

    fun obtenerRutaProductos(idCategoria: Int): String {
        return "$PRODUCTOS/$idCategoria"
    }

    fun obtenerRutaDetalleProducto(idProducto: Int): String {
        return "$PRODUCTOS/detalle/$idProducto"
    }

    fun obtenerRutaEditarProducto(idProducto: Int): String {
        return "$PRODUCTOS/formulario?$ARG_ID_PRODUCTO=$idProducto&$ARG_ID_CATEGORIA=0"
    }

    fun obtenerRutaNuevoProducto(idCategoria: Int): String {
        return "$PRODUCTOS/formulario?$ARG_ID_PRODUCTO=0&$ARG_ID_CATEGORIA=$idCategoria"
    }

    fun obtenerRutaDetallePedido(idPedido: Int): String {
        return "pedidos/$idPedido"
    }

    fun obtenerRutaBlogDetalle(postId: String): String {
        return "$BLOG/$postId"
    }
}