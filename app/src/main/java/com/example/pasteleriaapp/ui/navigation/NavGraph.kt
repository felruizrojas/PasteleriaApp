package com.example.pasteleriaapp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.pasteleriaapp.domain.repository.ProductoRepository
import com.example.pasteleriaapp.domain.repository.UsuarioRepository
import com.example.pasteleriaapp.ui.screen.auth.LoginScreen
import com.example.pasteleriaapp.ui.screen.auth.RegisterScreen
import com.example.pasteleriaapp.ui.screen.carrito.CarritoScreen
import com.example.pasteleriaapp.ui.screen.home.HomeScreen
// --- NUEVOS IMPORTS ---
import com.example.pasteleriaapp.ui.screen.profile.EditarProfileScreen
import com.example.pasteleriaapp.ui.screen.profile.ProfileScreen
// --- FIN NUEVOS IMPORTS ---
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
import com.example.pasteleriaapp.ui.viewmodel.ProductoDetalleViewModel
import com.example.pasteleriaapp.ui.viewmodel.ProductoDetalleViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.ProductoFormViewModel
import com.example.pasteleriaapp.ui.viewmodel.ProductoFormViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.ProductoViewModel
import com.example.pasteleriaapp.ui.viewmodel.ProductoViewModelFactory

@Composable
fun AppNavGraph(
    navController: NavHostController,
    categoriaRepository: CategoriaRepository,
    productoRepository: ProductoRepository,
    carritoRepository: CarritoRepository,
    usuarioRepository: UsuarioRepository,
    modifier: Modifier = Modifier
) {
    // --- ¡CAMBIO IMPORTANTE! ---
    // Creamos el AuthViewModel aquí, en el nivel superior.
    val authFactory = AuthViewModelFactory(usuarioRepository)
    val authViewModel: AuthViewModel = viewModel(factory = authFactory)
    // --- FIN DEL CAMBIO ---

    NavHost(
        navController = navController, startDestination = Rutas.HOME, modifier = modifier
    ) {
        // --- 1. RUTA HOME (MODIFICADA) ---
        composable(Rutas.HOME) {
            HomeScreen(
                authViewModel = authViewModel, // <-- Pasamos el ViewModel
                onNavigateToAuth = {
                    navController.navigate(Rutas.AUTH_FLOW)
                },
                onNavigateToPerfil = {
                    navController.navigate(Rutas.PERFIL)
                },
                onNavigateToCatalogo = {
                    navController.navigate(Rutas.CATEGORIAS)
                },
                onNavigateToNosotros = {
                    navController.navigate(Rutas.NOSOTROS)
                },
                onNavigateToCarrito = {
                    navController.navigate(Rutas.CARRITO)
                },
                onLogoutSuccess = { // Navega a Home al cerrar sesión
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                }
            )
        }

        // --- 2. FLUJO DE AUTENTICACIÓN (MODIFICADO) ---
        // Ahora recibe el ViewModel como parámetro
        authGraph(navController, authViewModel)

        // --- 3. RUTA CATEGORIAS (MODIFICADA) ---
        composable(Rutas.CATEGORIAS) {
            val factory = CategoriaViewModelFactory(categoriaRepository)
            val viewModel: CategoriaViewModel = viewModel(factory = factory)
            CategoriasScreen(
                viewModel = viewModel,
                onCategoriaClick = { idCategoria ->
                    navController.navigate(Rutas.obtenerRutaProductos(idCategoria))
                },
                onCarritoClick = {
                    navController.navigate(Rutas.CARRITO)
                }
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

        // --- 8. RUTA CARRITO ---
        composable(Rutas.CARRITO) {
            val factory = CarritoViewModelFactory(carritoRepository)
            val viewModel: CarritoViewModel = viewModel(factory = factory)
            CarritoScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // --- 9. ¡NUEVAS RUTAS DE PERFIL! ---

        // Muestra el perfil del usuario actual
        composable(Rutas.PERFIL) {
            ProfileScreen(
                authViewModel = authViewModel, // Usa el ViewModel compartido
                onNavigateToEdit = {
                    navController.navigate(Rutas.EDITAR_PERFIL)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Muestra el formulario para editar el perfil
        composable(Rutas.EDITAR_PERFIL) {
            EditarProfileScreen(
                authViewModel = authViewModel, // Usa el ViewModel compartido
                onEditSuccess = {
                    navController.popBackStack() // Vuelve a la pantalla de perfil
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}


// --- FUNCIÓN authGraph (MODIFICADA) ---
private fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel // <-- Recibe el ViewModel
) {
    navigation(
        startDestination = Rutas.LOGIN, route = Rutas.AUTH_FLOW
    ) {
        // --- Pantalla de Login ---
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

        // --- Pantalla de Registro ---
        composable(Rutas.REGISTRO) {
            RegisterScreen(
                viewModel = authViewModel, // <-- Usa el ViewModel compartido
                onRegisterSuccess = {
                    navController.popBackStack() // Vuelve a Login
                },
                onBackClick = {
                    navController.popBackStack() // Vuelve a Login
                }
            )
        }
    }
}

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