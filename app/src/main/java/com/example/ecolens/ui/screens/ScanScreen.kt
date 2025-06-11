package com.example.ecolens.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ecolens.data.local.entities.QrScanEntity
import com.example.ecolens.data.local.session.SessionViewModel
import com.example.ecolens.hardware.camera.QrCodeAnalyzer
import com.example.ecolens.ui.viewmodels.QrScanViewModel
import com.example.ecolens.ui.viewmodels.UserStatsViewModel
import com.example.ecolens.ui.viewmodels.UserViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class QrContent(
    val code: String,
    val title: String,
    val description: String,
    val isPlace: Boolean
)

val predefinedQrCodes = listOf(
    QrContent("QR_PROD_01", "Producto Sostenible", "Cepillo de dientes de bambú", false),
    QrContent("QR_PROD_02", "Producto Sostenible", "Bolsa reutilizable de algodón", false),
    QrContent("QR_PROD_03", "Producto Sostenible", "Botella de acero inoxidable", false),
    QrContent("QR_PROD_04", "Producto Sostenible", "Shampoo sólido ecológico", false),
    QrContent("QR_PROD_05", "Producto Sostenible", "Compostera doméstica", false),
    QrContent("QR_PLACE_01", "Lugar Sostenible", "Punto de reciclaje en Plaza Verde", true),
    QrContent("QR_PLACE_02", "Lugar Sostenible", "Tienda ecológica EcoVida", true),
    QrContent("QR_PLACE_03", "Lugar Sostenible", "Cafetería Cero Desperdicio", true),
    QrContent("QR_PLACE_04", "Lugar Sostenible", "Mercado de productos orgánicos", true),
    QrContent("QR_PLACE_05", "Lugar Sostenible", "Estación de carga solar", true)
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    sessionViewModel: SessionViewModel,
    userViewModel: UserViewModel,
    qrScanViewModel: QrScanViewModel,
    userStatsViewModel: UserStatsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var qrText by remember { mutableStateOf("") }
    var matchedQr by remember { mutableStateOf<QrContent?>(null) }
    val userEmail = sessionViewModel.userEmail.value

    LaunchedEffect(userEmail) {
        userEmail?.let { userViewModel.loadUserByEmail(it) }
    }
    val user by userViewModel.user

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }

    LaunchedEffect(qrText) {
        matchedQr = predefinedQrCodes.find { it.code == qrText }
    }

    val scans by qrScanViewModel.scans.collectAsState(emptyList())

    // Cargar historial al entrar
    LaunchedEffect(user) {
        user?.id?.let { qrScanViewModel.loadScansByUser(it) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Escanear productos o lugares", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Escanea códigos válidos y gana Puntos Eco", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
        ) {
            if (cameraPermissionState.status.isGranted) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val preview = Preview.Builder().build().apply {
                            setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val analysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build().apply {
                                setAnalyzer(ContextCompat.getMainExecutor(ctx)) { image ->
                                    QrCodeAnalyzer { scanned ->
                                        qrText = scanned
                                    }.analyze(image)
                                }
                            }
                        val selector = CameraSelector.DEFAULT_BACK_CAMERA
                        val provider = cameraProviderFuture.get()
                        provider.unbindAll()
                        provider.bindToLifecycle(lifecycleOwner, selector, preview, analysis)
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Permiso de cámara no concedido", color = Color.White, modifier = Modifier.align(Alignment.Center))
            }
        }

        Spacer(Modifier.height(16.dp))

        matchedQr?.let { qr ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFB2DFDB)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(qr.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(qr.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val userId = user?.id ?: return@Button
                matchedQr?.let { qr ->
                    val scan = QrScanEntity(
                        userId = userId,
                        scanTitle = qr.title,
                        content = qr.description,
                        datetime = System.currentTimeMillis()
                    )
                    qrScanViewModel.insertScanIfNotExists(
                        scan,
                        onSuccess = {
                            val points = if (qr.isPlace) 10 else 5
                            userStatsViewModel.addEcoPoints(userId, points)
                            Toast.makeText(context, "Código registrado con éxito", Toast.LENGTH_SHORT).show()
                            qrText = ""
                            matchedQr = null
                        },
                        onDuplicate = {
                            Toast.makeText(context, "Este código ya fue escaneado", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            enabled = matchedQr != null
        ) {
            Text("Guardar")
        }

        Spacer(Modifier.height(24.dp))

        Text("Escaneos anteriores", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(scans) { scan ->
                QrScanCard(scan)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}


@Composable
fun QrScanCard(scan: QrScanEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFA5D6A7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = scan.scanTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(scan.datetime)),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = scan.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}

