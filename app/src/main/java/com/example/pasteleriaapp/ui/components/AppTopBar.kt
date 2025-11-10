package com.example.pasteleriaapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import com.example.pasteleriaapp.R

data class AppTopBarActions(
    val onNavigateToCatalogo: () -> Unit,
    val onNavigateToBlog: () -> Unit,
    val onNavigateToNosotros: () -> Unit,
    val onOpenInstagram: () -> Unit,
    val onCartClick: () -> Unit,
    val onProfileClick: () -> Unit,
    val onLoginClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    badgeCount: Int,
    isLoggedIn: Boolean,
    actions: AppTopBarActions,
    modifier: Modifier = Modifier,
    extraActions: @Composable RowScope.() -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        modifier = modifier,
        navigationIcon = {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menú")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Catálogo") },
                        onClick = {
                            menuExpanded = false
                            actions.onNavigateToCatalogo()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Blog") },
                        onClick = {
                            menuExpanded = false
                            actions.onNavigateToBlog()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Nosotros") },
                        onClick = {
                            menuExpanded = false
                            actions.onNavigateToNosotros()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Instagram") },
                        onClick = {
                            menuExpanded = false
                            actions.onOpenInstagram()
                        }
                    )
                }
            }
        },
        title = {
            Image(
                painter = painterResource(id = R.drawable.logo_nav),
                contentDescription = "Pastelería Mil Sabores",
                modifier = Modifier,
                contentScale = ContentScale.Fit
            )
        },
        actions = {
            BadgedBox(
                badge = {
                    if (badgeCount > 0) {
                        Badge {
                            Text(if (badgeCount > 99) "99+" else badgeCount.toString())
                        }
                    }
                }
            ) {
                IconButton(onClick = actions.onCartClick) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                }
            }

            IconButton(onClick = if (isLoggedIn) actions.onProfileClick else actions.onLoginClick) {
                Icon(Icons.Default.Person, contentDescription = if (isLoggedIn) "Perfil" else "Iniciar sesión")
            }

            extraActions()
        }
    )
}

@Composable
fun AppScaffold(
    badgeCount: Int,
    isLoggedIn: Boolean,
    topBarActions: AppTopBarActions,
    modifier: Modifier = Modifier,
    extraActions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                badgeCount = badgeCount,
                isLoggedIn = isLoggedIn,
                actions = topBarActions,
                extraActions = extraActions
            )
        },
        bottomBar = bottomBar
    ) { innerPadding ->
        content(innerPadding)
    }
}
