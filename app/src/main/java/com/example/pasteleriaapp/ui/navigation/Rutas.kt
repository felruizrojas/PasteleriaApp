package com.example.pasteleriaapp.ui.navigation

object Rutas {
    // Definición de los nombres de los argumentos
    const val ARG_ID_CATEGORIA = "idCategoria"
    const val ARG_ID_PRODUCTO = "idProducto"

    // Rutas base
    const val HOME = "home"
    const val CATEGORIAS = "categorias" // <-- Esta será tu ruta de "Catálogo"
    const val NOSOTROS = "nosotros"     // <-- NUEVA
    const val LOGIN = "login"           // <-- NUEVA
    private const val PRODUCTOS = "productos"

    // --- Rutas Compuestas (con argumentos) ---

    // ... (El resto de tus rutas: PRODUCTOS_RUTA, DETALLE_PRODUCTO_RUTA, FORMULARIO_PRODUCTO) ...
    const val PRODUCTOS_RUTA = "$PRODUCTOS/{$ARG_ID_CATEGORIA}"
    const val DETALLE_PRODUCTO_RUTA = "$PRODUCTOS/detalle/{$ARG_ID_PRODUCTO}"
    const val FORMULARIO_PRODUCTO =
        "$PRODUCTOS/formulario?$ARG_ID_PRODUCTO={$ARG_ID_PRODUCTO}&$ARG_ID_CATEGORIA={$ARG_ID_CATEGORIA}"


    // --- Funciones auxiliares ... ---

    // ... (Todas tus funciones 'obtenerRuta...' existentes) ...
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
}