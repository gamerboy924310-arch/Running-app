package com.quickfit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quickfit.ui.viewmodel.FitnessViewModel

@Composable
fun SettingsScreen(viewModel: FitnessViewModel, onBack: () -> Unit) {
    val profile by viewModel.profile.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Text("⚙️ SETTINGS", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.padding(bottom = 8.dp))
        val settings = listOf("Name" to profile.name, "Age" to profile.age.toString(), "Weight" to String.format("%.1f kg", profile.weight),
            "Height" to String.format("%.1f cm", profile.height), "Daily Step Goal" to profile.dailyStepGoal.toString(),
            "Weekly Run Goal" to String.format("%.1f km", profile.weeklyRunGoal))
        settings.forEach { (label, value) ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(label, color = MaterialTheme.colorScheme.onBackground)
                    Text(value, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Text("⬅ Back")
        }
    }
}
