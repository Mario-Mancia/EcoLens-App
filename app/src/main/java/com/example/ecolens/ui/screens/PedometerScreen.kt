package com.example.ecolens.ui.screens

import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ecolens.data.local.session.SessionViewModel
import com.example.ecolens.ui.viewmodels.StepsViewModel
import com.example.ecolens.ui.viewmodels.UserStatsViewModel
import com.example.ecolens.ui.viewmodels.UserViewModel
import java.time.LocalDate
import com.example.ecolens.hardware.vibration.mediumVibrate

@Composable
fun PedometerScreen(
    sessionViewModel: SessionViewModel,
    userViewModel: UserViewModel,
    onStartPedometer: () -> Unit,
    onStopPedometer: () -> Unit,
    stepCount: Int,
    stepsViewModel: StepsViewModel,
    userStatsViewModel: UserStatsViewModel,
    onResetSteps: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userEmail = sessionViewModel.userEmail.value

    LaunchedEffect(userEmail) {
        userEmail?.let { userViewModel.loadUserByEmail(userEmail) }
    }

    val user by userViewModel.user
    val userName = user?.username ?: "desconocido"
    val userId = user?.id ?: 0

    var isPedometerOn by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val permission = android.Manifest.permission.ACTIVITY_RECOGNITION
    val totalSteps: Int = stepCount


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isPedometerOn = true
            onStartPedometer()
        } else {
            Toast.makeText(
                context,
                "El permiso es necesario para usar el podómetro",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pantalla de Podómetro",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Bienvenido, $userEmail")
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
            shape = RectangleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "$userName, en EcoLens fomentamos las actividades saludables y la disminución del uso de medios de transporte contaminantes, por eso, te presentamos un podómetro mediante el cual, podrás obtener Eco puntos por tus pasos.",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Activar Podómetro", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = isPedometerOn,
                onCheckedChange = { checked ->
                    if (checked) {
                        val isGranted = ContextCompat.checkSelfPermission(
                            context, permission
                        ) == PackageManager.PERMISSION_GRANTED

                        if (isGranted) {
                            isPedometerOn = true
                            onStartPedometer()
                        } else {
                            permissionLauncher.launch(permission)
                        }
                    } else {
                        isPedometerOn = false
                        onStopPedometer()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Pasos actuales: $totalSteps",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            if (userId != 0) {
                val today = LocalDate.now()
                stepsViewModel.insertOrUpdateSteps(userId, totalSteps, today)
                userStatsViewModel.addSteps(userId, totalSteps)
                mediumVibrate(context)
                Toast.makeText(
                    context,
                    "Pasos registrados exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
                onResetSteps()
            } else {
                Toast.makeText(
                    context,
                    "Error: usuario no identificado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        },
            enabled = totalSteps > 0 && !isPedometerOn
        ) {
            Text(text = "Registrar pasos")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PedometerPreview() {
    //PedometerScreen()
}