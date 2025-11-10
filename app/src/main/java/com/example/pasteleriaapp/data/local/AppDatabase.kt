package com.example.pasteleriaapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pasteleriaapp.data.local.dao.CarritoDao
import com.example.pasteleriaapp.data.local.dao.CategoriaDao
import com.example.pasteleriaapp.data.local.dao.PedidoDao
import com.example.pasteleriaapp.data.local.dao.ProductoDao
import com.example.pasteleriaapp.data.local.dao.UsuarioDao
import com.example.pasteleriaapp.data.local.entity.CarritoItemEntity
import com.example.pasteleriaapp.data.local.entity.CategoriaEntity
import com.example.pasteleriaapp.data.local.entity.PedidoEntity
import com.example.pasteleriaapp.data.local.entity.PedidoProductoEntity
import com.example.pasteleriaapp.data.local.entity.ProductoEntity
import com.example.pasteleriaapp.data.local.entity.UsuarioEntity
import com.example.pasteleriaapp.domain.model.TipoUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [
        CategoriaEntity::class,
        ProductoEntity::class,
        CarritoItemEntity::class,
        UsuarioEntity::class,
        PedidoEntity::class,
        PedidoProductoEntity::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(com.example.pasteleriaapp.data.local.TypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriaDao(): CategoriaDao
    abstract fun productoDao(): ProductoDao
    abstract fun carritoDao(): CarritoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun pedidoDao(): PedidoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE usuario ADD COLUMN tieneDescuentoEdad INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE usuario ADD COLUMN tieneDescuentoCodigo INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE usuario ADD COLUMN esEstudianteDuoc INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE usuario SET esEstudianteDuoc = 1 WHERE correo LIKE '%@duoc.cl' OR correo LIKE '%@profesor.duoc.cl'")
                db.execSQL("UPDATE usuario SET tieneDescuentoEdad = 1 WHERE run = '16789032-6'")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE usuario ADD COLUMN fotoUrl TEXT DEFAULT NULL")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Paso 1: eliminar duplicados por 'run', manteniendo el idUsuario mínimo
                db.execSQL(
                    "DELETE FROM usuario WHERE idUsuario NOT IN (SELECT minId FROM (SELECT MIN(idUsuario) as minId FROM usuario GROUP BY run))"
                )

                // Paso 2: eliminar duplicados por 'correo', manteniendo el idUsuario mínimo
                db.execSQL(
                    "DELETE FROM usuario WHERE idUsuario NOT IN (SELECT minId FROM (SELECT MIN(idUsuario) as minId FROM usuario GROUP BY correo))"
                )

                // Paso 3: crear índices únicos (ya no deberían fallar si no hay duplicados)
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_usuario_run ON usuario(run)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_usuario_correo ON usuario(correo)")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("UPDATE carrito SET mensajePersonalizado = TRIM(IFNULL(mensajePersonalizado, ''))")
                db.execSQL(
                    "DELETE FROM carrito WHERE idCarrito NOT IN (" +
                            "SELECT MIN(idCarrito) FROM carrito GROUP BY idProducto, mensajePersonalizado" +
                            ")"
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS idx_carrito_producto_mensaje ON carrito(idProducto, mensajePersonalizado)"
                )
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE usuario ADD COLUMN estaBloqueado INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE categoria ADD COLUMN estaBloqueada INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE producto ADD COLUMN estaBloqueado INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pasteleriaApp_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7
                    )
                    .addCallback(AppDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                        populateDatabase(
                            database.categoriaDao(),
                            database.productoDao(),
                            database.usuarioDao()
                        )
                    }
                }
            }

            suspend fun populateDatabase(
                categoriaDao: CategoriaDao,
                productoDao: ProductoDao,
                usuarioDao: UsuarioDao
            ) {
                productoDao.eliminarTodosLosProductos()
                categoriaDao.eliminarTodasLasCategorias()

                val categorias = listOf(
                    CategoriaEntity(0, "Tortas Cuadradas", "torta_cuadrada_de_chocolate"),
                    CategoriaEntity(0, "Tortas Circulares", "torta_circular_de_vainilla"),
                    CategoriaEntity(0, "Postres Individuales", "mousse_de_chocolate"),
                    CategoriaEntity(0, "Productos sin Azúcar", "torta_sin_azucar_de_naranja"),
                    CategoriaEntity(0, "Pastelería Tradicional", "empanada_de_manzana"),
                    CategoriaEntity(0, "Productos sin Gluten", "brownie_sin_gluten"),
                    CategoriaEntity(0, "Productos Veganos", "torta_vegana_de_chocolate"),
                    CategoriaEntity(0, "Tortas Especiales", "torta_especial_de_cumpleanos")
                )
                categoriaDao.insertarCategorias(categorias)

                val productos = listOf(
                    ProductoEntity(
                        0,
                        1,
                        "TC001",
                        "Torta Cuadrada de Chocolate",
                        45000.0,
                        "Deliciosa torta de chocolate con capas de ganache y un toque de avellanas. Personalizable con mensajes especiales.",
                        "torta_cuadrada_de_chocolate",
                        10,
                        3
                    ),
                    ProductoEntity(
                        0,
                        1,
                        "TC002",
                        "Torta Cuadrada de Frutas",
                        50000.0,
                        "Una mezcla de frutas frescas y crema chantilly sobre un suave bizcocho de vainilla, ideal para celebraciones.",
                        "torta_cuadrada_de_frutas",
                        10,
                        3
                    ),
                    ProductoEntity(
                        0,
                        2,
                        "TT001",
                        "Torta Circular de Vainilla",
                        40000.0,
                        "Bizcocho de vainilla clásico relleno con crema pastelera y cubierto con un glaseado dulce, perfecto para cualquier ocasión.",
                        "torta_circular_de_vainilla",
                        10,
                        3
                    ),
                    ProductoEntity(
                        0,
                        2,
                        "TT002",
                        "Torta Circular de Manjar",
                        42000.0,
                        "Torta tradicional chilena con manjar y nueces, un deleite para los amantes de los sabores dulces y clásicos.",
                        "torta_circular_de_manjar",
                        10,
                        3
                    ),
                    ProductoEntity(
                        0,
                        3,
                        "PI001",
                        "Mousse de Chocolate",
                        5000.0,
                        "Postre individual cremoso y suave, hecho con chocolate de alta calidad, ideal para los amantes del chocolate.",
                        "mousse_de_chocolate",
                        10,
                        3
                    ),
                    ProductoEntity(
                        0,
                        3,
                        "PI002",
                        "Tiramisú Clásico",
                        5500.0,
                        "Un postre italiano individual con capas de café, mascarpone y cacao, perfecto para finalizar cualquier comida.",
                        "tiramisu_clasico",
                        10,
                        3
                    ),
                    ProductoEntity(
                        0,
                        4,
                        "PSA001",
                        "Torta Sin Azúcar de Naranja",
                        48000.0,
                        "Torta ligera y deliciosa, endulzada naturalmente, ideal para quienes buscan opciones más saludables.",
                        "torta_sin_azucar_de_naranja",
                        10,
                        3
                    ),
                    ProductoEntity(
                        0,
                        4,
                        "PSA002",
                        "Cheesecake Sin Azúcar",
                        47000.0,
                        "Suave y cremoso, este cheesecake es una opción perfecta para disfrutar sin culpa.",
                        "cheesecake_sin_azucar",
                        10,
                        3
                    ),
                    ProductoEntity(
                        0,
                        5,
                        "PT001",
                        "Empanada de Manzana",
                        3000.0,
                        "Pastelería tradicional rellena de manzanas especiadas, perfecta para un dulce desayuno o merienda.",
                        "empanada_de_manzana",
                        10,
                        3
                    ),
                    ProductoEntity(
                        0,
                        5,
                        "PT002",
                        "Tarta de Santiago",
                        6000.0,
                        "Tradicional tarta española hecha con almendras, azúcar, y huevos, una delicia para los amantes de los postres clásicos.",
                        "tarta_de_santiago",
                        10,
                        3
                    ),
                    ProductoEntity(
                        0,
                        6,
                        "PG001",
                        "Brownie Sin Gluten",
                        4000.0,
                        "Rico y denso, este brownie es perfecto para quienes necesitan evitar el gluten sin sacrificar el sabor.",
                        "brownie_sin_gluten",
                        10,
                        3
                    ),
                    ProductoEntity(
                        0,
                        6,
                        "PG002",
                        "Pan Sin Gluten",
                        3500.0,
                        "Suave y esponjoso, ideal para sándwiches o para acompañar cualquier comida.",
                        "pan_sin_gluten",
                        10,
                        3
                    ),
                    ProductoEntity(
                        0,
                        7,
                        "PV001",
                        "Torta Vegana de Chocolate",
                        50000.0,
                        "Torta de chocolate húmeda y deliciosa, hecha sin productos de origen animal, perfecta para veganos.",
                        "torta_vegana_de_chocolate",
                        10,
                        3
                    ),
                    ProductoEntity(
                        0,
                        7,
                        "PV002",
                        "Galletas Veganas de Avena",
                        4500.0,
                        "Crujientes y sabrosas, estas galletas son una excelente opción para un snack saludable y vegano.",
                        "galletas_veganas_de_avena",
                        10,
                        3
                    ),
                    ProductoEntity(
                        0,
                        8,
                        "TE001",
                        "Torta Especial de Cumpleaños",
                        55000.0,
                        "Diseñada especialmente para celebraciones, personalizable con decoraciones y mensajes únicos.",
                        "torta_especial_de_cumpleanos",
                        10,
                        3
                    ),
                    ProductoEntity(
                        0,
                        8,
                        "TE002",
                        "Torta Especial de Boda",
                        60000.0,
                        "Elegante y deliciosa, esta torta está diseñada para ser el centro de atención en cualquier boda.",
                        "torta_especial_de_boda",
                        10,
                        3
                    )
                )
                productoDao.insertarProductos(productos)

                val usuarios = listOf(
                    UsuarioEntity(
                        0,
                        "11111111-1",
                        "Ana María",
                        "Pérez Soto",
                        "ana@duoc.cl",
                        "12-05-1992",
                        TipoUsuario.superAdmin,
                        "Metropolitana",
                        "Santiago",
                        "Av. Libertador 123",
                        "123q",
                        false,
                        false,
                        true,
                        false,
                        null
                    ),

                    UsuarioEntity(
                        0,
                        "12345678-5",
                        "Luis Felipe",
                        "González Fuentes",
                        "luis@duoc.cl",
                        "20-11-1989",
                        TipoUsuario.Administrador,
                        "Valparaíso",
                        "Viña del Mar",
                        "Calle 5 Norte 456",
                        "123q",
                        false,
                        false,
                        true,
                        false,
                        null
                    ),

                    UsuarioEntity(
                        0,
                        "14567832-3",
                        "Marcela Andrea",
                        "Rojas Díaz",
                        "marcela@profesor.duoc.cl",
                        "03-09-1976",
                        TipoUsuario.Vendedor,
                        "Biobío",
                        "Concepción",
                        "Pasaje Los Álamos 789",
                        "123q",
                        false,
                        false,
                        true,
                        false,
                        null
                    ),

                    UsuarioEntity(
                        0,
                        "16789032-6",
                        "Claudia Isabel",
                        "Fernández Mella",
                        "claudia.fernandez@gmail.com",
                        "04-05-1950",
                        TipoUsuario.Cliente,
                        "Maule",
                        "Talca",
                        "Av. San Miguel 876",
                        "123q",
                        true,
                        false,
                        false,
                        false,
                        null
                    )
                )
                usuarioDao.insertarUsuarios(usuarios)
            }
        }
    }
}