package com.quickfit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.quickfit.data.database.AppDatabase
import com.quickfit.data.repository.FitnessRepository
import com.quickfit.ui.components.BottomNavBar
import com.quickfit.ui.screens.*
import com.quickfit.ui.theme.QuickFitTheme
import com.quickfit.ui.viewmodel.FitnessViewModel
import com.quickfit.ui.viewmodel.FitnessViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickFitTheme {
                val database = AppDatabase.getInstance(this)
                val repository = FitnessRepository(database)
                val viewModel: FitnessViewModel = viewModel(
                    factory = FitnessViewModelFactory(repository)
                )
                viewModel.initSensorService(this)
                requestPermissions()

                Surface(modifier = Modifier.fillMaxSize()) {
                    QuickFitApp(viewModel)
                }
            }
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BODY_SENSORS)
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 100)
        }
    }
}

@Composable
fun QuickFitApp(viewModel: FitnessViewModel) {
    val navController = rememberNavController()
    val currentScreen by viewModel.currentScreen.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
            Text("🏃 QUICK FIT", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Text("No login & direct use", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Box(modifier = Modifier.weight(1f)) {
            NavHost(navController, startDestination = "dashboard") {
                composable("dashboard") { DashboardScreen(viewModel) { navController.navigate(it) } }
                composable("start") { StartWorkoutScreen(viewModel) { navController.popBackStack() } }
                composable("workout") { WorkoutActiveScreen(viewModel) { navController.popBackStack() } }
                composable("history") { HistoryScreen(viewModel) { navController.popBackStack() } }
                composable("guest") { GuestScreen(viewModel) { navController.popBackStack() } }
                composable("settings") { SettingsScreen(viewModel) { navController.popBackStack() } }
            }
        }

        BottomNavBar(
            currentScreen = currentScreen,
            onNavigate = { screen ->
                viewModel.setCurrentScreen(screen)
                navController.navigate(screen) { popUpTo("dashboard") { inclusive = false } }
            }
        )
    }
}
