package com.example.ecolens.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home: Screen("home", "Inicio", Icons.Filled.Home)
    object Pedometer: Screen("pedometer", "Pasos", Icons.Filled.DirectionsRun)
    object Camera: Screen("camera", "Reciclar", Icons.Filled.AddCircle)
    object QRScanner: Screen("qrscanner", "QR+", Icons.Filled.QrCode)
    object History: Screen("history", "Historial", Icons.Filled.History)
}