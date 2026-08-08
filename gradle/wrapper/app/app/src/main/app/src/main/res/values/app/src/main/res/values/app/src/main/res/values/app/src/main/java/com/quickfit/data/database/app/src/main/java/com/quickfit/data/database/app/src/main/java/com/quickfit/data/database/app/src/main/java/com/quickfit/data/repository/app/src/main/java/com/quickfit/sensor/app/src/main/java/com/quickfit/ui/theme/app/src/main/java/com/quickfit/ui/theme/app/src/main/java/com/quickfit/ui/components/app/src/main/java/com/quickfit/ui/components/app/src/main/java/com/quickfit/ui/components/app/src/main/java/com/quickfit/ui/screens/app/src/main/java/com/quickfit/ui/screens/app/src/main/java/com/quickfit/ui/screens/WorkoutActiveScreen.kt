package com.quickfit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickfit.ui.viewmodel.FitnessViewModel

@Composable
fun WorkoutActiveScreen(viewModel: FitnessViewModel, onEnd: () -> Unit) {
    val workout by viewModel.workout.collectAsState()
    val metrics by viewModel.metrics.collectAsState()
    val h = workout.elapsedSeconds / 3600
    val m = (workout.elapsedSeconds % 3600) / 60
    val s = workout.elapsedSeconds % 60
    val timerStr = String.format("%02d:%02d:%02d", h, m, s)

    Column(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)),
            border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.error)) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🏃 ${workout.type.uppercase()}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.secondary)
                Text(timerStr, fontSize = 42.sp, fontWeight = FontWeight.Light, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(vertical = 4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text("⚡ ${String.format("%.1f", metrics.speed)} km/h", color = MaterialTheme.colorScheme.primary)
                    Text("❤️ ${metrics.bpm} bpm", color = MaterialTheme.colorScheme.error)
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatItem("Distance", String.format("%.2f", workout.distance), "km")
            StatItem("Steps", workout.steps.toString(), "")
            StatItem("Calories", workout.calories.toString(), "kcal")
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.togglePause() }, modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Text(if (workout.paused) "▶️ Resume" else "⏸️ Pause")
            }
            Button(onClick = { viewModel.endWorkout(); onEnd() }, modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("⏹️ End")
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, unit: String) {
    Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
            if (unit.isNotEmpty()) Text(unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
