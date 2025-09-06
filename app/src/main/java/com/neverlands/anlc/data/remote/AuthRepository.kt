package com.neverlands.anlc.data.remote

import android.content.Context
import android.net.Uri
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
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

typealias AuthCallback = (result: AuthResult) -> Unit

sealed class AuthResult {
    data class Success(val contentUri: Uri) : AuthResult()
    data class Failure(val error: String) : AuthResult()
}

object AuthRepository {

    private val apiClient: ApiClient = ApiClientFactory.apiClient
    private val cookieJar = ApiClientFactory.cookieJar

    private var heartbeatJob: Job? = null

    fun authorize(
        context: Context,
        profile: Profile,
        password: String,
        callback: AuthCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                FileLogger.log(context, "Начало авторизации для ${profile.userNick}")

                val gamePassword = CryptoHelper.decryptString(profile.encryptedPassword, password)
                if (gamePassword.isEmpty() && profile.encryptedPassword.isNotEmpty()) {
                    withContext(Dispatchers.Main) { callback(AuthResult.Failure("Неверный пароль конфигурации")) }
                    return@launch
                }

                if (profile.autoClearCookies) {
                    (cookieJar as WebViewCookieJar).clearCookies("neverlands.ru")
                    FileLogger.log(context, "Старые куки для neverlands.ru очищены.")
                }

                apiClient.getInitialPage().body()?.close()
                apiClient.login(profile.userNick, gamePassword).body()?.close()

                val mainPageResponse = apiClient.getMainPage()
                if (!mainPageResponse.isSuccessful) {
                    withContext(Dispatchers.Main) { callback(AuthResult.Failure("Ошибка получения главной страницы: ${mainPageResponse.code()}")) }
                    return@launch
                }
                val mainPageBodyBytes = mainPageResponse.body()?.bytes() ?: ByteArray(0)
                FileLogger.log(context, "Raw response bytes (hex): ${mainPageBodyBytes.joinToString(" ") { "%02x".format(it) }}")

                val tempFile = File.createTempFile("game_content", ".html", context.cacheDir)
                tempFile.writeBytes(mainPageBodyBytes)
                val contentUri = Uri.fromFile(tempFile)

                withContext(Dispatchers.Main) { callback(AuthResult.Success(contentUri)) }

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
                    apiClient.getMainPage().body()?.close()
                    val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                    withContext(Dispatchers.Main) {
                        onHeartbeat(currentTime)
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
