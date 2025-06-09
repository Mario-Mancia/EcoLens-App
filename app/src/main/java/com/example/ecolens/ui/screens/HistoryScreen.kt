package com.example.ecolens.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.ecolens.data.local.session.SessionViewModel
import com.example.ecolens.ui.viewmodels.UserViewModel

@Composable
fun HistoryScreen(sessionViewModel: SessionViewModel,
                  userViewModel: UserViewModel,
                  modifier: Modifier = Modifier) {
    val userEmail = sessionViewModel.userEmail.value

    LaunchedEffect(userEmail) {
        userEmail?.let { userViewModel.loadUserByEmail(it) }
    }
    val user by userViewModel.user

    val userId =  user?.id ?: 0

    Column(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Pantalla de Historial",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text("Bienvenido, $userEmail")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HistoryPreview() {
    //HistoryScreen()
}