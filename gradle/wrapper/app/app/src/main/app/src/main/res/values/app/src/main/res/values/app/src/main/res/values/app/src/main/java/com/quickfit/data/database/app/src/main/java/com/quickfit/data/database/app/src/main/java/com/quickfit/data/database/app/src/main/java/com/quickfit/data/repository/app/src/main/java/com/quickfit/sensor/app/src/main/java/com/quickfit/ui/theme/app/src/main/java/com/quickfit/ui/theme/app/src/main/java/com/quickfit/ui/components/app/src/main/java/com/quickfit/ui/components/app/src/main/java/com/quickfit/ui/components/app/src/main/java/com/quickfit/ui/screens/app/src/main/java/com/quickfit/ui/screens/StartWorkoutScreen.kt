package com.quickfit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quickfit.ui.viewmodel.FitnessViewModel

@Composable
fun StartWorkoutScreen(viewModel: FitnessViewModel, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🏃 START WORKOUT", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Text("Select workout type", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        val workouts = listOf("Run" to "🏃 Cardio", "Walk" to "🚶 Easy", "Cycle" to "🚴 Endurance", "Crawling" to "🐢 Recovery")
        workouts.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (type, desc) ->
                    Button(onClick = { viewModel.startWorkout(type) }, modifier = Modifier.weight(1f).height(80.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(desc, style = MaterialTheme.typography.titleMedium)
                            Text(type, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = onBack, modifier = Modifier.padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Text("⬅ Back")
        }
    }
}
