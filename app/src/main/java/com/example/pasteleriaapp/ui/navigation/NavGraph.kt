package com.example.pasteleriaapp.ui.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.pasteleriaapp.domain.model.TipoUsuario
import com.example.pasteleriaapp.domain.repository.CarritoRepository
import com.example.pasteleriaapp.domain.repository.CategoriaRepository
import com.example.pasteleriaapp.domain.repository.PedidoRepository
import com.example.pasteleriaapp.domain.repository.ProductoRepository
import com.example.pasteleriaapp.domain.repository.UsuarioRepository
import com.example.pasteleriaapp.ui.screen.admin.UsuariosAdminScreen
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
import com.example.pasteleriaapp.ui.components.AppTopBarActions
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
import com.example.pasteleriaapp.ui.viewmodel.UserManagementViewModel
import com.example.pasteleriaapp.ui.viewmodel.UserManagementViewModelFactory
import com.example.pasteleriaapp.ui.screen.admin.AdminCatalogContent
import com.example.pasteleriaapp.ui.viewmodel.AdminCatalogViewModelFactory

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

    val authFactory = AuthViewModelFactory(usuarioRepository)
    val authViewModel: AuthViewModel = viewModel(factory = authFactory)

    val ctx = androidx.compose.ui.platform.LocalContext.current
    androidx.compose.runtime.LaunchedEffect(Unit) {
        authViewModel.restoreSession(ctx)
    }

    val carritoFactory = CarritoViewModelFactory(carritoRepository)
    val carritoViewModel: CarritoViewModel = viewModel(factory = carritoFactory)

    val pedidoFactory = PedidoViewModelFactory(pedidoRepository, carritoRepository)
    val pedidoViewModel: PedidoViewModel = viewModel(factory = pedidoFactory)

    val authState by authViewModel.uiState.collectAsState()
    val carritoUiState by carritoViewModel.uiState.collectAsState()

    val isLoggedIn = authState.usuarioActual != null
    val badgeCount = carritoUiState.totalArticulos
    val puedeAdministrar = authState.usuarioActual?.let {
        it.tipoUsuario == TipoUsuario.superAdmin ||
                it.tipoUsuario == TipoUsuario.Administrador ||
                it.tipoUsuario == TipoUsuario.Vendedor
    } == true

    LaunchedEffect(authState.logoutSuccess) {
        if (authState.logoutSuccess) {
            navController.navigate(Rutas.HOME) {
                popUpTo(Rutas.HOME) { inclusive = true }
            }
            authViewModel.resetNavegacion()
        }
    }

    val onLogout: (() -> Unit)? = if (isLoggedIn) {
        { authViewModel.logout(ctx) }
    } else null

    val openInstagram: () -> Unit = {
        val instagramIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com"))
        if (instagramIntent.resolveActivity(ctx.packageManager) != null) {
            ctx.startActivity(instagramIntent)
        }
    }

    val topBarActions = AppTopBarActions(
        onNavigateToCatalogo = { navController.navigateSingleTop(Rutas.CATEGORIAS) },
        onNavigateToBlog = { navController.navigateSingleTop(Rutas.BLOG) },
        onNavigateToNosotros = { navController.navigateSingleTop(Rutas.NOSOTROS) },
        onOpenInstagram = openInstagram,
        onCartClick = { navController.navigateSingleTop(Rutas.CARRITO) },
        onProfileClick = { navController.navigateSingleTop(Rutas.PERFIL) },
        onLoginClick = { navController.navigateSingleTop(Rutas.AUTH_FLOW) },
        onNavigateToAdmin = if (puedeAdministrar) {
            { navController.navigateSingleTop(Rutas.ADMIN_CATALOGO) }
        } else null
    )

    NavHost(
        navController = navController, startDestination = Rutas.HOME, modifier = modifier
    ) {
        // --- 1. RUTA HOME ---
        composable(Rutas.HOME) {
            HomeScreen(
                authViewModel = authViewModel,
                carritoViewModel = carritoViewModel,
                onNavigateToAuth = { navController.navigateSingleTop(Rutas.AUTH_FLOW) },
                onNavigateToPerfil = { navController.navigateSingleTop(Rutas.PERFIL) },
                onNavigateToCatalogo = { navController.navigateSingleTop(Rutas.CATEGORIAS) },
                onNavigateToNosotros = { navController.navigateSingleTop(Rutas.NOSOTROS) },
                onNavigateToCarrito = { navController.navigateSingleTop(Rutas.CARRITO) },
                onNavigateToBlog = { navController.navigateSingleTop(Rutas.BLOG) },
                onOpenInstagram = openInstagram
            )
        }

        // --- 2. FLUJO DE AUTENTICACIÓN ---
        authGraph(
            navController = navController,
            authViewModel = authViewModel,
            badgeCount = badgeCount,
            isLoggedIn = isLoggedIn,
            topBarActions = topBarActions,
            onLogout = onLogout
        )

        // --- 3. RUTA CATEGORIAS ---
        composable(Rutas.CATEGORIAS) {
            val factory = CategoriaViewModelFactory(categoriaRepository)
            val viewModel: CategoriaViewModel = viewModel(factory = factory)
            CategoriasScreen(
                viewModel = viewModel,
                onCategoriaClick = { id -> navController.navigate(Rutas.obtenerRutaProductos(id)) },
                badgeCount = badgeCount,
                isLoggedIn = isLoggedIn,
                topBarActions = topBarActions,
                onLogout = onLogout
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
                },
                badgeCount = badgeCount,
                isLoggedIn = isLoggedIn,
                topBarActions = topBarActions,
                onLogout = onLogout
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
                },
                badgeCount = badgeCount,
                isLoggedIn = isLoggedIn,
                topBarActions = topBarActions,
                onLogout = onLogout
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
            NosotrosScreen(
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
                },
                badgeCount = badgeCount,
                isLoggedIn = isLoggedIn,
                topBarActions = topBarActions,
                onLogout = onLogout
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
                },
                onNavigateToAdminPanel = {
                    navController.navigateSingleTop(Rutas.ADMIN_CATALOGO)
                },
                badgeCount = badgeCount,
                isLoggedIn = isLoggedIn,
                topBarActions = topBarActions,
                onLogout = onLogout
            )
        }

        // --- 9b. RUTA ADMINISTRACIÓN DE USUARIOS ---
        composable(Rutas.ADMIN_USUARIOS) {
            val usuarioActual = authState.usuarioActual
            if (usuarioActual == null ||
                (usuarioActual.tipoUsuario != TipoUsuario.superAdmin &&
                        usuarioActual.tipoUsuario != TipoUsuario.Administrador &&
                        usuarioActual.tipoUsuario != TipoUsuario.Vendedor)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes permiso para acceder a esta sección.")
                }
            } else {
                val factory = UserManagementViewModelFactory(usuarioRepository)
                val adminViewModel: UserManagementViewModel = viewModel(factory = factory)
                UsuariosAdminScreen(
                    viewModel = adminViewModel,
                    currentUser = usuarioActual,
                    onBackClick = { navController.popBackStack() },
                    badgeCount = badgeCount,
                    isLoggedIn = isLoggedIn,
                    topBarActions = topBarActions,
                    onLogout = onLogout
                )
            }
        }

        // --- ADMIN: CATALOGO (categorías y productos) ---
        composable(Rutas.ADMIN_CATALOGO) {
            val usuarioActual = authState.usuarioActual
            if (usuarioActual == null ||
                (usuarioActual.tipoUsuario != TipoUsuario.superAdmin &&
                        usuarioActual.tipoUsuario != TipoUsuario.Administrador &&
                        usuarioActual.tipoUsuario != TipoUsuario.Vendedor)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes permiso para acceder a esta sección.")
                }
            } else {
                val factory = AdminCatalogViewModelFactory(categoriaRepository, productoRepository)
                val adminCatalogViewModel: androidx.lifecycle.ViewModel = viewModel(factory = factory)
                @Suppress("UNCHECKED_CAST")
                AdminCatalogContent(
                    viewModel = adminCatalogViewModel as com.example.pasteleriaapp.ui.viewmodel.AdminCatalogViewModel,
                    onAgregarProducto = { idCategoria -> navController.navigate(Rutas.obtenerRutaNuevoProducto(idCategoria)) },
                    onEditarProducto = { idProducto, idCategoria -> navController.navigate(Rutas.obtenerRutaEditarProducto(idProducto)) },
                    onNavigateAdmin = { destino ->
                        when (destino) {
                            "usuarios" -> navController.navigateSingleTop(Rutas.ADMIN_USUARIOS)
                            "productos" -> navController.navigateSingleTop(Rutas.CATEGORIAS)
                            "pedidos" -> navController.navigateSingleTop(Rutas.MIS_PEDIDOS)
                            "tienda" -> navController.navigateSingleTop(Rutas.HOME)
                            else -> Unit
                        }
                    }
                )
            }
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
                onBackClick = { navController.popBackStack() },
                badgeCount = badgeCount,
                isLoggedIn = isLoggedIn,
                topBarActions = topBarActions,
                onLogout = onLogout
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
                onBackClick = { navController.popBackStack() },
                badgeCount = badgeCount,
                isLoggedIn = isLoggedIn,
                topBarActions = topBarActions,
                onLogout = onLogout
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
                onBackClick = { navController.popBackStack() },
                badgeCount = badgeCount,
                isLoggedIn = isLoggedIn,
                topBarActions = topBarActions,
                onLogout = onLogout
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

private fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    badgeCount: Int,
    isLoggedIn: Boolean,
    topBarActions: AppTopBarActions,
    onLogout: (() -> Unit)?
) {
    navigation(
        startDestination = Rutas.LOGIN, route = Rutas.AUTH_FLOW
    ) {
        // --- Pantalla de Login ---
        composable(Rutas.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
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
                viewModel = authViewModel,
                onRegisterSuccess = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() },
                badgeCount = badgeCount,
                isLoggedIn = isLoggedIn,
                topBarActions = topBarActions,
                onLogout = onLogout
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(texto: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(text = texto, style = MaterialTheme.typography.headlineMedium)
    }
}

private fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}