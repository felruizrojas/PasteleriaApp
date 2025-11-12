package com.example.pasteleriaapp.core.pricing

import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.model.Usuario
import java.util.Calendar

/**
 * Centraliza la lógica de descuentos aplicada sobre el subtotal del carrito.
 */
object PricingCalculator {
    private const val DESCUENTO_EDAD = 0.50
    private const val DESCUENTO_PROMO = 0.10

    fun calcularResumen(items: List<CarritoItem>, usuario: Usuario?): PricingSummary {
        val subtotal = items.sumOf { it.precioProducto * it.cantidad }
        if (subtotal <= 0.0) {
            return PricingSummary(subtotal = 0.0, descuento = 0.0, total = 0.0)
        }

        val usuarioActual = usuario

        if (usuarioActual != null && usuarioActual.esEstudianteDuoc && esCumpleanosHoy(usuarioActual.fechaNacimiento)) {
            return PricingSummary(subtotal = subtotal, descuento = subtotal, total = 0.0)
        }

        val tasaDescuento = usuarioActual?.let { u ->
            var acumulado = 0.0
            val aplicaDescuentoEdad = esMayorOIgualCincuenta(u.fechaNacimiento) || u.tieneDescuentoEdad
            if (aplicaDescuentoEdad) acumulado += DESCUENTO_EDAD
            if (u.tieneDescuentoCodigo) acumulado += DESCUENTO_PROMO
            acumulado.coerceIn(0.0, 1.0)
        } ?: 0.0

        val descuento = subtotal * tasaDescuento
        val total = (subtotal - descuento).coerceAtLeast(0.0)

        return PricingSummary(
            subtotal = subtotal,
            descuento = descuento,
            total = total
        )
    }
    private fun esCumpleanosHoy(fechaNacimiento: String): Boolean {
        val partes = fechaNacimiento.split("-")
        if (partes.size != 3) return false
        val dia = partes[0].toIntOrNull() ?: return false
        val mes = partes[1].toIntOrNull() ?: return false

        val hoy = Calendar.getInstance()
        val diaActual = hoy.get(Calendar.DAY_OF_MONTH)
        val mesActual = hoy.get(Calendar.MONTH) + 1

        return diaActual == dia && mesActual == mes
    }

    private fun esMayorOIgualCincuenta(fechaNacimiento: String): Boolean {
        val partes = fechaNacimiento.split("-")
        if (partes.size != 3) return false
        val dia = partes[0].toIntOrNull() ?: return false
        val mes = partes[1].toIntOrNull() ?: return false
        val ano = partes[2].toIntOrNull() ?: return false

        val hoy = Calendar.getInstance()
        val nacimiento = Calendar.getInstance().apply {
            set(ano, mes - 1, dia, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
        if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
            edad--
        }

        return edad >= 50
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
