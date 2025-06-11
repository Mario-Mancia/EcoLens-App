package com.example.ecolens.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecolens.data.local.session.SessionViewModel
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
import com.example.ecolens.ui.viewmodels.QrScanViewModel
import com.example.ecolens.ui.viewmodels.RecyclingViewModel
import com.example.ecolens.ui.viewmodels.RegisterViewModel
import com.example.ecolens.ui.viewmodels.StepsViewModel
import com.example.ecolens.ui.viewmodels.UserStatsViewModel
import com.example.ecolens.ui.viewmodels.UserViewModel


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "launch",
    sessionViewModel: SessionViewModel,
    registerViewModel: RegisterViewModel,
    userViewModel: UserViewModel,
    userStatsViewModel: UserStatsViewModel,
    recyclingViewModel: RecyclingViewModel,
    onStartPedometer: () -> Unit,
    onStopPedometer: () -> Unit,
    stepCount: Int,
    onResetSteps: () -> Unit,
    stepsViewModel: StepsViewModel,
    qrScanViewModel: QrScanViewModel
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
            LoginScreen(navController, sessionViewModel)
        }
        composable("Register") {
            RegisterScreen(navController, registerViewModel)
        }

        //Pantallas privadas

        composable("home") {
            HomeScreen(sessionViewModel, userViewModel, userStatsViewModel)
        }
        composable("pedometer") {
            PedometerScreen(
                sessionViewModel,
                userViewModel,
                onStartPedometer = onStartPedometer,
                onStopPedometer = onStopPedometer,
                stepCount = stepCount,
                stepsViewModel,
                userStatsViewModel,
                onResetSteps = onResetSteps
            )
        }
        composable("camera") {
            CamScreen(
                sessionViewModel,
                userViewModel,
                recyclingViewModel,
                userStatsViewModel,
                navController
            )
        }
        composable("qrscanner") {
            ScanScreen(sessionViewModel, userViewModel, qrScanViewModel, userStatsViewModel)
        }
        composable("history") {
            HistoryScreen(sessionViewModel, userViewModel, recyclingViewModel)
        }
        composable("profile") {
            ProfileScreen(navController, sessionViewModel, userViewModel)
        }
        composable("launch") {
            LaunchScreen(navController)
        }
    }
}