package com.example.pasteleriaapp.core.pricing

import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.model.Usuario
/**
 * Centraliza la lógica de descuentos aplicada sobre el subtotal del carrito.
 */
object PricingCalculator {
    private const val DESCUENTO_EDAD = 0.10
    private const val DESCUENTO_PROMO = 0.50
    private const val DESCUENTO_DUOC = 0.15
    private const val MAX_DESCUENTO_ACUMULADO = 0.70

    fun calcularResumen(items: List<CarritoItem>, usuario: Usuario?): PricingSummary {
        val subtotal = items.sumOf { it.precioProducto * it.cantidad }
        if (subtotal <= 0.0) {
            return PricingSummary(subtotal = 0.0, descuento = 0.0, total = 0.0)
        }

        val tasaDescuento = usuario?.let { u ->
            var acumulado = 0.0
            if (u.tieneDescuentoEdad) acumulado += DESCUENTO_EDAD
            if (u.tieneDescuentoCodigo) acumulado += DESCUENTO_PROMO
            if (u.esEstudianteDuoc) acumulado += DESCUENTO_DUOC
            acumulado.coerceAtMost(MAX_DESCUENTO_ACUMULADO)
        } ?: 0.0

        val descuento = subtotal * tasaDescuento
        val total = (subtotal - descuento).coerceAtLeast(0.0)

        return PricingSummary(
            subtotal = subtotal,
            descuento = descuento,
            total = total
        )
    }
}

data class PricingSummary(
    val subtotal: Double,
    val descuento: Double,
    val total: Double
) {
    val tieneDescuento: Boolean get() = descuento > 0.0
    fun formatoMoneda(valor: Double): String = "%.0f".format(valor)
    val subtotalFormateado get() = formatoMoneda(subtotal)
    val descuentoFormateado get() = formatoMoneda(descuento)
    val totalFormateado get() = formatoMoneda(total)
}
