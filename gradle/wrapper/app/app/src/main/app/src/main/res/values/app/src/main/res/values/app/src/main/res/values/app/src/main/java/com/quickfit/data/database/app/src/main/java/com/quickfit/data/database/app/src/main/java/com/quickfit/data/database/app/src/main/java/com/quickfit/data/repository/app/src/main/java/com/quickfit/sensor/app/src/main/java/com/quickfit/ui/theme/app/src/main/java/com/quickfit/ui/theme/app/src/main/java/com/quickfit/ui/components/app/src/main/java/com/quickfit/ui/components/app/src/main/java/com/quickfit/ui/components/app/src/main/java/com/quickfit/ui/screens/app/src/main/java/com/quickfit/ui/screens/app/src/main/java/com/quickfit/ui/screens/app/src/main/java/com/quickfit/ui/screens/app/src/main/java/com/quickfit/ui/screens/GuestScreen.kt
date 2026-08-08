package com.quickfit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quickfit.ui.viewmodel.FitnessViewModel

@Composable
fun GuestScreen(viewModel: FitnessViewModel, onBack: () -> Unit) {
    val profile by viewModel.profile.collectAsState()
    val stats by viewModel.totalStats.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("👤 GUEST MODE", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.secondary)
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Welcome, ${profile.name}!", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                Text("Your data is stored locally.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📊", style = MaterialTheme.typography.titleLarge)
                        Text("${stats.first}", style = MaterialTheme.typography.titleMedium)
                        Text("Sessions", style = MaterialTheme.typography.labelSmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📏", style = MaterialTheme.typography.titleLarge)
                        Text("${String.format("%.1f", stats.second)}", style = MaterialTheme.typography.titleMedium)
                        Text("km", style = MaterialTheme.typography.labelSmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔥", style = MaterialTheme.typography.titleLarge)
                        Text("${stats.third}", style = MaterialTheme.typography.titleMedium)
                        Text("cal", style = MaterialTheme.typography.labelSmall)
                    }
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
