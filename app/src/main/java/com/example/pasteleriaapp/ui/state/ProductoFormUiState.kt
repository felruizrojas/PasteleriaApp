package com.example.pasteleriaapp.ui.state

import com.example.pasteleriaapp.domain.model.Categoria
import com.example.pasteleriaapp.domain.model.Producto

data class ProductoFormUiState(
    val idProducto: Int = 0,
    val idCategoria: Int = 0,

    val nombre: String = "",
    val precio: String = "",
    val descripcion: String = "",
    val stock: String = "",
    val codigo: String = "",
    val imagen: String = "",
    val estaBloqueado: Boolean = false,
    val categoriasDisponibles: List<Categoria> = emptyList(),

    // Estado de la UI
    val tituloPantalla: String = "Nuevo Producto",
    val estaCargando: Boolean = false,
    val error: String? = null,
    val guardadoExitoso: Boolean = false
)

fun Producto.toFormUiState(): ProductoFormUiState {
    return ProductoFormUiState(
        idProducto = this.idProducto,
        idCategoria = this.idCategoria,
        nombre = this.nombreProducto,
        precio = this.precioProducto.toString(),
        descripcion = this.descripcionProducto,
        stock = this.stockProducto.toString(),
        codigo = this.codigoProducto,
        imagen = this.imagenProducto,
        tituloPantalla = "Editar Producto",
        estaBloqueado = this.estaBloqueado
    )
}