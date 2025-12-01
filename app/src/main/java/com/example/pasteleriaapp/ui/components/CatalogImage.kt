package com.example.pasteleriaapp.ui.components

import android.content.Context
import android.net.Uri
import android.webkit.URLUtil
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pasteleriaapp.R
import java.io.File

@Composable
fun CatalogImage(
    imagePath: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    @DrawableRes placeholderRes: Int = R.drawable.ic_launcher_background
) {
    val context = LocalContext.current
    val placeholderPainter = painterResource(id = placeholderRes)
    val trimmedPath = imagePath?.trim().orEmpty()
    val isRemoteImage = trimmedPath.isNotBlank() && URLUtil.isValidUrl(trimmedPath)
    val isLocalFile = trimmedPath.startsWith("file://") || trimmedPath.startsWith("/")

    when {
        isRemoteImage || isLocalFile -> {
            val data: Any = when {
                isRemoteImage -> trimmedPath
                trimmedPath.startsWith("file://") -> Uri.parse(trimmedPath)
                else -> File(trimmedPath)
            }
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(data)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                modifier = modifier,
                placeholder = placeholderPainter,
                error = placeholderPainter,
                contentScale = contentScale
            )
        }
        trimmedPath.isNotBlank() -> {
            Image(
                painter = painterResource(id = context.drawableIdFromName(trimmedPath)),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        else -> {
            Image(
                painter = placeholderPainter,
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
    }
}

fun Context.drawableIdFromName(resName: String?): Int {
    if (resName.isNullOrBlank()) return R.drawable.ic_launcher_background
    return try {
        val resId = resources.getIdentifier(resName, "drawable", packageName)
        if (resId == 0) R.drawable.ic_launcher_background else resId
    } catch (e: Exception) {
        R.drawable.ic_launcher_background
    }
}
