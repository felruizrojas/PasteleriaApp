package com.example.pasteleriaapp.ui.screen.nosotros

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NosotrosScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nosotros") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp), // Padding general para el contenido
            verticalArrangement = Arrangement.spacedBy(24.dp) // Espacio entre secciones
        ) {
            // --- Sección 1: Quiénes Somos ---
            InfoSection(
                title = "Quiénes Somos",
                imageName = "vista_pasteleria_mil_sabores",
                text = "En Pastelería 1000 Sabores celebramos 50 años de historia endulzando momentos únicos y siendo un referente de la repostería chilena. Desde nuestro inolvidable récord Guinness en 1995, cuando colaboramos en la creación de la torta más grande del mundo, hemos mantenido la tradición de innovar y sorprender con cada creación.\n\nHoy renovamos nuestro sistema de ventas online para que nuestros clientes disfruten de una experiencia de compra moderna, fácil y accesible, llevando la dulzura directamente a sus hogares."
            )

            // --- Sección 2: Misión ---
            InfoSection(
                title = "Misión",
                imageName = "persona_trabajando_en_una_cocina",
                text = "Ofrecer una experiencia dulce y memorable a nuestros clientes, proporcionando tortas y productos de repostería de alta calidad para todas las ocasiones, mientras celebramos nuestras raíces históricas y fomentamos la creatividad en la repostería."
            )

            // --- Sección 3: Visión ---
            InfoSection(
                title = "Visión",
                imageName = "diversos_productos",
                text = "Convertirnos en la tienda online líder de productos de repostería en Chile, conocida por nuestra innovación, calidad y el impacto positivo en la comunidad, especialmente en la formación de nuevos talentos en gastronomía."
            )

            // --- Sección 4: Impacto Comunitario ---
            InfoSection(
                title = "Impacto Comunitario",
                imageName = "estudiante_de_reposteria_aprendiendo_en_la_cocina",
                text = "Cada compra en Pastelería 1000 Sabores apoya a estudiantes de gastronomía y a la comunidad local, contribuyendo a que nuevas generaciones de reposteros sigan creando y compartiendo su arte."
            )
        }
    }
}

/**
 * Un Composable reutilizable para mostrar una sección de [Imagen - Título - Texto]
 */
@Composable
private fun InfoSection(
    title: String,
    imageName: String,
    text: String
) {
    val context = LocalContext.current
    val imageResId = painterResourceFromName(context, imageName)

    Column(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f) // Ratio panorámico
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Función auxiliar para obtener un ID de drawable a partir de su nombre (String).
 */
@DrawableRes
@Composable
private fun painterResourceFromName(context: Context, resName: String): Int {
    return try {
        // Asumimos que las imágenes (ej. "diversos_productos.jpg")
        // se guardan en res/drawable como "diversos_productos"
        val cleanResName = resName.substringBefore(".")
        val resId = context.resources.getIdentifier(cleanResName, "drawable", context.packageName)
        if (resId == 0) {
            R.drawable.ic_launcher_background
        } else {
            resId
        }
    } catch (e: Exception) {
        R.drawable.ic_launcher_background
    }
}