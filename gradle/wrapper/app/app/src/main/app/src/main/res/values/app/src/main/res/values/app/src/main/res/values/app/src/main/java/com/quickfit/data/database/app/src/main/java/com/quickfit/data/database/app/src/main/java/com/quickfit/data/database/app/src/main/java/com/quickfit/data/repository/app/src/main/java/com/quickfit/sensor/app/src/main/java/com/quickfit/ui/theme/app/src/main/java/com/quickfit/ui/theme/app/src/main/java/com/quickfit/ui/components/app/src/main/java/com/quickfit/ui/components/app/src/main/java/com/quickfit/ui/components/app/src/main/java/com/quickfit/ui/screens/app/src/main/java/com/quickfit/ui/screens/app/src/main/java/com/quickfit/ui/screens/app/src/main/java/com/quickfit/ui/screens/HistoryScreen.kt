package com.quickfit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quickfit.ui.components.SessionItemFull
import com.quickfit.ui.viewmodel.FitnessViewModel

@Composable
fun HistoryScreen(viewModel: FitnessViewModel, onBack: () -> Unit) {
    val sessions by viewModel.allSessions.collectAsState()
    val stats by viewModel.totalStats.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        Text("📜 WORKOUT HISTORY", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))
        if (sessions.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth().weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Text("📭 No sessions recorded yet.", modifier = Modifier.fillMaxWidth().padding(32.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(sessions) { SessionItemFull(it) }
            }
        }
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                Text("📊 ${sessions.size} sessions", color = MaterialTheme.colorScheme.primary)
                Text("📏 ${String.format("%.1f", stats.second)} km", color = MaterialTheme.colorScheme.secondary)
                Text("🔥 ${stats.third} cal", color = MaterialTheme.colorScheme.error)
            }
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Text("⬅ Back")
        }
    }
}
