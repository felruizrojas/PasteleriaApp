package com.example.pasteleriaapp.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class RutasTest {

    @Test
    fun obtenerRutaProductos_includesCategoriaId() {
        assertEquals("productos/42", Rutas.obtenerRutaProductos(42))
    }

    @Test
    fun obtenerRutaDetalleProducto_includesProductoId() {
        assertEquals("productos/detalle/9", Rutas.obtenerRutaDetalleProducto(9))
    }

    @Test
    fun obtenerRutaEditarProducto_setsCategoriaEnCero() {
        assertEquals(
            "productos/formulario?idProducto=77&idCategoria=0",
            Rutas.obtenerRutaEditarProducto(77)
        )
    }

    @Test
    fun obtenerRutaNuevoProducto_includesCategoriaEnQuery() {
        assertEquals(
            "productos/formulario?idProducto=0&idCategoria=15",
            Rutas.obtenerRutaNuevoProducto(15)
        )
    }

    @Test
    fun obtenerRutaDetallePedido_includesPedidoId() {
        assertEquals("pedidos/123", Rutas.obtenerRutaDetallePedido(123))
    }

    @Test
    fun obtenerRutaBlogDetalle_concatenatesBlogPath() {
        assertEquals("blog/slug-abc", Rutas.obtenerRutaBlogDetalle("slug-abc"))
    }
}
