package com.neverlands.anlc.data.remote

import android.content.Context
import android.webkit.CookieManager
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
import okhttp3.Cookie
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

typealias AuthCallback = (result: AuthResult) -> Unit

sealed class AuthResult {
    data class Success(val htmlContent: String) : AuthResult()
    data class Failure(val error: String) : AuthResult()
}

object AuthRepository {

    private val apiClient: ApiClient = ApiClientFactory.apiClient
    private val cookieJar = ApiClientFactory.cookieJar as WebViewCookieJar

    private var heartbeatJob: Job? = null

    fun authorize(
        context: Context,
        profile: Profile,
        password: String,
        callback: AuthCallback
    ) {
        FileLogger.log(context, "AuthRepository.authorize called for ${profile.userNick}")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                FileLogger.log(context, "Начало авторизации для ${profile.userNick}")

                val gamePassword = CryptoHelper.decryptString(profile.encryptedPassword, password)
                if (gamePassword.isEmpty() && profile.encryptedPassword.isNotEmpty()) {
                    FileLogger.log(context, "Ошибка: Неверный пароль конфигурации.")
                    withContext(Dispatchers.Main) { callback(AuthResult.Failure("Неверный пароль конфигурации")) }
                    return@launch
                }

                if (profile.autoClearCookies) {
                    cookieJar.clearCookies("neverlands.ru")
                    FileLogger.log(context, "Старые куки для neverlands.ru очищены.")
                }

                // Step 1: GET / to get initial cookies
                FileLogger.log(context, "GET /")
                val initialResponse = apiClient.getInitialPage()
                if (!initialResponse.isSuccessful) {
                    FileLogger.log(context, "Ошибка получения стартовой страницы: ${initialResponse.code()}")
                    withContext(Dispatchers.Main) { callback(AuthResult.Failure("Ошибка получения стартовой страницы: ${initialResponse.code()}")) }
                    return@launch
                }
                FileLogger.log(context, "GET / - Успех. Код: ${initialResponse.code()}")

                // Step 2: POST /game.php with credentials
                FileLogger.log(context, "POST /game.php")
                val loginResponse = apiClient.login(profile.userNick, gamePassword)
                if (!loginResponse.isSuccessful) {
                    FileLogger.log(context, "Ошибка входа: ${loginResponse.code()}")
                    withContext(Dispatchers.Main) { callback(AuthResult.Failure("Ошибка входа: ${loginResponse.code()}")) }
                    return@launch
                }
                FileLogger.log(context, "POST /game.php - Успех. Код: ${loginResponse.code()}")

                // Step 3: GET /main.php to get the game page
                FileLogger.log(context, "GET /main.php")
                val mainPageResponse = apiClient.getMainPage()
                if (!mainPageResponse.isSuccessful) {
                    FileLogger.log(context, "Ошибка получения главной страницы: ${mainPageResponse.code()}")
                    withContext(Dispatchers.Main) { callback(AuthResult.Failure("Ошибка получения главной страницы: ${mainPageResponse.code()}")) }
                    return@launch
                }
                val mainPageBody = mainPageResponse.body() ?: ""
                FileLogger.log(context, "GET /main.php - Успех. Код: ${mainPageResponse.code()}")

                // Step 4: Check for login errors
                if (mainPageBody.contains("auth_form") || mainPageBody.contains("неверный пароль")) {
                    FileLogger.log(context, "Ошибка: Неверный логин или пароль.")
                    withContext(Dispatchers.Main) { callback(AuthResult.Failure("Неверный логин или пароль.")) }
                    return@launch
                }

                FileLogger.log(context, "Авторизация успешна, вызов callback.")
                withContext(Dispatchers.Main) { callback(AuthResult.Success(mainPageBody)) }

            } catch (e: Exception) {
                val errorMsg = "Критическая ошибка авторизации: ${e.javaClass.simpleName}: ${e.message}"
                FileLogger.log(context, errorMsg)
                withContext(Dispatchers.Main) { callback(AuthResult.Failure(e.message ?: "Unknown error")) }
            }
        }
    }

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
                    }
                } catch (e: IOException) {
                    // Handle network error
                }
                delay(30000) // 30 seconds
            }
        }
    }

    fun stopSessionHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

    fun logout() {
        stopSessionHeartbeat()
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }
}
