package com.example.pasteleriaapp.ui.screen.blog

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.domain.model.BlogData
import com.example.pasteleriaapp.domain.model.BlogPost
import com.example.pasteleriaapp.ui.components.AppScaffold
import com.example.pasteleriaapp.ui.components.AppTopBarActions

@Composable
fun BlogListScreen(
    onPostClick: (String) -> Unit,
    onBackClick: () -> Unit,
    badgeCount: Int,
    isLoggedIn: Boolean,
    topBarActions: AppTopBarActions,
    onLogout: (() -> Unit)?
) {
    val posts = BlogData.posts

    AppScaffold(
        badgeCount = badgeCount,
        isLoggedIn = isLoggedIn,
        topBarActions = topBarActions,
        pageTitle = "Blog",
        onLogout = onLogout
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TextButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Volver")
                }
            }

            items(posts) { post ->
                BlogItemCard(
                    post = post,
                    onClick = { onPostClick(post.id) }
                )
            }
        }
    }
}

@Composable
fun BlogItemCard(
    post: BlogPost,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = post.imagenRes),
                contentDescription = post.titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = post.titulo,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = post.autor,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}