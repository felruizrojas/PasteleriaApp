package com.example.pasteleriaapp.data.local

import androidx.room.TypeConverter
import com.example.pasteleriaapp.domain.model.TipoUsuario

class TypeConverters {
    @TypeConverter
    fun fromTipoUsuario(tipo: TipoUsuario): String {
        return tipo.name
    }

    @TypeConverter
    fun toTipoUsuario(valor: String): TipoUsuario {
        return enumValueOf(valor)
    }
}