package com.abclient.ui.welcome

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.abclient.profile.ProfileManager
import com.abclient.profile.UserProfile

@Composable
fun WelcomeScreen(
    onSettingsClick: () -> Unit,
    onAddProfileClick: () -> Unit,
    onLoginProfile: (UserProfile) -> Unit = {}
) {
    val context = LocalContext.current
    var profiles by remember { mutableStateOf(listOf<UserProfile>()) }
    var selectedProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        profiles = ProfileManager.getProfiles(context)
    }

    Column(Modifier.padding(16.dp)) {
        Text("ABClient", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        if (profiles.isEmpty()) {
            Text("Нет сохранённых профилей", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Text("Профили:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            profiles.forEach { profile ->
                val isSelected = selectedProfile?.login == profile.login
                Surface(
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                    tonalElevation = if (isSelected) 2.dp else 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .clickable { selectedProfile = profile }
                ) {
                    Row(Modifier.padding(8.dp)) {
                        Text(profile.login, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.weight(1f))
                        if (isSelected) Text("✓", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (selectedProfile != null) {
                        isLoading = true
                        statusMessage = "Вход..."
                        onLoginProfile(selectedProfile!!)
                    }
                },
                enabled = selectedProfile != null && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text("Вход в игру")
            }
        }
        if (statusMessage.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(statusMessage, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onAddProfileClick, modifier = Modifier.fillMaxWidth()) {
            Text("Добавить профиль")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onSettingsClick, modifier = Modifier.fillMaxWidth()) {
            Text("Настройки")
        }
    }
}
