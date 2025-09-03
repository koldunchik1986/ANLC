package com.neverlands.anlc.auth

import android.content.Context
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

object AuthRepository {

    private const val GAME_URL = "http://neverlands.ru/"
    private const val LOGIN_URL = "http://neverlands.ru/game.php"
    private const val MAIN_PHP_URL = "http://neverlands.ru/main.php"

    private val cookieJar = SimpleCookieJar()
    private val client by lazy {
        OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .build()
    }

    private var heartbeatJob: Job? = null
    private val profileCookies = mutableMapOf<String, List<Cookie>>()
    private lateinit var logFile: File

    fun init(context: Context) {
        logFile = File(context.filesDir, "log.txt")
        ProfileManager.init(context.filesDir)
    }

    fun authorizeAsync(profile: ProfileManager.Profile, callback: AuthCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (profile.autoClearCookies) {
                    GAME_URL.toHttpUrlOrNull()?.host?.let { cookieJar.clearCookies(it) }
                }

                // 1. Get initial cookies
                val initialResponse = client.newCall(Request.Builder().url(GAME_URL).build()).execute()
                logToFile("Initial response: ${initialResponse.code}")

                // 2. Post credentials
                val formBody = "login=${profile.login}&pass=${profile.password}"
                val requestBody = formBody.toRequestBody("application/x-www-form-urlencoded; charset=windows-1251".toMediaType())
                val loginRequest = Request.Builder().url(LOGIN_URL).post(requestBody).build()
                val loginResponse = client.newCall(loginRequest).execute()
                val loginResponseBody = loginResponse.body?.string() ?: ""
                logToFile("Login response: ${loginResponse.code}")

                if (!loginResponse.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        callback(3, null, "Login request failed: ${loginResponse.code}")
                    }
                    return@launch
                }

                // 3. Save cookies
                GAME_URL.toHttpUrlOrNull()?.host?.let { host ->
                    val cookies = cookieJar.getCookies(host)
                    profileCookies[profile.login] = cookies
                    logToFile("Saved cookies for ${profile.login}: $cookies")
                }

                // 4. Verify login
                val mainRequest = Request.Builder().url(MAIN_PHP_URL).build()
                val mainResponse = client.newCall(mainRequest).execute()
                val mainResponseBody = mainResponse.body?.string() ?: ""

                if (mainResponseBody.contains("parent.location='/game.php'") || mainResponseBody.contains("location='/game.php'")) {
                    withContext(Dispatchers.Main) {
                        callback(1, null, "Invalid login or password")
                    }
                } else if (mainResponseBody.contains("aboi.png")) { // Assuming aboi.png is part of captcha
                    withContext(Dispatchers.Main) {
                        callback(2, null, "Captcha required")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        callback(0, mainResponseBody, null)
                    }
                }

            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    callback(3, null, e.message)
                }
            }
        }
    }

    fun startSessionHeartbeat(profile: ProfileManager.Profile) {
        stopSessionHeartbeat()
        heartbeatJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(3000)
                try {
                    val request = Request.Builder().url(MAIN_PHP_URL).build()
                    val response = client.newCall(request).execute()
                    logToFile("Heartbeat response: ${response.code}")
                } catch (e: IOException) {
                    logToFile("Heartbeat failed: ${e.message}")
                }
            }
        }
    }

    fun stopSessionHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

    private fun logToFile(message: String) {
        if (::logFile.isInitialized) {
            logFile.appendText("[${System.currentTimeMillis()}] $message\n")
        }
    }
}