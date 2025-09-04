package com.neverlands.anlc.data.remote

import com.neverlands.anlc.data.local.model.Profile
import com.neverlands.anlc.data.remote.api.ApiClient
import com.neverlands.anlc.util.CryptoHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

typealias AuthCallback = (result: AuthResult) -> Unit

sealed class AuthResult {
    object Success : AuthResult()
    data class Failure(val error: String) : AuthResult()
    object CaptchaRequired : AuthResult()
}

/**
 * Репозиторий для управления авторизацией и сессией.
 * Является единой точкой для всей логики, связанной с аутентификацией.
 */
object AuthRepository {

    private val apiClient: ApiClient = ApiClientFactory.apiClient
    private val authInterceptor = ApiClientFactory.authInterceptor
    private val cookieJar = ApiClientFactory.cookieJar

    private var heartbeatJob: Job? = null

    /**
     * Выполняет полный цикл авторизации.
     * @param profile Профиль пользователя.
     * @param password Пароль для расшифровки данных профиля.
     * @param callback Callback для уведомления о результате.
     */
    fun authorize(
        profile: Profile,
        password: String,
        callback: AuthCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Расшифровываем пароли
                val gamePassword = CryptoHelper.decryptString(profile.encryptedPassword, password)
                val flashPassword = CryptoHelper.decryptString(profile.encryptedPasswordFlash, password)

                // 2. Очищаем старые куки, если нужно
                if (profile.autoClearCookies) {
                    cookieJar.clearCookies("neverlands.ru")
                }

                // 3. Получаем начальные куки
                apiClient.getInitialPage()

                // 4. Отправляем логин и пароль
                val loginResponse = apiClient.login(profile.userNick, gamePassword)
                if (!loginResponse.isSuccessful) {
                    withContext(Dispatchers.Main) { callback(AuthResult.Failure("Login request failed")) }
                    return@launch
                }

                // 5. Проверяем ответ на наличие ошибок (неверный пароль, капча)
                val loginBody = loginResponse.body()?.string() ?: ""
                if (loginBody.contains("parent.location='/game.php'") || loginBody.contains("location='/game.php'")) {
                    withContext(Dispatchers.Main) { callback(AuthResult.Failure("Неверный логин или пароль")) }
                    return@launch
                } else if (loginBody.contains("aboi.png")) {
                    withContext(Dispatchers.Main) { callback(AuthResult.CaptchaRequired) }
                    return@launch
                }

                // 6. Если все успешно, устанавливаем флеш-пароль в перехватчик
                authInterceptor.setPassword(flashPassword)

                // 7. Уведомляем об успехе
                withContext(Dispatchers.Main) { callback(AuthResult.Success) }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(AuthResult.Failure(e.message ?: "Unknown error")) }
            }
        }
    }

    /**
     * Запускает "пульс" для поддержания сессии.
     * @param onHeartbeat Callback, который вызывается при каждом успешном "пульсе" и передает текущее время.
     */
    fun startSessionHeartbeat(onHeartbeat: (time: String) -> Unit) {
        stopSessionHeartbeat()
        heartbeatJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    val response = apiClient.getMainPage()
                    if (response.isSuccessful) {
                        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                        withContext(Dispatchers.Main) {
                            onHeartbeat(currentTime)
                        }
                    } else {
                        // Можно добавить логику обработки неудачного "пульса"
                    }
                } catch (e: IOException) {
                    // Можно добавить логику обработки ошибки сети
                }
                delay(3000)
            }
        }
    }

    /**
     * Останавливает "пульс" сессии.
     */
    fun stopSessionHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

    /**
     * Проверяет, авторизован ли пользователь.
     * @return true, если сессионная кука существует.
     */
    fun isLoggedIn(): Boolean {
        val cookies = cookieJar.getCookies("neverlands.ru")
        return cookies.any { it.name == "PHPSESSID" }
    }

    /**
     * Выход из системы.
     */
    fun logout() {
        cookieJar.clearCookies("neverlands.ru")
        authInterceptor.setPassword(null)
        stopSessionHeartbeat()
    }
}
