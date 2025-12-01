package com.example.pasteleriaapp.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EstadoPedidoExtensionsTest {

    @Test
    fun displayName_returnsLocalizedTextForEveryState() {
        val expected = mapOf(
            EstadoPedido.PENDIENTE to "Pedido recibido",
            EstadoPedido.EN_PREPARACION to "En preparación",
            EstadoPedido.EN_REPARTO to "En reparto",
            EstadoPedido.ENTREGADO to "Entregado",
            EstadoPedido.CANCELADO to "Cancelado"
        )

        expected.forEach { (estado, texto) ->
            assertEquals("Nombre incorrecto para $estado", texto, estado.displayName())
        }
    }

    @Test
    fun descripcion_returnsDetailedMessageForEveryState() {
        val expected = mapOf(
            EstadoPedido.PENDIENTE to "Hemos recibido tu pedido y lo estamos validando.",
            EstadoPedido.EN_PREPARACION to "Nuestro equipo está preparando tu pedido con dedicación.",
            EstadoPedido.EN_REPARTO to "Tu pedido salió a reparto, pronto lo recibirás.",
            EstadoPedido.ENTREGADO to "Pedido entregado con éxito. ¡Que lo disfrutes!",
            EstadoPedido.CANCELADO to "El pedido fue cancelado. Si tienes dudas contáctanos."
        )

        expected.forEach { (estado, texto) ->
            assertEquals("Descripción incorrecta para $estado", texto, estado.descripcion())
        }
    }

    @Test
    fun progressStep_andFraction_followTrackingOrder() {
        val tracking = trackingEstados()
        tracking.forEachIndexed { index, estado ->
            assertEquals(index, estado.progressStep())
        }

        val totalSteps = (tracking.size - 1).coerceAtLeast(1)
        tracking.forEachIndexed { index, estado ->
            val expectedFraction = index.toFloat() / totalSteps.toFloat()
            assertEquals(expectedFraction, estado.progressFraction(), 0.0001f)
        }
    }

    @Test
    fun cancelado_state_resetsProgress() {
        assertEquals(0, EstadoPedido.CANCELADO.progressStep())
        assertEquals(0f, EstadoPedido.CANCELADO.progressFraction(), 0.0f)
    }

    @Test
    fun trackingEstados_returnsOrderedTrackingList() {
        val estados = trackingEstados()
        assertEquals(listOf(
            EstadoPedido.PENDIENTE,
            EstadoPedido.EN_PREPARACION,
            EstadoPedido.EN_REPARTO,
            EstadoPedido.ENTREGADO
        ), estados)
        assertTrue(estados.none { it == EstadoPedido.CANCELADO })
    }
}
