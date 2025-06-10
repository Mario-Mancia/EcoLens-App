package com.example.ecolens.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.ecolens.data.local.entities.RecyclingEntity
import com.example.ecolens.data.local.session.SessionViewModel
import com.example.ecolens.ui.viewmodels.RecyclingViewModel
import com.example.ecolens.ui.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(sessionViewModel: SessionViewModel,
                  userViewModel: UserViewModel,
                  recyclingViewModel: RecyclingViewModel,
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
            recyclingViewModel.loadRecyclingHistory(userId)
        }
    }

    val recyclingHistory by recyclingViewModel.recyclingHistory.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Historial de Reciclajes",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = Color(0xFF026B60)
                ),
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Text(
                text = "$userName, aquí puedes ver tus últimos 15 registros, " +
                        "si quieres ver el total de tus contribuciones puedes hacerlo en tu inicio:",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = Color.DarkGray
                ),
                modifier = Modifier.padding(vertical = 12.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recyclingHistory) { record ->
                    RecyclingCard(record)
                }
            }
        }
    }
}

@Composable
fun RecyclingCard(recycling: RecyclingEntity) {
    val backgroundColor = getColorForProductType(recycling.productType)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
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
                    text = recycling.productType,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(recycling.datetime)),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                DatoChip(
                    icon = Icons.Default.Check,
                    label = "Cantidad: ${recycling.quantity}",
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DatoChip(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.2f),
        modifier = modifier.height(64.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = color
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontSize = 12.sp,
                maxLines = 1,
                color = color
            )
        }
    }
}

fun getColorForProductType(productType: String): Color {
    return when (productType.lowercase()) {
        "plástico" -> Color(0xFFFFEB3B)
        "papel", "cartón" -> Color(0xFF38A2F6)
        "vidrio" -> Color(0xFF4CAF50)
        "orgánicos" -> Color(0xFFF3744C)
        "metales" -> Color(0xFF9E9E9E)
        else -> Color(0xFFBDBDBD)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HistoryPreview() {
    //HistoryScreen()
}