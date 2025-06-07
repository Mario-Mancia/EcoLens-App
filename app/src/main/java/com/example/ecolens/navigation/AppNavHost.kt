package com.example.ecolens.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecolens.ui.screens.StartScreen
import com.example.ecolens.ui.screens.LoginScreen
import com.example.ecolens.ui.screens.RegisterScreen
import com.example.ecolens.ui.screens.HomeScreen
import com.example.ecolens.ui.screens.PedometerScreen
import com.example.ecolens.ui.screens.CamScreen
import com.example.ecolens.ui.screens.ScanScreen
import com.example.ecolens.ui.screens.HistoryScreen
import com.example.ecolens.ui.screens.ProfileScreen
import com.example.ecolens.ui.screens.LaunchScreen


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "launch"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        //Pantallas públicas de arranque
        composable("Start") {
            StartScreen(navController)
        }
        composable("Login") {
            LoginScreen(navController)
        }
        composable("Register") {
            RegisterScreen(navController)
        }

        //Pantallas privadas

        composable("home") {
            HomeScreen()
        }
        composable("pedometer") {
            PedometerScreen()
        }
        composable("camera") {
            CamScreen()
        }
        composable("qrscanner") {
            ScanScreen()
        }
        composable("history") {
            HistoryScreen()
        }
        composable("profile") {
            ProfileScreen(navController)
        }
        composable("launch") {
            LaunchScreen(navController)
        }
    }
}