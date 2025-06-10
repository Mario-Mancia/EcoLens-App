package com.example.ecolens.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecolens.data.local.session.SessionViewModel
import com.example.ecolens.ui.viewmodels.UserStatsViewModel
import com.example.ecolens.ui.viewmodels.UserViewModel

@Composable
fun HomeScreen(sessionViewModel: SessionViewModel,
               userViewModel: UserViewModel,
               userStatsViewModel: UserStatsViewModel,
               modifier: Modifier = Modifier) {
    val userEmail = sessionViewModel.userEmail.value

    LaunchedEffect(userEmail) {
        userEmail?.let { userViewModel.loadUserByEmail(it) }
    }

    val user by userViewModel.user
    val userId = user?.id ?: 0
    val userName = user?.username ?: "desconocido"

    LaunchedEffect(userId) {
        if (userId != 0) {
            userStatsViewModel.loadStatsByUserId(userId)
        }
    }

    val stats by userStatsViewModel.stats.collectAsState()

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido, $userName",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color(0xFF026B60)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Aquí podrás ver un resumen de tus estadísticas con EcoLens:",
            style = TextStyle(fontSize = 18.sp, color = Color.DarkGray),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (stats != null) {
            StatsCard(
                title = "Reciclajes",
                description = "Has conseguido un total de ${stats!!.totalRecyclings} reciclajes.",
                icon = Icons.Default.Refresh,
                backgroundColor = Color(0xFFA5D6A7)
            )
            StatsCard(
                title = "Logros",
                description = "Has conseguido un total de ${stats!!.totalAchievements} logros.",
                icon = Icons.Default.EmojiEvents,
                backgroundColor = Color(0xFFFFF59D)
            )
            StatsCard(
                title = "Eco Puntos",
                description = "Has conseguido un total de ${stats!!.ecoPoints} eco puntos.",
                icon = Icons.Default.Star,
                backgroundColor = Color(0xFF81D4FA)
            )
            StatsCard(
                title = "Pasos dados",
                description = "Has caminado un total de ${stats!!.totalSteps} pasos.",
                icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                backgroundColor = Color(0xFFFFAB91)
            )
        } else {
            Text("Cargando estadísticas...", color = Color.Gray)
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    description: String,
    icon: ImageVector,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.Black,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp)
            )
            Column {
                Text(
                    text = title,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = description,
                    style = TextStyle(
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePreview() {
    //HomeScreen()
}