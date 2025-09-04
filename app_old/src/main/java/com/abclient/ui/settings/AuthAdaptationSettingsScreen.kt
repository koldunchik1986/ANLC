package com.abclient.ui.settings

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.abclient.auth.AuthConfig
import com.abclient.auth.loadAuthConfig
import com.abclient.auth.AuthConfigResult
import kotlinx.coroutines.launch

@Composable
fun AuthAdaptationSettingsScreen(
    context: Context,
    configName: String,
    onConfigChanged: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var config by remember { mutableStateOf<AuthConfig?>(null) }
    var showCreated by remember { mutableStateOf(false) }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var testResult by remember { mutableStateOf<List<String>>(emptyList()) }
    var isTesting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var statusMessage by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(configName) {
    val result = loadAuthConfig(context, configName)
    config = result.config
    showCreated = result.wasCreated
    }

    Column(Modifier.padding(16.dp)) {
        if (showCreated) {
            Text(
                "Создан новый конфиг авторизации по умолчанию (auth_config.json)",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Text("Адаптация авторизации", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        config?.let { cfg ->
            var loginUrl by remember { mutableStateOf(cfg.loginUrl.replace("login.php", "game.php")) }
            var isEditingUrl by remember { mutableStateOf(false) }
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                OutlinedTextField(
                    value = loginUrl,
                    onValueChange = { loginUrl = it },
                    label = { Text("Login URL") },
                    enabled = isEditingUrl,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                val scope = rememberCoroutineScope()
                Button(onClick = {
                    if (isEditingUrl) {
                        val newConfig = cfg.copy(loginUrl = loginUrl)
                        scope.launch {
                            com.abclient.auth.saveAuthConfig(context, configName, newConfig)
                            config = newConfig
                            statusMessage = "Login URL сохранён"
                            showSnackbar = true
                        }
                    }
                    isEditingUrl = !isEditingUrl
                }) {
                    Text(if (isEditingUrl) "Сохранить" else "Редактировать")
                }
            }
            OutlinedTextField(
                value = cfg.encoding,
                onValueChange = { /* ... */ },
                label = { Text("Encoding") },
                enabled = false
            )
            // ...другие параметры (только просмотр, редактирование через отдельный экран)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = login,
                onValueChange = { login = it },
                label = { Text("Логин для теста") }
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль для теста") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                isTesting = true
                errorMessage = null
                statusMessage = "Проверка..."
                showSnackbar = false
                scope.launch {
                    try {
                        val result = com.abclient.auth.authorize(context, configName, login, password)
                        statusMessage = if (result) "Авторизация успешна!" else "Ошибка авторизации. Проверьте данные."
                        showSnackbar = true
                        isTesting = false
                        onConfigChanged()
                    } catch (e: Exception) {
                        errorMessage = when {
                            e.message?.contains("ECONNREFUSED", true) == true -> "Сервер недоступен или не принимает соединения (http/https)."
                            e.message?.contains("SSL", true) == true -> "Ошибка SSL-соединения. Сертификат сервера не принят."
                            e.message?.contains("timeout", true) == true -> "Превышено время ожидания ответа от сервера."
                            else -> "Ошибка сети: ${e.localizedMessage}"
                        }
                        statusMessage = errorMessage ?: ""
                        showSnackbar = true
                        isTesting = false
                    }
                }
            }, enabled = !isTesting) {
                if (isTesting) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text("Проверить и адаптировать")
            }
            if (showSnackbar && statusMessage.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Snackbar(
                    action = {
                        TextButton(onClick = { showSnackbar = false }) { Text("OK") }
                    },
                    modifier = Modifier.padding(4.dp)
                ) { Text(statusMessage) }
            }
        } ?: Text("Конфиг не найден")
    }
}
