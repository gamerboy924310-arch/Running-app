package com.quickfit.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

@Composable
fun BottomNavBar(
    currentScreen: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        val items = listOf(
            "dashboard" to "🏠 Dash",
            "start" to "▶️ Start",
            "history" to "📋 Hist",
            "guest" to "👤 Guest",
            "settings" to "⚙️ Sets"
        )
        items.forEach { (screen, label) ->
            NavigationBarItem(
                selected = currentScreen == screen,
                onClick = { onNavigate(screen) },
                label = { Text(label, fontSize = TextUnit(10f, TextUnitType.Sp)) },
                icon = {}
            )
        }
    }
}
