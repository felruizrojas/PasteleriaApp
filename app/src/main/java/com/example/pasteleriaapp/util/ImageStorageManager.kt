package com.example.pasteleriaapp.util

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

object ImageStorageManager {

    /**
     * Guarda una copia de la imagen referenciada por [uri] en el almacenamiento interno de la app
     * dentro de un subdirectorio [directoryName]. Devuelve la ruta absoluta del archivo creado o null
     * si ocurrió un error durante el proceso.
     */
    suspend fun saveImageFromUri(
        context: Context,
        uri: Uri,
        directoryName: String
    ): String? = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val inputStream = resolver.openInputStream(uri) ?: return@withContext null
        val extension = resolver.getType(uri)
            ?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
            ?: MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            ?: "jpg"
        val safeExtension = if (extension.isBlank()) "jpg" else extension

        val imagesDir = File(context.filesDir, "images/$directoryName")
        if (!imagesDir.exists() && !imagesDir.mkdirs()) {
            inputStream.close()
            return@withContext null
        }

        val fileName = "img_${'$'}{System.currentTimeMillis()}.$safeExtension"
        val destination = File(imagesDir, fileName)

        try {
            inputStream.use { input ->
                destination.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } catch (ioe: IOException) {
            destination.delete()
            return@withContext null
        }

        return@withContext destination.absolutePath
    }
}
