package com.example.pasteleriaapp.ui.state

import com.example.pasteleriaapp.domain.model.Producto

/**
 * Define el estado de la pantalla del formulario de producto.
 * Se usan Strings para los campos de texto para compatibilidad con TextField.
 */
data class ProductoFormUiState(
    val idProducto: Int = 0,
    val idCategoria: Int = 0,

    // Campos del formulario
    val nombre: String = "",
    val precio: String = "",
    val descripcion: String = "",
    val stock: String = "",
    val codigo: String = "",

    // Estado de la UI
    val tituloPantalla: String = "Nuevo Producto",
    val estaCargando: Boolean = false,
    val error: String? = null,
    val guardadoExitoso: Boolean = false // Se pone a true para navegar hacia atrás
)

/**
 * Función de conveniencia para mapear un Producto del dominio
 * al estado del formulario (UiState).
 */
fun Producto.toFormUiState(): ProductoFormUiState {
    return ProductoFormUiState(
        idProducto = this.idProducto,
        idCategoria = this.idCategoria,
        nombre = this.nombreProducto,
        precio = this.precioProducto.toString(),
        descripcion = this.descripcionProducto,
        stock = this.stockProducto.toString(),
        codigo = this.codigoProducto,
        tituloPantalla = "Editar Producto"
    )
}