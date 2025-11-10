package com.example.pasteleriaapp.ui.screen.profile

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person // <-- NUEVO IMPORT
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // <-- ¡IMPORTANTE! PARA MOSTRAR LA FOTO
import com.example.pasteleriaapp.ui.screen.auth.VoiceTextField
import com.example.pasteleriaapp.ui.screen.profile.InfoRow
import com.example.pasteleriaapp.ui.viewmodel.AuthViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EditarProfileScreen(
    authViewModel: AuthViewModel,
    onEditSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // --- LÓGICA DE CÁMARA (MODIFICADA) ---
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { newBitmap: Bitmap? ->
        if (newBitmap != null) {
            // Guardamos el bitmap usando el ViewModel
            authViewModel.guardarFotoPerfil(newBitmap, context)
        }
    }
    // --- FIN LÓGICA DE CÁMARA ---

    LaunchedEffect(Unit) {
        authViewModel.cargarDatosPerfil()
    }
    // ... (Otros LaunchedEffects sin cambios) ...
    LaunchedEffect(state.updateSuccess) { /* ... */ }
    LaunchedEffect(state.error) { /* ... */ }

    Scaffold(
        // ... (TopAppBar sin cambios) ...
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- UI DE CÁMARA (MODIFICADA) ---
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.fotoUri != null) {
                        // Usamos Coil para cargar la foto guardada
                        AsyncImage(
                            model = state.fotoUri,
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Icono por defecto
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Tomar foto",
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    if (cameraPermissionState.status.isGranted) {
                        cameraLauncher.launch()
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }) {
                    Icon(Icons.Default.CameraAlt, "Cámara", modifier = Modifier.padding(end = 8.dp))
                    Text("Cambiar Foto")
                }
                // --- FIN UI DE CÁMARA ---

                Spacer(Modifier.height(24.dp))

                // ... (InfoRow de RUN y Correo sin cambios) ...
                InfoRow(label = "RUN:", value = state.usuarioActual?.run ?: "")
                InfoRow(label = "Correo:", value = state.usuarioActual?.correo ?: "")
                Spacer(Modifier.height(16.dp))

                // ... (Todos los VoiceTextField sin cambios) ...
                VoiceTextField(value = state.profNombre, onValueChange = authViewModel::onProfNombreChange, label = "Nombre", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                VoiceTextField(value = state.profApellidos, onValueChange = authViewModel::onProfApellidosChange, label = "Apellidos", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                VoiceTextField(value = state.profRegion, onValueChange = authViewModel::onProfRegionChange, label = "Región", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                VoiceTextField(value = state.profComuna, onValueChange = authViewModel::onProfComunaChange, label = "Comuna", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                VoiceTextField(value = state.profDireccion, onValueChange = authViewModel::onProfDireccionChange, label = "Dirección", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(24.dp))

                // ... (Botón Guardar Cambios y CircularProgressIndicator sin cambios) ...
                Button(onClick = { authViewModel.guardarCambiosPerfil() }, modifier = Modifier.fillMaxWidth(), enabled = !state.isLoading) {
                    Text("Guardar Cambios")
                }
            }
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}