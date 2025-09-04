package com.abclient.auth

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset

// Модель конфига авторизации
data class AuthConfig(
    val loginUrl: String,
    val encoding: String,
    val fields: Map<String, String>,
    val cookies: List<String>,
    val headers: Map<String, String>,
    val method: String
)

// Результат загрузки конфига с флагом создания
data class AuthConfigResult(val config: AuthConfig, val wasCreated: Boolean)

// Загрузка конфига из файла, возвращает AuthConfigResult
suspend fun loadAuthConfig(context: Context, configName: String): AuthConfigResult = withContext(Dispatchers.IO) {
    val configDir = File(context.filesDir, "configs/auth")
    if (!configDir.exists()) configDir.mkdirs()
    val configFile = File(configDir, configName)
    var wasCreated = false
    if (!configFile.exists()) {
        val defaultConfig = """{
  "loginUrl": "http://neverlands.ru/game.php",
  "encoding": "windows-1251",
  "fields": {"username": "login", "password": "pass"},
  "cookies": ["watermark", "PHPSESSID"],
  "headers": {"User-Agent": "ABClient/Android", "Accept": "text/html"},
  "method": "POST"
}"""
        configFile.writeText(defaultConfig, Charset.forName("UTF-8"))
        wasCreated = true
    }
    val json = configFile.readText(Charset.forName("UTF-8"))
    val obj = JSONObject(json)
    // Исправление: если cookies не JSONArray, а строка или массив без кавычек, преобразовать
    val cookiesValue = obj.get("cookies")
    val cookiesArray = when (cookiesValue) {
        is JSONArray -> cookiesValue
        is String -> JSONArray().apply { put(cookiesValue) }
        is java.util.Collection<*> -> JSONArray(cookiesValue)
        else -> JSONArray()
    }
    // Если cookies были не массивом, пересохраняем конфиг в правильном формате
    if (cookiesValue !is JSONArray) {
        obj.put("cookies", cookiesArray)
        val configDir = File(context.filesDir, "configs/auth")
        val configFile = File(configDir, configName)
        configFile.writeText(obj.toString(2), Charset.forName("UTF-8"))
    }
    val config = AuthConfig(
        loginUrl = obj.getString("loginUrl"),
        encoding = obj.getString("encoding"),
        fields = obj.getJSONObject("fields").let { f ->
            f.keys().asSequence().associateWith { f.getString(it) }
        },
        cookies = cookiesArray.let { arr ->
            List(arr.length()) { arr.getString(it) }
        },
        headers = obj.getJSONObject("headers").let { h ->
            h.keys().asSequence().associateWith { h.getString(it) }
        },
        method = obj.getString("method")
    )
    AuthConfigResult(config, wasCreated)
}

// Загрузка cookies последнего профиля
suspend fun loadLastProfileCookies(context: Context): Map<String, String> = withContext(Dispatchers.IO) {
    val profilesDir = File(context.filesDir, "profiles")
    val lastProfile = profilesDir.listFiles()?.maxByOrNull { it.lastModified() } ?: return@withContext emptyMap()
    val cookiesFile = File(lastProfile, "cookies.json")
    if (!cookiesFile.exists()) return@withContext emptyMap()
    val json = JSONObject(cookiesFile.readText(Charset.forName("UTF-8")))
    json.keys().asSequence().associateWith { json.getString(it) }
}

// Основная функция авторизации
suspend fun authorize(context: Context, configName: String, login: String, password: String): Boolean = withContext(Dispatchers.IO) {
    if (login.isBlank() || password.isBlank()) return@withContext false
    val configResult = loadAuthConfig(context, configName)
    val origConfig = configResult.config
    var lastException: Exception? = null
    val tryUrls = buildList {
        add(origConfig.loginUrl)
        // если https, добавить http; если http — добавить https
        if (origConfig.loginUrl.startsWith("https://")) add(origConfig.loginUrl.replaceFirst("https://", "http://"))
        else if (origConfig.loginUrl.startsWith("http://")) add(origConfig.loginUrl.replaceFirst("http://", "https://"))
    }.distinct()
    for (url in tryUrls) {
        try {
            val config = origConfig.copy(loginUrl = url)
            val client = OkHttpClient.Builder().cookieJar(SimpleCookieJar()).build()
            val formBody = FormBody.Builder()
                .add(config.fields["username"] ?: "login", login)
                .add(config.fields["password"] ?: "pass", password)
                .build()
            val headersBuilder = Headers.Builder()
            for ((k, v) in config.headers) headersBuilder.add(k, v)
            val request = Request.Builder()
                .url(config.loginUrl)
                .headers(headersBuilder.build())
                .method(config.method, formBody)
                .build()
            val response = client.newCall(request).execute()
            val body = response.body?.bytes() ?: ByteArray(0)
            val decoded = String(body, Charset.forName(config.encoding))
            val success = !decoded.contains("name=\"login\"") && !decoded.contains("name=\"password\"")
            if (!success) {
                val cookies = loadLastProfileCookies(context)
                val recommendations = AuthConfigTester().analyzeResponse(response, config, decoded, cookies)
                // showAdaptationDialog(recommendations)
            }
            response.close()
            return@withContext success
        } catch (e: Exception) {
            lastException = e
            // Пробуем следующий вариант
        }
    }
    // Если оба варианта не сработали — пробрасываем последнюю ошибку
    throw lastException ?: RuntimeException("Authorization failed: unknown error")
}

// CookieJar для OkHttp (упрощённый)
class SimpleCookieJar : CookieJar {
    private val cookieStore = mutableListOf<Cookie>()
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore.addAll(cookies)
    }
    override fun loadForRequest(url: HttpUrl): List<Cookie> = cookieStore
}

// Тестер-рекомендатор для анализа ответа авторизации
class AuthConfigTester {
    fun analyzeResponse(
        response: Response,
        config: AuthConfig,
        decodedBody: String,
        cookies: Map<String, String>
    ): List<String> {
        val rec = mutableListOf<String>()
        val setCookies = response.headers("Set-Cookie").map { it.substringBefore("=") }
        val missing = setCookies.filter { it !in config.cookies }
        if (missing.isNotEmpty()) {
            rec.add("Добавьте cookie: ${missing.joinToString()}")
        }
        if ("name=\"login\"" !in decodedBody && "name=\"username\"" in decodedBody) {
            rec.add("Похоже, имя поля логина изменилось. Обновите fields.username")
        }
        // ...другие эвристики...
        return rec
    }
}
