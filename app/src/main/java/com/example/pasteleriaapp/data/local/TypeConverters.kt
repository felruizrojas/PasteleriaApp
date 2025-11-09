package com.example.pasteleriaapp.data.local

import androidx.room.TypeConverter
import com.example.pasteleriaapp.domain.model.TipoUsuario
import com.example.pasteleriaapp.domain.model.EstadoPedido

class TypeConverters {
    @TypeConverter
    fun fromTipoUsuario(tipo: TipoUsuario): String {
        return tipo.name
    }

    @TypeConverter
    fun toTipoUsuario(valor: String): TipoUsuario {
        return enumValueOf(valor)
    }

    @TypeConverter
    fun fromEstadoPedido(estado: EstadoPedido): String {
        return estado.name
    }

    @TypeConverter
    fun toEstadoPedido(valor: String): EstadoPedido {
        return enumValueOf(valor)
    }
}