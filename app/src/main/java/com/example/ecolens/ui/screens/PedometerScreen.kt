package com.example.ecolens.ui.screens

import android.app.Activity
import android.content.Context
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
import android.app.ActivityManager
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.style.TextAlign
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
    LaunchedEffect(Unit) {
        isPedometerOn = isStepServiceRunning(context)
    }
    val permission = android.Manifest.permission.ACTIVITY_RECOGNITION
    val totalSteps: Int = stepCount

    val today = LocalDate.now()

    LaunchedEffect(userId) {
        if (userId != 0) {
            stepsViewModel.loadTodaySteps(userId, today)
        }
    }
    val todaySteps by stepsViewModel.todaySteps.collectAsState()

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
            text = "Contador de pasos",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 42.sp,
                color = Color(0xFF026B60),
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RectangleShape,
            colors = CardDefaults.cardColors(containerColor = Color(0xFF098E0F)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                            contentDescription = null,
                            tint = Color(0xFF026B60),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "$userName, en EcoLens fomentamos las actividades saludables y la disminución del uso de medios de transporte contaminantes, por eso, te presentamos un podómetro mediante el cual, podrás obtener Eco puntos por tus pasos.",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isPedometerOn) "Podómetro activado" else "Podómetro desactivado",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                color = Color(0xFF026B60)
            )
            Spacer(modifier.width(28.dp))
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
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF026B60),
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color(0xFFBDBDBD)
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                if (isPedometerOn) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                        contentDescription = "Contando pasos",
                        tint = Color(0xFF026B60),
                        modifier = Modifier
                            .size(28.dp)
                            .padding(end = 8.dp)
                    )
                }
                Text(
                    text = "Pasos actuales: $totalSteps",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color(0xFF026B60),
                        fontSize = 24.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (userId != 0) {
                        val ecoPoints = totalSteps / 100
                        stepsViewModel.insertOrUpdateSteps(userId, totalSteps, today)
                        userStatsViewModel.addSteps(userId, totalSteps)

                        if (ecoPoints > 0) {
                            userStatsViewModel.addEcoPoints(userId, ecoPoints)
                        }

                        mediumVibrate(context)

                        Toast.makeText(
                            context,
                            "Pasos registrados exitosamente" + if (ecoPoints > 0) " (+$ecoPoints EcoPuntos)" else "",
                            Toast.LENGTH_SHORT
                        ).show()

                        onResetSteps()
                        stepsViewModel.loadTodaySteps(userId, today)
                    } else {
                        Toast.makeText(
                            context,
                            "Error: usuario no identificado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                enabled = totalSteps > 0 && !isPedometerOn,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF026B60)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(36.dp)
            ) {
                Text(
                    text = "Registrar pasos",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Registrar",
                    tint = Color.White
                )
            }
        }
        /*
        Text(
            text = "Pasos actuales: $totalSteps",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            if (userId != 0) {
                val ecoPoints = totalSteps / 100
                stepsViewModel.insertOrUpdateSteps(userId, totalSteps, today)
                userStatsViewModel.addSteps(userId, totalSteps)

                if (ecoPoints > 0) {
                    userStatsViewModel.addEcoPoints(userId, ecoPoints)
                }

                mediumVibrate(context)

                Toast.makeText(
                    context,
                    "Pasos registrados exitosamente" + if (ecoPoints > 0) " (+$ecoPoints EcoPuntos)" else "",
                    Toast.LENGTH_SHORT
                ).show()

                onResetSteps()
                stepsViewModel.loadTodaySteps(userId, today) // refrescar después de registrar
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
        }*/
        /*
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Pasos registrados hoy: ${todaySteps?: 0}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )*/
    }
}

//Esta función detecta si el servicio está activo para que el switch está apagado, se encienda automáticamente
fun isStepServiceRunning(context: Context): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    activityManager?.let {
        for (service in it.getRunningServices(Int.MAX_VALUE)) {
            if (service.service.className == "com.example.ecolens.hardware.stepsensor.StepForegroundService") {
                return true
            }
        }
    }
    return false
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PedometerPreview() {
    //PedometerScreen()
}