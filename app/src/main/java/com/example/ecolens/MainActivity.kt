/**
 *  DESARROLLO DE APLICACIONES MÓVILES I | Parcial 3.
 *  Proyecto: EcoLens
 *  Desarrollado por:
 *  - Jacquelinne Esmeralda Hernández Melgar.
 *  - Mario Roberto Mancía Peraza.
 */

package com.example.ecolens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ecolens.ui.screens.StartScreen
import com.example.ecolens.ui.screens.LoginScreen
import com.example.ecolens.ui.screens.RegisterScreen
import com.example.ecolens.ui.components.BottomNavBar
import com.example.ecolens.ui.components.TopNavBar
import com.example.ecolens.ui.viewmodels.RegisterViewModel
import com.example.ecolens.ui.viewmodels.RegisterViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var sessionViewModel: SessionViewModel

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
                MainScreen(sessionViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(sessionViewModel: SessionViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf("home", "pedometer", "camera", "qrscanner", "history")
    val showBottomBar = currentRoute in bottomBarRoutes
    val showTopBar = currentRoute in bottomBarRoutes

    val context = LocalContext.current
    val db = remember { DatabaseInstance.getDatabase(context) }
    val userDao = remember { db.userDao() }

    val registerViewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(userDao)
    )

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
                registerViewModel
            )
        }
    }
}
