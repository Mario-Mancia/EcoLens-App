package com.example.ecolens.ui.screens

import androidx.compose.ui.tooling.preview.Preview
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.ecolens.data.local.entities.RecyclingEntity
import com.example.ecolens.ui.viewmodels.RecyclingViewModel
import com.example.ecolens.data.local.session.SessionViewModel
import com.example.ecolens.ui.viewmodels.UserStatsViewModel
import com.example.ecolens.ui.viewmodels.UserViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import com.example.ecolens.hardware.vibration.mediumVibrate

@Composable
fun CamScreen(
    sessionViewModel: SessionViewModel,
    userViewModel: UserViewModel,
    recyclingViewModel: RecyclingViewModel,
    userStatsViewModel: UserStatsViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val userEmail = sessionViewModel.userEmail.value
    LaunchedEffect(userEmail) {
        userEmail?.let { userViewModel.loadUserByEmail(it) }
    }

    val user by userViewModel.user
    val userId = user?.id ?: 0

    var imageUriString by rememberSaveable { mutableStateOf<String?>(null) }
    val imageUri: Uri? = imageUriString?.let { Uri.parse(it) }

    var showForm by rememberSaveable { mutableStateOf(false) }

    var productType by rememberSaveable { mutableStateOf<String?>(null) }
    var quantity by rememberSaveable { mutableStateOf(1) }
    val productOptions = listOf("Orgánico", "Plástico", "Papel o Cartón", "Vidrio", "Metal", "Otro")
    var expanded by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        Log.d("CamScreen", "Camera result: success=$success, uri=$imageUri")
        if (success && imageUri != null) {
            showForm = true
        } else {
            Log.d("CamScreen", "Image capture failed or cancelled.")
        }
    }

    fun launchCameraIntent() {
        val photoFile = createImageFile(context)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            photoFile
        )
        Log.d("CamScreen", "launchCameraIntent: File created at ${photoFile.absolutePath}")
        Log.d("CamScreen", "launchCameraIntent: Launching camera with URI $uri")
        imageUriString = uri.toString()
        showForm = false
        productType = null
        quantity = 1
        cameraLauncher.launch(uri)
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Añadir reciclaje",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (imageUri == null || !showForm) {
            Button(onClick = { launchCameraIntent() }) {
                Text("Capturar imagen")
            }
        }

        imageUri?.let { uri ->
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Vista previa",
                modifier = Modifier
                    .size(250.dp)
                    .border(2.dp, Color.Gray, RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }

        if (showForm && imageUri != null) {
            Spacer(modifier = Modifier.height(24.dp))

            Box {
                Text(
                    text = productType ?: "Seleccionar tipo de producto",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                        .background(Color.LightGray)
                        .padding(12.dp)
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    productOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                productType = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { if (quantity > 1) quantity-- }) { Text("-") }
                Text(
                    text = quantity.toString(),
                    modifier = Modifier.padding(horizontal = 24.dp),
                    fontSize = 20.sp
                )
                Button(onClick = {
                    if (quantity < 5) {
                        quantity++
                    } else {
                        Toast.makeText(
                            context,
                            "Solo puedes añadir 5 productos a la vez",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) { Text("+") }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (productType != null && quantity > 0) {
                            // Insertar reciclaje
                            recyclingViewModel.insertRecycling(
                                RecyclingEntity(
                                    userId = userId,
                                    productType = productType!!,
                                    quantity = quantity
                                )
                            )

                            Toast.makeText(
                                context,
                                "Producto reciclado con éxito",
                                Toast.LENGTH_SHORT
                            ).show()
                            mediumVibrate(context)

                            // Actualizar stats
                            userStatsViewModel.incrementRecyclings(userId, quantity)

                            val ecoPoints = when (productType) {
                                "Papel" -> 1 * quantity
                                "Orgánico" -> 3
                                "Plástico" -> 5 * quantity
                                "Vidrio" -> 7 * quantity
                                "Metal" -> 10 * quantity
                                else -> 0
                            }
                            userStatsViewModel.addEcoPoints(userId, ecoPoints)
                            navController.navigate("history")
                            imageUriString = null
                            showForm = false
                            productType = null
                            quantity = 1
                        }
                    },
                    enabled = productType != null && quantity > 0
                ) {
                    Text("Guardar")
                }

                Button(
                    onClick = {
                        imageUriString = null
                        showForm = false
                        productType = null
                        quantity = 1
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        }
    }
}

fun createImageFile(context: Context): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CamPreview() {
    //CamScreen()
}