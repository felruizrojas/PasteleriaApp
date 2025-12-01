package com.example.pasteleriaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pasteleriaapp.data.local.entity.UsuarioEntity
import com.example.pasteleriaapp.domain.model.TipoUsuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT) // Abortar si el 'run' o 'correo' ya existen (requiere índices)
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuario WHERE correo = :correo LIMIT 1")
    suspend fun obtenerUsuarioPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuario WHERE run = :run LIMIT 1")
    suspend fun obtenerUsuarioPorRun(run: String): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuarios(usuarios: List<UsuarioEntity>)

    @Query("SELECT * FROM usuario")
    suspend fun obtenerUsuariosSnapshot(): List<UsuarioEntity>

    @Update
    suspend fun actualizarUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuario ORDER BY tipoUsuario, nombre")
    fun observarUsuarios(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuario WHERE idUsuario = :idUsuario LIMIT 1")
    suspend fun obtenerUsuarioPorId(idUsuario: Int): UsuarioEntity?

    @Query("UPDATE usuario SET tipoUsuario = :tipoUsuario WHERE idUsuario = :idUsuario")
    suspend fun actualizarTipoUsuario(idUsuario: Int, tipoUsuario: TipoUsuario)

    @Query("UPDATE usuario SET estaBloqueado = :estaBloqueado WHERE idUsuario = :idUsuario")
    suspend fun actualizarEstadoBloqueo(idUsuario: Int, estaBloqueado: Boolean)

    @Query("DELETE FROM usuario WHERE idUsuario = :idUsuario")
    suspend fun eliminarUsuario(idUsuario: Int)
}