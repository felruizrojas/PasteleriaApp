package com.example.pasteleriaapp.data.remote.api

import android.net.Uri
import java.io.File
import java.util.Locale
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

suspend fun PasteleriaApiService.ensureRemoteImagePath(originalPath: String, folder: String): String {
    val trimmed = originalPath.trim()
    if (trimmed.isBlank()) return trimmed
    val lower = trimmed.lowercase(Locale.ROOT)
    if (lower.startsWith("http://") || lower.startsWith("https://")) {
        return trimmed
    }
    val looksLikeLocalFile = trimmed.startsWith("file://") || trimmed.startsWith("/")
    if (!looksLikeLocalFile) {
        return trimmed
    }

    val file = when {
        trimmed.startsWith("file://") -> Uri.parse(trimmed).path?.let { File(it) }
        else -> File(trimmed)
    } ?: return trimmed

    if (!file.exists()) {
        return trimmed
    }

    val mediaType = "image/*".toMediaTypeOrNull()
    val requestBody = file.asRequestBody(mediaType)
    val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
    val folderBody = folder.ifBlank { "general" }.toRequestBody("text/plain".toMediaType())

    val response = subirImagen(filePart, folderBody)
    return response.url.ifBlank { trimmed }
}
