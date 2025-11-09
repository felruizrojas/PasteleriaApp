package com.example.pasteleriaapp.ui.screen.productos

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.domain.model.Categoria
import com.example.pasteleriaapp.ui.viewmodel.CategoriaViewModel
import com.example.pasteleriaapp.R // <-- IMPORTANTE: Importa R

/**
 * Pantalla que muestra la lista de categorías.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
    viewModel: CategoriaViewModel,
    onCategoriaClick: (Int) -> Unit // Lambda para navegar
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Catálogo de Productos") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.estaCargando -> {
                    CircularProgressIndicator()
                }
                state.error != null -> {
                    Text(text = "Error: ${state.error}")
                }
                state.hayCategorias -> {
                    // --- MODIFICADO: Usamos una Grilla ---
                    CategoriasGrid(
                        categorias = state.categorias,
                        onCategoriaClick = onCategoriaClick
                    )
                }
                else -> {
                    Text(text = "No hay categorías disponibles.")
                }
            }
        }
    }
}

/**
 * Muestra la grilla de categorías.
 */
@Composable
private fun CategoriasGrid(
    categorias: List<Categoria>,
    onCategoriaClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Grilla de 2 columnas
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(categorias) { categoria ->
            CategoriaCard(
                categoria = categoria,
                onClick = { onCategoriaClick(categoria.idCategoria) }
            )
        }
    }
}

/**
 * Representa una sola Card de categoría.
 */
@Composable
private fun CategoriaCard(
    categoria: Categoria,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    // Obtenemos el ID del drawable usando el nombre de la imagen
    val imageResId = painterResourceFromName(context, categoria.imagenCategoria)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Imagen de ${categoria.nombreCategoria}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f), // Imagen cuadrada
                contentScale = ContentScale.Crop
            )
            Text(
                text = categoria.nombreCategoria,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Función auxiliar para obtener un ID de drawable a partir de su nombre (String).
 */
@DrawableRes
@Composable
private fun painterResourceFromName(context: Context, resName: String): Int {
    return try {
        val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
        if (resId == 0) {
            // Si no se encuentra, usamos una imagen de fallback (debes tenerla en drawable)
            R.drawable.ic_launcher_background // Cambia esto por una imagen genérica si quieres
        } else {
            resId
        }
    } catch (e: Exception) {
        R.drawable.ic_launcher_background // Fallback en caso de error
    }
}