package com.example.pasteleriaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pasteleriaapp.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT) // Abortar si el 'run' o 'correo' ya existen (requiere índices)
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuario WHERE correo = :correo LIMIT 1")
    suspend fun obtenerUsuarioPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuario WHERE run = :run LIMIT 1")
    suspend fun obtenerUsuarioPorRun(run: String): UsuarioEntity?

    // Para pre-poblar
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarUsuarios(usuarios: List<UsuarioEntity>)

    // --- FUNCIÓN NUEVA AÑADIDA ---
    @Update
    suspend fun actualizarUsuario(usuario: UsuarioEntity)
}