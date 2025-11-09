package com.example.pasteleriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
// Imports necesarios
import androidx.navigation.compose.rememberNavController
import com.example.pasteleriaapp.data.local.AppDatabase
import com.example.pasteleriaapp.data.repository.CategoriaRepositoryImpl
import com.example.pasteleriaapp.data.repository.CarritoRepositoryImpl
import com.example.pasteleriaapp.data.repository.PedidoRepositoryImpl
// --- ¡ESTA ES LA LÍNEA CORREGIDA! ---
import com.example.pasteleriaapp.domain.repository.CarritoRepository
import com.example.pasteleriaapp.data.repository.UsuarioRepositoryImpl
import com.example.pasteleriaapp.domain.repository.UsuarioRepository
import com.example.pasteleriaapp.data.repository.ProductoRepositoryImpl
import com.example.pasteleriaapp.ui.navigation.AppNavGraph
import com.example.pasteleriaapp.ui.theme.PasteleriaAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- 1. Creación de Dependencias ---
        // Obtenemos la instancia de la base de datos
        val database = AppDatabase.getDatabase(applicationContext)

        // Creamos las implementaciones de los repositorios,
        // pasándoles los DAOs desde la base de datos.
        val categoriaRepository = CategoriaRepositoryImpl(database.categoriaDao())
        val productoRepository = ProductoRepositoryImpl(database.productoDao())
        val carritoRepository = CarritoRepositoryImpl(database.carritoDao())
        val usuarioRepository = UsuarioRepositoryImpl(database.usuarioDao())
        val pedidoRepository = PedidoRepositoryImpl(database)

        // --- 2. Configuración del Contenido ---
        setContent {
            PasteleriaAppTheme {

                // Creamos el NavController, que gestionará la navegación
                val navController = rememberNavController()

                // Llamamos a nuestro NavGraph en lugar de "Greeting"
                AppNavGraph(
                    navController = navController,
                    categoriaRepository = categoriaRepository, // Le pasamos el repositorio
                    productoRepository = productoRepository,   // Le pasamos el repositorio
                    carritoRepository = carritoRepository,
                    usuarioRepository = usuarioRepository,
                    pedidoRepository = pedidoRepository,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}