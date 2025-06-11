/**
 *  DESARROLLO DE APLICACIONES MÓVILES I | Parcial 3.
 *  Proyecto: EcoLens
 *  Desarrollado por:
 *  - Jacquelinne Esmeralda Hernández Melgar.
 *  - Mario Roberto Mancía Peraza.
 */

package com.example.ecolens

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ecolens.data.local.database.DatabaseInstance
import com.example.ecolens.data.local.session.SessionManager
import com.example.ecolens.data.local.session.SessionViewModel
import com.example.ecolens.data.local.session.SessionViewModelFactory
import com.example.ecolens.ui.theme.EcoLensTheme
import com.example.ecolens.navigation.AppNavHost
import com.example.ecolens.ui.components.BottomNavBar
import com.example.ecolens.ui.components.TopNavBar
import com.example.ecolens.ui.viewmodels.RegisterViewModel
import com.example.ecolens.ui.viewmodels.RegisterViewModelFactory
import com.example.ecolens.ui.viewmodels.UserStatsViewModel
import com.example.ecolens.ui.viewmodels.UserStatsViewModelFactory
import com.example.ecolens.ui.viewmodels.UserViewModel
import com.example.ecolens.ui.viewmodels.UserViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.ecolens.hardware.stepsensor.StepForegroundService
import com.example.ecolens.hardware.vibration.*
import com.example.ecolens.ui.viewmodels.AchievementsViewModel
import com.example.ecolens.ui.viewmodels.AchievementsViewModelFactory
import com.example.ecolens.ui.viewmodels.QrScanViewModel
import com.example.ecolens.ui.viewmodels.QrScanViewModelFactory
import com.example.ecolens.ui.viewmodels.RecyclingViewModel
import com.example.ecolens.ui.viewmodels.RecyclingViewModelFactory
import com.example.ecolens.ui.viewmodels.StepsViewModel
import com.example.ecolens.ui.viewmodels.StepsViewModelFactory
import com.example.ecolens.ui.viewmodels.UserAchievementsViewModel
import com.example.ecolens.ui.viewmodels.UserAchievementsViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var sessionViewModel: SessionViewModel

    private fun startPedometerService() {
        val intent = Intent(this, StepForegroundService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun stopPedometerService() {
        val intent = Intent(this, StepForegroundService::class.java)
        stopService(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Esta parte permite que las pantallas puedan cargar los datos desde el "localstorage"
        val sessionManager = SessionManager(applicationContext)
        val factory = SessionViewModelFactory(sessionManager)
        sessionViewModel = ViewModelProvider(this, factory)[SessionViewModel::class.java]

        val db = DatabaseInstance.getDatabase(applicationContext)
        val dao = db.stepsDao()

        CoroutineScope(Dispatchers.IO).launch {
            dao.getAll()
            delay(1000)
        }

        setContent {
            EcoLensTheme {
                MainScreen(sessionViewModel,
                        onStartPedometer = { startPedometerService() },
                    onStopPedometer = { stopPedometerService() }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    sessionViewModel: SessionViewModel,
    onStartPedometer: () -> Unit,
    onStopPedometer: () -> Unit,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf("home", "pedometer", "camera", "qrscanner", "history")
    val showBottomBar = currentRoute in bottomBarRoutes
    val showTopBar = currentRoute in bottomBarRoutes
    val context = LocalContext.current

    if (currentRoute in bottomBarRoutes) {
        val activity = context as? Activity
        var backPressedTime by remember { mutableStateOf(0L) }

        BackHandler {
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressedTime < 2000) {
                activity?.finish()
            } else {
                backPressedTime = currentTime
                shortVibrate(context)
                Toast.makeText(context, "Presiona de nuevo para salir", Toast.LENGTH_SHORT).show()
            }
        }
    }


    val db = remember { DatabaseInstance.getDatabase(context) }
    val userDao = remember { db.userDao() }
    val userStatsDao = remember { db.userStatsDao() }
    val recyclingDao = remember { db.recyclingDao() }
    val stepsDao = remember { db.stepsDao() }
    val achievementsDao = remember { db.archievementsDao() }
    val userAchievementsDao = remember { db.userArchievementsDao() }
    val qrScanDao = remember { db.qrScanDao() }

    val registerViewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(userDao)
    )
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userDao)
    )
    val userStatsViewModel: UserStatsViewModel = viewModel(
        factory = UserStatsViewModelFactory(userStatsDao)
    )
    val recyclingViewModel: RecyclingViewModel = viewModel(
        factory = RecyclingViewModelFactory(recyclingDao)
    )
    val stepsViewModel: StepsViewModel = viewModel(
        factory = StepsViewModelFactory(stepsDao)
    )
    val achievementsViewModel: AchievementsViewModel = viewModel(
        factory = AchievementsViewModelFactory(achievementsDao)
    )
    val userAchievementsViewModel: UserAchievementsViewModel = viewModel(
        factory = UserAchievementsViewModelFactory(userAchievementsDao)
    )
    val qrScanViewModel: QrScanViewModel = viewModel(
        factory = QrScanViewModelFactory(qrScanDao)
    )
    //pedometer:
    //var stepCount by remember { mutableStateOf(0) }
    val stepCountState = remember { mutableStateOf(0) }
    // Escuchar broadcasts del servicio
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action == "com.example.ecolens.STEP_UPDATE") {
                    val steps = intent.getIntExtra("stepCount", 0)
                    stepCountState.value = steps
                }
            }
        }

        val filter = IntentFilter("com.example.ecolens.STEP_UPDATE")
        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopNavBar(navController = navController)
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = if (showBottomBar && showTopBar) Modifier.padding(innerPadding) else Modifier
        ) {
            AppNavHost(
                navController = navController,
                startDestination = "launch",
                sessionViewModel,
                registerViewModel,
                userViewModel,
                userStatsViewModel,
                recyclingViewModel,
                onStartPedometer = onStartPedometer,
                onStopPedometer = onStopPedometer,
                stepCount = stepCountState.value,
                onResetSteps = { stepCountState.value = 0 },
                stepsViewModel,
                qrScanViewModel
            )
        }
    }
}
