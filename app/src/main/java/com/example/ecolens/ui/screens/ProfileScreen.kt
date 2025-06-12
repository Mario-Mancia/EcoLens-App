package com.example.ecolens.ui.screens

import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecolens.R
import com.example.ecolens.data.local.session.SessionManager
import com.example.ecolens.data.local.session.SessionViewModel
import com.example.ecolens.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch

//Función Composable para estructurar la pantalla de perfil de ususario
@Composable
fun ProfileScreen(navController: NavHostController,
                  sessionViewModel: SessionViewModel,
                  userViewModel: UserViewModel,
                  modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val coroutineScope = rememberCoroutineScope()
    val userEmail = sessionViewModel.userEmail.value
    val user by userViewModel.user
    val navbarColor = Brush.horizontalGradient(
        listOf(
            Color(0xFF409D44),
            Color(0xFF037A6F),
            Color(0xFF026B60)
        )
    )

    LaunchedEffect(userEmail) {
        userEmail?.let { userViewModel.loadUserByEmail(it) }
    }

    Column(
        modifier.fillMaxSize()
    ) {
        Box(
            modifier
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = {
                            navController.navigate("home")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Icono para retroceder",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier.width(64.dp))

                    Text(
                        text = (stringResource(id = R.string.profileScreenTitle)),
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
            modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xCC71FA8A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    // Avatar
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
                        onEditClick = {
                            // Acción para editar nombre (lo puedes implementar luego)
                        }
                    )

                    InfoRowWithEdit(
                        label = "Contraseña",
                        value = "••••••••",
                        onEditClick = {
                            // Acción para editar contraseña
                        }
                    )
                }
            }

            // Cerrar sesión
            Row(
                modifier.fillMaxWidth().padding(20.dp),
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfilePreview() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val sessionM = remember { SessionManager(context) }
    val sessionV = remember { SessionViewModel(sessionM)}
    //ProfileScreen(navController, sessionV)
}