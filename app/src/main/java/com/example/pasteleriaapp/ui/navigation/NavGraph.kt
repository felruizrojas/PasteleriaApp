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
import com.example.pasteleriaapp.domain.repository.PedidoRepository // <-- NUEVO IMPORT
import com.example.pasteleriaapp.domain.repository.ProductoRepository
import com.example.pasteleriaapp.domain.repository.UsuarioRepository
import com.example.pasteleriaapp.ui.screen.auth.LoginScreen
import com.example.pasteleriaapp.ui.screen.auth.RegisterScreen
import com.example.pasteleriaapp.ui.screen.carrito.CarritoScreen
import com.example.pasteleriaapp.ui.screen.home.HomeScreen
// --- NUEVOS IMPORTS ---
import com.example.pasteleriaapp.ui.screen.pedidos.CheckoutScreen
import com.example.pasteleriaapp.ui.screen.pedidos.MisPedidosScreen
import com.example.pasteleriaapp.ui.screen.pedidos.PedidoDetalleScreen
import com.example.pasteleriaapp.ui.screen.profile.EditarProfileScreen
import com.example.pasteleriaapp.ui.screen.profile.ProfileScreen
// --- FIN NUEVOS IMPORTS ---
import com.example.pasteleriaapp.ui.screen.productos.CategoriasScreen
// ... (imports existentes: ProductoDetalleScreen, ProductoFormScreen, etc.)
import com.example.pasteleriaapp.ui.viewmodel.AuthViewModel
import com.example.pasteleriaapp.ui.viewmodel.AuthViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.CarritoViewModel
import com.example.pasteleriaapp.ui.viewmodel.CarritoViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.CategoriaViewModel
import com.example.pasteleriaapp.ui.viewmodel.CategoriaViewModelFactory
// ... (imports existentes: CategoriaViewModelFactory, ProductoDetalleViewModel, etc.)
// --- NUEVOS IMPORTS ---
import com.example.pasteleriaapp.ui.viewmodel.PedidoViewModel
import com.example.pasteleriaapp.ui.viewmodel.PedidoViewModelFactory
// --- FIN NUEVOS IMPORTS ---

@Composable
fun AppNavGraph(
    navController: NavHostController,
    categoriaRepository: CategoriaRepository,
    productoRepository: ProductoRepository,
    carritoRepository: CarritoRepository,
    usuarioRepository: UsuarioRepository,
    pedidoRepository: PedidoRepository, // <-- PARÁMETRO AÑADIDO
    modifier: Modifier = Modifier
) {
    // --- ViewModels COMPARTIDOS (NIVEL SUPERIOR) ---
    val authFactory = AuthViewModelFactory(usuarioRepository)
    val authViewModel: AuthViewModel = viewModel(factory = authFactory)

    // ¡ELEVADO! CarritoViewModel ahora se comparte
    val carritoFactory = CarritoViewModelFactory(carritoRepository)
    val carritoViewModel: CarritoViewModel = viewModel(factory = carritoFactory)

    // ¡NUEVO! PedidoViewModel se comparte
    val pedidoFactory = PedidoViewModelFactory(pedidoRepository, carritoRepository)
    val pedidoViewModel: PedidoViewModel = viewModel(factory = pedidoFactory)
    // --- FIN ViewModels COMPARTIDOS ---

    NavHost(
        navController = navController, startDestination = Rutas.HOME, modifier = modifier
    ) {
        // --- 1. RUTA HOME ---
        composable(Rutas.HOME) {
            HomeScreen(
                authViewModel = authViewModel,
                onNavigateToAuth = { navController.navigate(Rutas.AUTH_FLOW) },
                onNavigateToPerfil = { navController.navigate(Rutas.PERFIL) },
                onNavigateToCatalogo = { navController.navigate(Rutas.CATEGORIAS) },
                onNavigateToNosotros = { navController.navigate(Rutas.NOSOTROS) },
                onNavigateToCarrito = { navController.navigate(Rutas.CARRITO) },
                onLogoutSuccess = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                }
            )
        }

        // --- 2. FLUJO DE AUTENTICACIÓN ---
        authGraph(navController, authViewModel)

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

        // ... (Rutas 4, 5, 6: PRODUCTOS, DETALLE, FORMULARIO sin cambios) ...
        composable(Rutas.PRODUCTOS_RUTA, arguments = listOf(/*...*/)) { /*...*/ }
        composable(Rutas.DETALLE_PRODUCTO_RUTA, arguments = listOf(/*...*/)) { /*...*/ }
        composable(Rutas.FORMULARIO_PRODUCTO, arguments = listOf(/*...*/)) { /*...*/ }

        // --- 7. RUTA NOSOTROS (Placeholder) ---
        composable(Rutas.NOSOTROS) {
            PlaceholderScreen(texto = "Pantalla 'Nosotros'")
        }

        // --- 8. RUTA CARRITO (MODIFICADA) ---
        composable(Rutas.CARRITO) {
            CarritoScreen(
                viewModel = carritoViewModel, // <-- Usa el ViewModel compartido
                onBackClick = { navController.popBackStack() },
                onNavigateToCheckout = { // <-- LAMBDA AÑADIDO
                    navController.navigate(Rutas.CHECKOUT)
                }
            )
        }

        // --- 9. RUTA PERFIL (MODIFICADA) ---
        composable(Rutas.PERFIL) {
            ProfileScreen(
                authViewModel = authViewModel,
                onNavigateToEdit = { navController.navigate(Rutas.EDITAR_PERFIL) },
                onBackClick = { navController.popBackStack() },
                onNavigateToMisPedidos = { // <-- LAMBDA AÑADIDO
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

        // --- ¡¡NUEVAS RUTAS DE PEDIDO!! ---

        // --- 11. RUTA CHECKOUT ---
        composable(Rutas.CHECKOUT) {
            val carritoState by carritoViewModel.uiState.collectAsState()

            CheckoutScreen(
                authViewModel = authViewModel,
                pedidoViewModel = pedidoViewModel,
                carritoState = carritoState, // <-- Pasa el estado del carrito
                onPedidoCreado = {
                    // Vuelve a Home después de comprar
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
    }
}


// --- authGraph (MODIFICADO para usar el ViewModel pasado) ---
private fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel // <-- Recibe el ViewModel
) {
    navigation(
        startDestination = Rutas.LOGIN, route = Rutas.AUTH_FLOW
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                viewModel = authViewModel, // <-- Usa el ViewModel compartido
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

        composable(Rutas.REGISTRO) {
            RegisterScreen(
                viewModel = authViewModel, // <-- Usa el ViewModel compartido
                onRegisterSuccess = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

// ... (PlaceholderScreen sin cambios) ...

// ... (PlaceholderScreen sin cambios) ...
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