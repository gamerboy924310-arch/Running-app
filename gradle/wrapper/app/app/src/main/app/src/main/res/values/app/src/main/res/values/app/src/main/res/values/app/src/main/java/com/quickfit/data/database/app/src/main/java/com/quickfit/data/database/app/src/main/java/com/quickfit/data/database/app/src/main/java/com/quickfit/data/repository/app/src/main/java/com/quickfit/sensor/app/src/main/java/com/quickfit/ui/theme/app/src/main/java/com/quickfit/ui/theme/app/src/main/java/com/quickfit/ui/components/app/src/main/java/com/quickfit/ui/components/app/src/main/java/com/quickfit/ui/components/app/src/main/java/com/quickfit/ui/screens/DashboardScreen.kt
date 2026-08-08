package com.quickfit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quickfit.ui.components.MetricCard
import com.quickfit.ui.components.SessionItem
import com.quickfit.ui.viewmodel.FitnessViewModel

@Composable
fun DashboardScreen(
    viewModel: FitnessViewModel,
    onNavigate: (String) -> Unit
) {
    val metrics by viewModel.metrics.collectAsState()
    val recentSessions by viewModel.recentSessions.collectAsState()
    val stats by viewModel.totalStats.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard("LIVE SPEED", String.format("%.1f", metrics.speed), "km/h", "⚡", MaterialTheme.colorScheme.primary)
                MetricCard("STEPS TODAY", metrics.steps.toString(), "", "👣", MaterialTheme.colorScheme.secondary)
                MetricCard("BPM", metrics.bpm.toString(), "bpm", "❤️", MaterialTheme.colorScheme.error)
                MetricCard("DISTANCE", String.format("%.1f", metrics.distance), "km", "📏", MaterialTheme.colorScheme.tertiary)
            }
        }
        item {
            Text("📋 RECENT SESSIONS", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        }
        if (recentSessions.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Text("No sessions yet. Start a workout!", modifier = Modifier.fillMaxWidth().padding(16.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(recentSessions) { SessionItem(it) }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text("🏆 ${stats.first}", color = MaterialTheme.colorScheme.primary)
                    Text("📏 ${String.format("%.1f", stats.second)} km", color = MaterialTheme.colorScheme.secondary)
                    Text("🔥 ${stats.third} cal", color = MaterialTheme.colorScheme.error)
                }
            }
        }
        item {
            Button(onClick = { viewModel.resetAllData() }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.error)) {
                Text("🔄 RESET DATA & START OVER")
            }
        }
    }
}
