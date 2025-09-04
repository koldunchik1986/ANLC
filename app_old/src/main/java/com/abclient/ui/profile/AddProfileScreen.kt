package com.abclient.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.abclient.profile.ProfileManager
import com.abclient.profile.UserProfile
import java.time.Instant

@Composable
fun AddProfileScreen(
    context: android.content.Context,
    configName: String,
    onProfileAdded: (String) -> Unit,
    onAdaptationRequested: () -> Unit
) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var canAdd by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<List<String>>(emptyList()) }
    var isTesting by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(Modifier.padding(16.dp)) {
        Text("Добавление профиля", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = login,
            onValueChange = {
                login = it
                canAdd = false
            },
            label = { Text("Логин") },
            singleLine = true
        )
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                canAdd = false
            },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                isTesting = true
                statusMessage = "Проверка..."
                scope.launch {
                    try {
                        val result = com.abclient.auth.authorize(context, configName, login, password)
                        canAdd = result && login.isNotBlank() && password.isNotBlank()
                        statusMessage = if (canAdd) "Авторизация успешна!" else "Ошибка авторизации. Проверьте данные."
                    } catch (e: Exception) {
                        canAdd = false
                        statusMessage = "Ошибка: ${e.localizedMessage}".take(100)
                    } finally {
                        isTesting = false
                        showSnackbar = true
                    }
                }
            },
            enabled = !isTesting && login.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isTesting) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
            Text("Проверить авторизацию")
        }
        if (statusMessage.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(statusMessage, color = if (canAdd) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                // После успешной авторизации получить cookies (заглушка: пусто, TODO: реализовать реальное получение)
                val cookies: Map<String, String> = emptyMap() // TODO: получить реальные cookies после авторизации
                val profile = UserProfile(
                    login = login,
                    created = Instant.now().toString(),
                    cookies = cookies,
                    lastUsed = Instant.now().toString()
                )
                ProfileManager.addProfile(context, profile)
                statusMessage = "Профиль добавлен!"
                showSnackbar = true
                onProfileAdded(login)
            },
            enabled = canAdd && login.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить профиль")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onAdaptationRequested, modifier = Modifier.fillMaxWidth()) {
            Text("Перейти к адаптации")
        }
        if (showSnackbar && statusMessage.isNotEmpty()) {
            Snackbar(
                action = {
                    TextButton(onClick = { showSnackbar = false }) { Text("OK") }
                },
                modifier = Modifier.padding(4.dp)
            ) { Text(statusMessage) }
        }
    }
}
