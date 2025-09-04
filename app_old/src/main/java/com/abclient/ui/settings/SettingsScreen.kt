package com.abclient.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(onAdaptationClick: () -> Unit) {
    Column(Modifier.padding(16.dp)) {
        Text("Настройки", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        // Другие настройки...
        Divider()
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Адаптация авторизации",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAdaptationClick() }
                .padding(8.dp)
        )
        Text(
            text = "Изменить параметры авторизации, протестировать и получить рекомендации.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
    }
}
