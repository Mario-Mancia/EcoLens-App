package com.example.ecolens.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecolens.R
import com.example.ecolens.data.local.entities.UserAchievementsEntity
import com.example.ecolens.data.local.session.SessionManager
import com.example.ecolens.data.local.session.SessionViewModel
import com.example.ecolens.ui.viewmodels.AchievementsViewModel
import com.example.ecolens.ui.viewmodels.RecyclingViewModel
import com.example.ecolens.ui.viewmodels.UserAchievementsViewModel
import com.example.ecolens.ui.viewmodels.UserStatsViewModel
import com.example.ecolens.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.ecolens.hardware.vibration.*
import com.example.ecolens.ui.viewmodels.QrScanViewModel

//Función Composable para estructurar la pantalla de perfil de ususario
@Composable
fun ProfileScreen(
    navController: NavHostController,
    sessionViewModel: SessionViewModel,
    userViewModel: UserViewModel,
    recyclingViewModel: RecyclingViewModel,
    userStatsViewModel: UserStatsViewModel,
    achievementsViewModel: AchievementsViewModel,
    userAchievementsViewModel: UserAchievementsViewModel,
    qrScanViewModel: QrScanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val coroutineScope = rememberCoroutineScope()

    val userEmail = sessionViewModel.userEmail.value
    val user by userViewModel.user
    val stats by userStatsViewModel.stats.collectAsState()
    val userAchievements by userAchievementsViewModel.userAchievements.collectAsState()
    val allAchievements by achievementsViewModel.achievements.collectAsState()

    val navbarColor = Brush.horizontalGradient(
        listOf(Color(0xFF409D44), Color(0xFF037A6F), Color(0xFF026B60))
    )

    val previousAchievements = remember { mutableStateOf<List<Int>>(emptyList()) }

    LaunchedEffect(userEmail) {
        userEmail?.let { userViewModel.loadUserByEmail(it) }
    }

    LaunchedEffect(user) {
        user?.let {
            userStatsViewModel.loadStatsByUserId(it.id)
            userAchievementsViewModel.loadUserAchievements(it.id)
            qrScanViewModel.loadUniqueScanCount(it.id)
        }
    }
    val userId = user?.id
    LaunchedEffect(Unit) {
        achievementsViewModel.loadAchievements()
    }
    val qrScanCount by qrScanViewModel.uniqueScanCount.collectAsState()

    val unlockedAchievements by userAchievementsViewModel.userAchievements.collectAsState()

    LaunchedEffect(user, stats, qrScanCount, unlockedAchievements) {
        if (user != null && stats != null) {
            val userId = user!!.id
            val unlocked = unlockedAchievements.map { it.achievementId }.toSet()
            val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val checks = listOf(
                Triple(1, stats!!.totalRecyclings >= 1, "Reciclador principiante"),
                Triple(4, stats!!.totalSteps >= 1000, "Caminante urbano"),
                Triple(5, stats!!.ecoPoints >= 500, "Activista del reciclaje"),
                Triple(6, qrScanCount >= 1, "Explorador sostenible"),
                Triple(7, qrScanCount >= 5, "Consumidor consciente"),
                Triple(10, stats!!.totalSteps >= 1, "Primer paso Eco")
            )

            checks.forEach { (achievementId, condition, _) ->
                if (condition) {
                    val ua = UserAchievementsEntity(userId, achievementId, now)
                    userAchievementsViewModel.insertUserAchievementIfNotExists(ua, unlocked)
                }
            }
        }
    }


    val ecoPoints = stats?.ecoPoints ?: 0
    val totalSteps = stats?.totalSteps ?: 0
    val totalRecyclings = stats?.totalRecyclings ?: 0

    var showUsernameDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    Column(modifier.fillMaxSize()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(brush = navbarColor)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Icono para retroceder",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier.width(64.dp))

                    Text(
                        text = stringResource(id = R.string.profileScreenTitle),
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Tarjeta de perfil
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xCC71FA8A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Foto de perfil",
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = user?.username ?: "Cargando...",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF026B60)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow(label = "Nacionalidad", value = user?.country ?: "Desconocida")
                    InfoRow(label = "Email", value = user?.email ?: "Desconocido")
                    InfoRow(label = "Género", value = user?.gender ?: "Desconocido")

                    InfoRowWithEdit(
                        label = "Nombre de usuario",
                        value = user?.username ?: "",
                        onEditClick = { showUsernameDialog = true }
                    )

                    InfoRowWithEdit(
                        label = "Contraseña",
                        value = "••••••••",
                        onEditClick = { showPasswordDialog = true }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // SECCIÓN DE LOGROS
            Text(
                text = "Mis logros",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                color = Color(0xFF026B60)
            )

            if (userAchievements.isEmpty()) {
                Text(
                    text = "Aún no has conseguido logros.",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray,
                    fontSize = 18.sp
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    userAchievements.forEach { ua ->
                        val achievement = allAchievements.find { it.id == ua.achievementId }
                        achievement?.let {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = getAchievementColor(it.type)),
                                elevation = CardDefaults.cardElevation(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = getAchievementIcon(it.type),
                                        contentDescription = null,
                                        tint = Color(0xFF026B60),
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(it.name, fontWeight = FontWeight.Bold)
                                        Text(it.description ?: "", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // BOTÓN CERRAR SESIÓN
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Icono de Logout",
                    tint = Color(0xFFF56D82),
                    modifier = Modifier.size(36.dp)
                )
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            sessionManager.logout()
                            navController.navigate("Start") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    }
                ) {
                    Text(
                        text = "Cerrar sesión",
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 26.sp,
                            color = Color(0xFFF56D82)
                        )
                    )
                }
            }
        }

    }
    if (showUsernameDialog && user != null) {
        var newUsername by remember { mutableStateOf(user!!.username) }

        AlertDialog(
            onDismissRequest = { showUsernameDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    userViewModel.updateUsername(user!!.id, newUsername)
                    showUsernameDialog = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUsernameDialog = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Editar nombre de usuario") },
            text = {
                Column {
                    Text("Introduce un nuevo nombre de usuario:")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newUsername,
                        onValueChange = { newUsername = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
    if (showPasswordDialog && user != null) {
        var currentPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        val success = userViewModel.updatePassword(user!!.id, currentPassword, newPassword)
                        if (success) {
                            showPasswordDialog = false
                            Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                        } else {
                            error = "Contraseña actual incorrecta"
                        }
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Cambiar contraseña") },
            text = {
                Column {
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Contraseña actual") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Nueva contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    error?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = Color.Red, fontSize = 12.sp)
                    }
                }
            }
        )
    }
}


@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Text(
                text = "$label:",
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = value,
                color = Color.Black
            )
        }
    }
}

@Composable
fun InfoRowWithEdit(label: String, value: String, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "$label:",
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = value,
                color = Color.Black
            )
        }
        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Editar $label",
                tint = Color(0xFF00796B)
            )
        }
    }
}

fun getAchievementColor(type: String): Color {
    return when (type) {
        "reciclaje" -> Color(0xFFC8E6C9) // RecyclingGreen
        "pasos" -> Color(0xFFBBDEFB)     // StepBlue
        "qr" -> Color(0xFFFFF9C4)        // QRYellow
        else -> Color(0xFFE0E0E0)        // Gris claro para 'otros'
    }
}
fun getAchievementIcon(type: String): ImageVector {
    return when (type) {
        "reciclaje" -> Icons.Default.Recycling
        "pasos" -> Icons.AutoMirrored.Filled.DirectionsWalk
        "qr" -> Icons.Default.QrCodeScanner
        else -> Icons.Default.EmojiEvents
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfilePreview() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val sessionM = remember { SessionManager(context) }
    val sessionV = remember { SessionViewModel(sessionM)}
    //ProfileScreen(navController, sessionV)
}