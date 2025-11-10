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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
// --- IMPORTS IMPORTANTES ---
import com.example.pasteleriaapp.ui.screen.auth.VoiceTextField // <-- Importa el campo de voz
import com.example.pasteleriaapp.ui.screen.profile.InfoRow // <-- Importa el InfoRow
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

    // --- INICIO RECURSO 2: CÁMARA ---

    // 1. Estado para guardar la foto tomada (temporalmente)
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 2. Lógica de Permiso de Cámara
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    // 3. Lógica del Lanzador de Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { newBitmap: Bitmap? ->
        if (newBitmap != null) {
            bitmap = newBitmap
            // TODO: Aquí guardaríamos la URI de la imagen en el AuthViewModel
            // Por ahora, solo la mostramos.
        }
    }
    // --- FIN RECURSO 2: CÁMARA ---

    // ... (LaunchedEffects de Carga, Éxito y Error) ...
    LaunchedEffect(Unit) {
        authViewModel.cargarDatosPerfil()
    }
    LaunchedEffect(state.updateSuccess) {
        if (state.updateSuccess) {
            Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
            authViewModel.resetNavegacion()
            onEditSuccess()
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            authViewModel.resetNavegacion()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- INICIO UI DE CÁMARA ---
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap!!.asImageBitmap(),
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Tomar foto",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    // Pedimos permiso para la CÁMARA
                    if (cameraPermissionState.status.isGranted) {
                        cameraLauncher.launch()
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }) {
                    Text("Tomar Foto de Perfil")
                }
                // --- FIN UI DE CÁMARA ---

                Spacer(Modifier.height(24.dp))

                // Campos no editables (Usan el InfoRow de ProfileScreen.kt)
                InfoRow(label = "RUN:", value = state.usuarioActual?.run ?: "")
                InfoRow(label = "Correo:", value = state.usuarioActual?.correo ?: "")
                Spacer(Modifier.height(16.dp))

                // --- CAMPOS EDITABLES (CON VOZ) ---

                VoiceTextField(
                    value = state.profNombre,
                    onValueChange = authViewModel::onProfNombreChange,
                    label = "Nombre",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.profApellidos,
                    onValueChange = authViewModel::onProfApellidosChange,
                    label = "Apellidos",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.profRegion,
                    onValueChange = authViewModel::onProfRegionChange,
                    label = "Región",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.profComuna,
                    onValueChange = authViewModel::onProfComunaChange,
                    label = "Comuna",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                VoiceTextField(
                    value = state.profDireccion,
                    onValueChange = authViewModel::onProfDireccionChange,
                    label = "Dirección",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { authViewModel.guardarCambiosPerfil() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    Text("Guardar Cambios")
                }
            }
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}