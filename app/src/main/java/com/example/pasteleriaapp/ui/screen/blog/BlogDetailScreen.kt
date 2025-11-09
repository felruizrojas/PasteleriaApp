package com.example.pasteleriaapp.ui.screen.blog

// --- IMPORTS AÑADIDOS ---
import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.example.pasteleriaapp.domain.model.BlogPost
// --- FIN IMPORTS AÑADIDOS ---

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.domain.model.BlogData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogDetailScreen(
    postId: String,
    onBackClick: () -> Unit
) {
    // Obtenemos el post estático
    val post = remember { BlogData.getPostById(postId) }
    // Obtenemos el contexto para el Intent de compartir
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        post?.titulo ?: "Artículo",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                // --- BOTÓN DE COMPARTIR AÑADIDO ---
                actions = {
                    if (post != null) {
                        IconButton(onClick = {
                            compartirBlogPost(context, post)
                        }) {
                            Icon(Icons.Default.Share, "Compartir artículo")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (post == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Artículo no encontrado.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = post.imagenRes),
                contentDescription = post.titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = post.titulo,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = post.autor,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(16.dp))
                Text(
                    text = post.contenido,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.fontSize * 1.5
                )
            }
        }
    }
}

/**
 * --- NUEVA FUNCIÓN AUXILIAR AÑADIDA ---
 * Crea un Intent de Android para compartir el artículo del blog.
 */
private fun compartirBlogPost(context: Context, post: BlogPost) {
    // Tomamos solo los primeros 100 caracteres del contenido para el resumen
    val resumenContenido = post.contenido.take(100) + "..."

    val textoCompartir = """
¡Mira este artículo de Pastelería Mil Sabores!

*${post.titulo}*
${post.autor}

$resumenContenido

(Lee el artículo completo en nuestra app)
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, post.titulo)
        putExtra(Intent.EXTRA_TEXT, textoCompartir)
    }

    context.startActivity(
        Intent.createChooser(intent, "Compartir artículo en...")
    )
}