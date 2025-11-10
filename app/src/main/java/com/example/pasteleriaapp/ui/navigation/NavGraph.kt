package com.example.pasteleriaapp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.pasteleriaapp.domain.repository.CarritoRepository
import com.example.pasteleriaapp.domain.repository.CategoriaRepository
import com.example.pasteleriaapp.domain.repository.PedidoRepository
import com.example.pasteleriaapp.domain.repository.ProductoRepository
import com.example.pasteleriaapp.domain.repository.UsuarioRepository
import com.example.pasteleriaapp.ui.screen.auth.LoginScreen
import com.example.pasteleriaapp.ui.screen.auth.RegisterScreen
import com.example.pasteleriaapp.ui.screen.carrito.CarritoScreen
import com.example.pasteleriaapp.ui.screen.home.HomeScreen
import com.example.pasteleriaapp.ui.screen.pedidos.CheckoutScreen
import com.example.pasteleriaapp.ui.screen.pedidos.MisPedidosScreen
import com.example.pasteleriaapp.ui.screen.pedidos.PedidoDetalleScreen
import com.example.pasteleriaapp.ui.screen.profile.EditarProfileScreen
import com.example.pasteleriaapp.ui.screen.profile.ProfileScreen
import com.example.pasteleriaapp.ui.screen.blog.BlogDetailScreen
import com.example.pasteleriaapp.ui.screen.blog.BlogListScreen
import com.example.pasteleriaapp.ui.screen.productos.CategoriasScreen
import com.example.pasteleriaapp.ui.screen.productos.ProductoDetalleScreen
import com.example.pasteleriaapp.ui.screen.productos.ProductoFormScreen
import com.example.pasteleriaapp.ui.screen.productos.ProductoListScreen
import com.example.pasteleriaapp.ui.viewmodel.AuthViewModel
import com.example.pasteleriaapp.ui.viewmodel.AuthViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.CarritoViewModel
import com.example.pasteleriaapp.ui.viewmodel.CarritoViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.CategoriaViewModel
import com.example.pasteleriaapp.ui.viewmodel.CategoriaViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.PedidoViewModel
import com.example.pasteleriaapp.ui.viewmodel.PedidoViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.ProductoDetalleViewModel
import com.example.pasteleriaapp.ui.viewmodel.ProductoDetalleViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.ProductoFormViewModel
import com.example.pasteleriaapp.ui.viewmodel.ProductoFormViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.ProductoViewModel
import com.example.pasteleriaapp.ui.viewmodel.ProductoViewModelFactory
import com.example.pasteleriaapp.ui.screen.nosotros.NosotrosScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    categoriaRepository: CategoriaRepository,
    productoRepository: ProductoRepository,
    carritoRepository: CarritoRepository,
    usuarioRepository: UsuarioRepository,
    pedidoRepository: PedidoRepository,
    modifier: Modifier = Modifier
) {
    // --- ViewModels COMPARTIDOS (NIVEL SUPERIOR) ---
    val authFactory = AuthViewModelFactory(usuarioRepository)
    val authViewModel: AuthViewModel = viewModel(factory = authFactory)

    val carritoFactory = CarritoViewModelFactory(carritoRepository)
    val carritoViewModel: CarritoViewModel = viewModel(factory = carritoFactory)

    val pedidoFactory = PedidoViewModelFactory(pedidoRepository, carritoRepository)
    val pedidoViewModel: PedidoViewModel = viewModel(factory = pedidoFactory)
    // --- FIN ViewModels COMPARTIDOS ---

    NavHost(
        navController = navController, startDestination = Rutas.HOME, modifier = modifier
    ) {
        // --- 1. RUTA HOME (CORREGIDA) ---
        composable(Rutas.HOME) {
            HomeScreen(
                authViewModel = authViewModel,
                onNavigateToAuth = { navController.navigate(Rutas.AUTH_FLOW) },
                onNavigateToPerfil = { navController.navigate(Rutas.PERFIL) },
                onNavigateToCatalogo = { navController.navigate(Rutas.CATEGORIAS) },
                onNavigateToNosotros = { navController.navigate(Rutas.NOSOTROS) },
                onNavigateToCarrito = { navController.navigate(Rutas.CARRITO) },
                onNavigateToBlog = {
                    navController.navigate(Rutas.BLOG)
                },
                onLogoutSuccess = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                }
            )
        }

        // --- 2. FLUJO DE AUTENTICACIÓN ---
        authGraph(navController, authViewModel) // <-- Le pasamos el VM compartido

        // --- 3. RUTA CATEGORIAS ---
        composable(Rutas.CATEGORIAS) {
            val factory = CategoriaViewModelFactory(categoriaRepository)
            val viewModel: CategoriaViewModel = viewModel(factory = factory)
            CategoriasScreen(
                viewModel = viewModel,
                onCategoriaClick = { id -> navController.navigate(Rutas.obtenerRutaProductos(id)) },
                onCarritoClick = { navController.navigate(Rutas.CARRITO) }
            )
        }

        // --- 4. RUTA LISTA DE PRODUCTOS ---
        composable(
            route = Rutas.PRODUCTOS_RUTA,
            arguments = listOf(navArgument(Rutas.ARG_ID_CATEGORIA) { type = NavType.IntType })
        ) { backStackEntry ->
            val idCategoria = backStackEntry.arguments?.getInt(Rutas.ARG_ID_CATEGORIA)
            requireNotNull(idCategoria) { "idCategoria no encontrado en la ruta" }

            val factory = ProductoViewModelFactory(productoRepository, idCategoria)
            val viewModel: ProductoViewModel = viewModel(
                key = "producto_$idCategoria", factory = factory
            )

            ProductoListScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onProductoClick = { idProducto ->
                    navController.navigate(Rutas.obtenerRutaDetalleProducto(idProducto))
                },
                onAddProductoClick = {
                    navController.navigate(Rutas.obtenerRutaNuevoProducto(idCategoria))
                }
            )
        }

        // --- 5. RUTA DETALLE DE PRODUCTO ---
        composable(
            route = Rutas.DETALLE_PRODUCTO_RUTA,
            arguments = listOf(navArgument(Rutas.ARG_ID_PRODUCTO) { type = NavType.IntType })
        ) { backStackEntry ->
            val idProducto = backStackEntry.arguments?.getInt(Rutas.ARG_ID_PRODUCTO)
            requireNotNull(idProducto) { "idProducto no encontrado en la ruta" }

            val factory = ProductoDetalleViewModelFactory(
                productoRepository,
                carritoRepository,
                idProducto
            )
            val viewModel: ProductoDetalleViewModel = viewModel(
                key = "detalle_producto_$idProducto",
                factory = factory
            )

            ProductoDetalleScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onEditProductoClick = {
                    navController.navigate(Rutas.obtenerRutaEditarProducto(it))
                }
            )
        }

        // --- 6. RUTA FORMULARIO DE PRODUCTO ---
        composable(
            route = Rutas.FORMULARIO_PRODUCTO,
            arguments = listOf(
                navArgument(Rutas.ARG_ID_PRODUCTO) { type = NavType.IntType; defaultValue = 0 },
                navArgument(Rutas.ARG_ID_CATEGORIA) { type = NavType.IntType; defaultValue = 0 }
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

            ProductoFormScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

// --- 7. RUTA NOSOTROS (MODIFICADA) ---
        composable(Rutas.NOSOTROS) {
            // PlaceholderScreen(texto = "Pantalla 'Nosotros'") // <-- ELIMINADO
            NosotrosScreen( // <-- AÑADIDO
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- 8. RUTA CARRITO ---
        composable(Rutas.CARRITO) {
            CarritoScreen(
                viewModel = carritoViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToCheckout = {
                    navController.navigate(Rutas.CHECKOUT)
                }
            )
        }

        // --- 9. RUTA PERFIL ---
        composable(Rutas.PERFIL) {
            ProfileScreen(
                authViewModel = authViewModel,
                onNavigateToEdit = { navController.navigate(Rutas.EDITAR_PERFIL) },
                onBackClick = { navController.popBackStack() },
                onNavigateToMisPedidos = {
                    navController.navigate(Rutas.MIS_PEDIDOS)
                }
            )
        }

        // --- 10. RUTA EDITAR PERFIL ---
        composable(Rutas.EDITAR_PERFIL) {
            EditarProfileScreen(
                authViewModel = authViewModel,
                onEditSuccess = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- 11. RUTA CHECKOUT ---
        composable(Rutas.CHECKOUT) {
            val carritoState by carritoViewModel.uiState.collectAsState()

            CheckoutScreen(
                authViewModel = authViewModel,
                pedidoViewModel = pedidoViewModel,
                carritoState = carritoState,
                onPedidoCreado = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- 12. RUTA MIS PEDIDOS ---
        composable(Rutas.MIS_PEDIDOS) {
            MisPedidosScreen(
                authViewModel = authViewModel,
                pedidoViewModel = pedidoViewModel,
                onPedidoClick = { idPedido ->
                    navController.navigate(Rutas.obtenerRutaDetallePedido(idPedido))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- 13. RUTA DETALLE DE PEDIDO ---
        composable(
            route = Rutas.PEDIDO_DETALLE_RUTA,
            arguments = listOf(navArgument("idPedido") { type = NavType.IntType })
        ) { backStackEntry ->
            val idPedido = backStackEntry.arguments?.getInt("idPedido")
            requireNotNull(idPedido) { "idPedido no encontrado" }

            PedidoDetalleScreen(
                idPedido = idPedido,
                pedidoViewModel = pedidoViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- 14. RUTA LISTA DE BLOG ---
        composable(Rutas.BLOG) {
            BlogListScreen(
                onPostClick = { postId ->
                    navController.navigate(Rutas.obtenerRutaBlogDetalle(postId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- 15. RUTA DETALLE DE BLOG ---
        composable(
            route = Rutas.BLOG_DETALLE_RUTA,
            arguments = listOf(navArgument(Rutas.ARG_POST_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString(Rutas.ARG_POST_ID)
            requireNotNull(postId) { "postId no encontrado" }

            BlogDetailScreen(
                postId = postId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}


// --- authGraph (CORREGIDO) ---
private fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel // <-- Recibe el VM compartido
) {
    navigation(
        startDestination = Rutas.LOGIN, route = Rutas.AUTH_FLOW
    ) {
        // --- Pantalla de Login (CORREGIDA) ---
        composable(Rutas.LOGIN) {
            // ¡No creamos un VM nuevo! Simplemente usamos el que nos pasaron.
            LoginScreen(
                viewModel = authViewModel, // <-- Usa el VM compartido
                onLoginSuccess = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Rutas.REGISTRO)
                }
            )
        }

        // --- Pantalla de Registro (CORREGIDA) ---
        composable(Rutas.REGISTRO) {
            // ¡No creamos un VM nuevo! Simplemente usamos el que nos pasaron.
            RegisterScreen(
                viewModel = authViewModel, // <-- Usa el VM compartido
                onRegisterSuccess = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}


/**
 * Una pantalla genérica temporal para que la navegación funcione.
 */
@Composable
private fun PlaceholderScreen(texto: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(text = texto, style = MaterialTheme.typography.headlineMedium)
    }
}