package com.example.pasteleriaapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.R

data class AppTopBarActions(
    val onNavigateToCatalogo: () -> Unit,
    val onNavigateToBlog: () -> Unit,
    val onNavigateToNosotros: () -> Unit,
    val onOpenInstagram: () -> Unit,
    val onCartClick: () -> Unit,
    val onProfileClick: () -> Unit,
    val onLoginClick: () -> Unit,
    val onNavigateToAdmin: (() -> Unit)? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    badgeCount: Int,
    isLoggedIn: Boolean,
    actions: AppTopBarActions,
    modifier: Modifier = Modifier,
    onLogout: (() -> Unit)? = null
) {
    var menuExpanded by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        modifier = modifier,
        navigationIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
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

                Spacer(modifier = Modifier.width(6.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo_nav),
                    contentDescription = "Pastelería Mil Sabores",
                    modifier = Modifier.size(28.dp),
                    contentScale = ContentScale.Fit
                )
            }
        },
        title = {},
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

            actions.onNavigateToAdmin?.let { navigateToAdmin ->
                IconButton(onClick = navigateToAdmin) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Administración"
                    )
                }
            }

            IconButton(onClick = if (isLoggedIn) actions.onProfileClick else actions.onLoginClick) {
                Icon(Icons.Default.Person, contentDescription = if (isLoggedIn) "Perfil" else "Iniciar sesión")
            }

            if (isLoggedIn && onLogout != null) {
                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Cerrar sesión"
                    )
                }
            }
        }
    )
}

@Composable
fun AppScaffold(
    badgeCount: Int,
    isLoggedIn: Boolean,
    topBarActions: AppTopBarActions,
    pageTitle: String? = null,
    modifier: Modifier = Modifier,
    onLogout: (() -> Unit)? = null,
    floatingActionButton: (@Composable () -> Unit)? = null,
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
                onLogout = onLogout
            )
        },
        floatingActionButton = floatingActionButton ?: {},
        bottomBar = bottomBar
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (pageTitle != null) {
                Text(
                    text = pageTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    textAlign = TextAlign.Start
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                content(PaddingValues())
            }
        }
    }
}
