package com.example.pasteleriaapp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pasteleriaapp.domain.repository.CategoriaRepository
import com.example.pasteleriaapp.domain.repository.ProductoRepository
import com.example.pasteleriaapp.ui.screen.home.HomeScreen
import com.example.pasteleriaapp.ui.screen.productos.CategoriasScreen
import com.example.pasteleriaapp.ui.screen.productos.ProductoDetalleScreen
import com.example.pasteleriaapp.ui.screen.productos.ProductoFormScreen
import com.example.pasteleriaapp.ui.screen.productos.ProductoListScreen
import com.example.pasteleriaapp.ui.viewmodel.CategoriaViewModel
import com.example.pasteleriaapp.ui.viewmodel.CategoriaViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.ProductoDetalleViewModel
import com.example.pasteleriaapp.ui.viewmodel.ProductoDetalleViewModelFactory
// Asegúrate también de haber creado ProductoFormViewModel y su Factory
import com.example.pasteleriaapp.ui.viewmodel.ProductoFormViewModel
import com.example.pasteleriaapp.ui.viewmodel.ProductoFormViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.ProductoViewModel
import com.example.pasteleriaapp.ui.viewmodel.ProductoViewModelFactory

@Composable
fun AppNavGraph(
    navController: NavHostController,
    categoriaRepository: CategoriaRepository,
    productoRepository: ProductoRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Rutas.HOME,
        modifier = modifier
    ) {
        // --- 1. RUTA HOME (Pantalla de Inicio) ---
        composable(Rutas.HOME) {
            HomeScreen(
                onCatalogoClick = {
                    navController.navigate(Rutas.CATEGORIAS)
                },
                onNosotrosClick = {
                    navController.navigate(Rutas.NOSOTROS)
                },
                onLoginClick = {
                    navController.navigate(Rutas.LOGIN)
                }
            )
        }

        // --- 2. RUTA CATEGORIAS (Catálogo) ---
        composable(Rutas.CATEGORIAS) {
            val factory = CategoriaViewModelFactory(categoriaRepository)
            val viewModel: CategoriaViewModel = viewModel(factory = factory)

            CategoriasScreen(
                viewModel = viewModel,
                onCategoriaClick = { idCategoria ->
                    navController.navigate(Rutas.obtenerRutaProductos(idCategoria))
                }
            )
        }

        // --- 3. RUTA LISTA DE PRODUCTOS ---
        composable(
            route = Rutas.PRODUCTOS_RUTA,
            arguments = listOf(navArgument(Rutas.ARG_ID_CATEGORIA) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val idCategoria = backStackEntry.arguments?.getInt(Rutas.ARG_ID_CATEGORIA)
            requireNotNull(idCategoria) { "idCategoria no encontrado en la ruta" }

            val factory = ProductoViewModelFactory(productoRepository, idCategoria)
            val viewModel: ProductoViewModel = viewModel(
                key = "producto_$idCategoria",
                factory = factory
            )

            ProductoListScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onProductoClick = { idProducto ->
                    navController.navigate(Rutas.obtenerRutaDetalleProducto(idProducto))
                },
                onAddProductoClick = {
                    navController.navigate(Rutas.obtenerRutaNuevoProducto(idCategoria))
                }
            )
        }

        // --- 4. RUTA DETALLE DE PRODUCTO ---
        composable(
            route = Rutas.DETALLE_PRODUCTO_RUTA,
            arguments = listOf(navArgument(Rutas.ARG_ID_PRODUCTO) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val idProducto = backStackEntry.arguments?.getInt(Rutas.ARG_ID_PRODUCTO)
            requireNotNull(idProducto) { "idProducto no encontrado en la ruta" }

            val factory = ProductoDetalleViewModelFactory(productoRepository, idProducto)
            val viewModel: ProductoDetalleViewModel = viewModel(
                key = "detalle_producto_$idProducto",
                factory = factory
            )

            ProductoDetalleScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditProductoClick = {
                    navController.navigate(Rutas.obtenerRutaEditarProducto(it))
                }
            )
        }

        // --- 5. RUTA FORMULARIO DE PRODUCTO (Añadir/Editar) ---
        composable(
            route = Rutas.FORMULARIO_PRODUCTO,
            arguments = listOf(
                navArgument(Rutas.ARG_ID_PRODUCTO) {
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument(Rutas.ARG_ID_CATEGORIA) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val idProducto = backStackEntry.arguments?.getInt(Rutas.ARG_ID_PRODUCTO) ?: 0
            val idCategoria = backStackEntry.arguments?.getInt(Rutas.ARG_ID_CATEGORIA) ?: 0

            if (idProducto == 0 && idCategoria == 0) {
                throw IllegalArgumentException("Se requiere un idCategoria para crear un producto nuevo")
            }

            val factory = ProductoFormViewModelFactory(productoRepository, idProducto, idCategoria)
            val viewModel: ProductoFormViewModel = viewModel(
                key = "form_producto_$idProducto",
                factory = factory
            )

            // ESTA ES LA LÍNEA QUE USA EL ARCHIVO IMPORTADO
            ProductoFormScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // --- 6. RUTAS NUEVAS (Placeholders) ---

        composable(Rutas.NOSOTROS) {
            PlaceholderScreen(texto = "Pantalla 'Nosotros'")
        }

        composable(Rutas.LOGIN) {
            PlaceholderScreen(texto = "Pantalla 'Inicio de Sesión'")
        }
    }
}

/**
 * Una pantalla genérica temporal para que la navegación funcione.
 */
@Composable
private fun PlaceholderScreen(texto: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = texto, style = MaterialTheme.typography.headlineMedium)
    }
}