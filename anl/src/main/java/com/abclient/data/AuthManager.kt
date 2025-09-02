package com.abclient.data

// Менеджер авторизации: логика HTTP-запросов, cookies, заголовки
import kotlinx.coroutines.*
import kotlinx.coroutines.DelicateCoroutinesApi
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.CopyOnWriteArrayList

object AuthManager {
    // Все поля, функции и классы внутри object AuthManager
    val profileCookies = mutableMapOf<String, List<Cookie>>()
    private var heartbeatJob: Job? = null

    const val GAME_URL = "http://neverlands.ru/"
    const val LOGIN_URL = "http://neverlands.ru/game.php"

    val client by lazy {
        val builder = OkHttpClient.Builder()
            .cookieJar(SimpleCookieJar())
            .followRedirects(true)
    // Временно всегда логируем HTTP-запросы/ответы для диагностики
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    builder.addInterceptor(logging)
        builder.build()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun startSessionHeartbeat(context: android.content.Context, login: String) {
        heartbeatJob?.cancel()
        heartbeatJob = GlobalScope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    val cookies = profileCookies[login] ?: emptyList()
                    val cookieHeader = cookies.joinToString("; ") { it.name + "=" + it.value }
                    val request = Request.Builder()
                        .url(GAME_URL + "main.php")
                        .addHeader("Host", "neverlands.ru")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("Cache-Control", "max-age=0")
                        .addHeader("Upgrade-Insecure-Requests", "1")
                        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.7258.138 Safari/537.36")
                        .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                        .addHeader("Referer", GAME_URL)
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        .addHeader("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                        .addHeader("Cookie", cookieHeader)
                        .build()
                    val response = client.newCall(request).execute()
                    val body = response.body?.string() ?: ""
                    response.close()
                    logToFile(context, "HEARTBEAT main.php\nRESPONSE:\n$body")
                } catch (e: Exception) {
                    logToFile(context, "HEARTBEAT ERROR: ${e.message}")
                }
                delay(3000)
            }
        }
    }

    fun stopSessionHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

    private fun logToFile(context: android.content.Context, text: String) {
        try {
            com.abclient.log.NetworkLogger.log(context, text)
        } catch (e: Exception) {
            android.util.Log.e("ABClientLog", "Ошибка записи в NetworkLogger: ${e.message}", e)
        }
    }

    class SimpleCookieJar : CookieJar {
        private val cookieMap = mutableMapOf<String, MutableMap<String, Cookie>>()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            val domain = url.topPrivateDomain() ?: url.host
            val domainCookies = cookieMap.getOrPut(domain) { mutableMapOf() }
            for (cookie in cookies) {
                domainCookies[cookie.name] = cookie
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            val domain = url.topPrivateDomain() ?: url.host
            val domainCookies = cookieMap[domain] ?: return emptyList()
            val now = System.currentTimeMillis() / 1000
            return domainCookies.values.filter { cookie ->
                (cookie.expiresAt / 1000 > now) && url.encodedPath.startsWith(cookie.path)
            }
        }

        fun dumpAllCookies(domain: String): String {
            return cookieMap[domain]?.values?.joinToString("; ") { it.name + "=" + it.value } ?: ""
        }
    }

    /**
     * Асинхронная авторизация на сервере Neverlands.ru
     * @param login логин (имя профиля)
     * @param password пароль
     * @param callback результат: 0 - успех, 1 - неверный логин/пароль, 2 - капча, 3 - ошибка сети
     * @param context контекст для логирования (может быть null)
     */
    fun authorizeAsync(
        login: String,
        password: String,
    callback: (Int, String?, String?, Int?, String?, Int?, String?) -> Unit,
        context: android.content.Context? = null
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Очистка cookies, если включено в профиле
                val userProfile = ProfileManager.getProfileNames().find { it == login }?.let {
                    val pass = ProfileManager.getPassword(it)
                    val autologin = ProfileManager.getAutologin(it) ?: false
                    val useProxy = ProfileManager.getUseProxy(it) ?: false
                    val autoClearCookies = ProfileManager.getAutoClearCookies(it) ?: false
                    ProfileManager.Profile(it, pass ?: "", autologin, useProxy, autoClearCookies)
                }
                val url = HttpUrl.Builder().scheme("http").host("neverlands.ru").build()
                if (userProfile?.autoClearCookies == true) {
                    profileCookies[login] = emptyList()
                    (client.cookieJar as? SimpleCookieJar)?.saveFromResponse(url, emptyList())
                }

                // 1. GET-запрос для получения cookies
                val getRequest = Request.Builder()
                    .url(GAME_URL)
                    .addHeader("Host", "neverlands.ru")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Cache-Control", "max-age=0")
                    .addHeader("Upgrade-Insecure-Requests", "1")
                    .addHeader("Origin", GAME_URL)
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.7258.138 Safari/537.36")
                    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                    .addHeader("Referer", GAME_URL)
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .addHeader("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                    .build()
                val getResponse = client.newCall(getRequest).execute()
                val getStatusCode = getResponse.code
                val getBody = getResponse.body?.string() ?: ""
                val getCookies = getResponse.headers("Set-Cookie")
                getResponse.close()
                val cookieHeader = client.cookieJar.loadForRequest(url).joinToString("; ") { it.name + "=" + it.value }
                val allCookies = (client.cookieJar as? SimpleCookieJar)?.dumpAllCookies("neverlands.ru") ?: ""
                context?.let { logToFile(it, "[AUTH] GET $GAME_URL\n--HEADERS--\n" +
                    getRequest.headers.joinToString("\n") { it.first + ": " + it.second } +
                    "\n--COOKIES (sent)--\n$cookieHeader\n--COOKIES (all)--\n$allCookies\n--SET-COOKIE--\n${getCookies.joinToString("\n")}\n--PARAMS--\n(нет, GET)") }

                // 2. POST-запрос авторизации
                val nickEncoded = java.net.URLEncoder.encode(login, "windows-1251")
                val passEncoded = java.net.URLEncoder.encode(password, "windows-1251")
                val postData = "player_nick=$nickEncoded&player_password=$passEncoded"
                val mediaType = "application/x-www-form-urlencoded".toMediaType()
                val requestBody = postData.toByteArray(charset("windows-1251")).toRequestBody(mediaType)
                val postRequest = Request.Builder()
                    .url(LOGIN_URL)
                    .post(requestBody)
                    .addHeader("Host", "neverlands.ru")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Cache-Control", "max-age=0")
                    .addHeader("Upgrade-Insecure-Requests", "1")
                    .addHeader("Origin", GAME_URL)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=windows-1251")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.7258.138 Safari/537.36")
                    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                    .addHeader("Referer", GAME_URL)
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .addHeader("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                    .addHeader("Cookie", cookieHeader)
                    .build()
                val postResponse = client.newCall(postRequest).execute()
                val postStatusCode = postResponse.code
                val postBody = postResponse.body?.string() ?: ""
                val postCookies = postResponse.headers("Set-Cookie")
                // Явно обновляем cookies из ответа POST
                (client.cookieJar as? SimpleCookieJar)?.saveFromResponse(url, Cookie.parseAll(url, postResponse.headers))
                val allCookiesPost = (client.cookieJar as? SimpleCookieJar)?.dumpAllCookies("neverlands.ru") ?: ""
                postResponse.close()
                context?.let { logToFile(it, "[AUTH] POST $LOGIN_URL\n--HEADERS--\n" +
                    postRequest.headers.joinToString("\n") { it.first + ": " + it.second } +
                    "\n--COOKIES (sent)--\n$cookieHeader\n--COOKIES (all)--\n$allCookiesPost\n--SET-COOKIE--\n${postCookies.joinToString("\n")}\n--PARAMS--\n$postData") }

                // Сохраняем cookies для профиля (только после авторизации)
                profileCookies[login] = client.cookieJar.loadForRequest(url)
                val authCookies = profileCookies[login]?.joinToString("; ") { it.name + "=" + it.value } ?: ""

                // 3. GET main.php с авторизационными cookies
                val mainRequest = Request.Builder()
                    .url(GAME_URL + "main.php")
                    .addHeader("Host", "neverlands.ru")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Cache-Control", "max-age=0")
                    .addHeader("Upgrade-Insecure-Requests", "1")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.7258.138 Safari/537.36")
                    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                    .addHeader("Referer", LOGIN_URL)
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .addHeader("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                    .addHeader("Cookie", authCookies)
                    .build()
                val mainResponse = client.newCall(mainRequest).execute()
                val mainBodyRaw = mainResponse.body?.bytes() ?: ByteArray(0)
                val encoding = mainResponse.header("Content-Encoding")
                val charset = mainResponse.header("Content-Type")?.let {
                    Regex("charset=([\\w-]+)", RegexOption.IGNORE_CASE).find(it)?.groupValues?.getOrNull(1)
                } ?: "windows-1251"
                val decompressed = if (encoding?.contains("gzip") == true) {
                    java.util.zip.GZIPInputStream(mainBodyRaw.inputStream()).readBytes()
                } else mainBodyRaw
                val mainBody = try {
                    String(decompressed, charset(charset))
                } catch (e: Exception) {
                    "Ошибка декодирования: ${e.message}"
                }
                mainResponse.close()
                val allCookiesMain = (client.cookieJar as? SimpleCookieJar)?.dumpAllCookies("neverlands.ru") ?: ""
                context?.let { logToFile(it, "[AUTH] GET ${GAME_URL}main.php\n--HEADERS--\n" +
                    mainRequest.headers.joinToString("\n") { it.first + ": " + it.second } +
                    "\n--COOKIES (sent)--\n$authCookies\n--COOKIES (all)--\n$allCookiesMain\n--PARAMS--\n(нет, GET)\n--RESPONSE (decoded)--\n$mainBody") }

                // Проверка успешности авторизации (поиск формы/ошибки/редиректа)
                if (mainBody.contains("<form method=\"post\" id=\"auth_form\" action=\"./game.php\"", true)) {
                    callback(1, null, "Неверный логин или пароль", getStatusCode, getBody, postStatusCode, postBody)
                } else if (mainBody.contains("captcha", true)) {
                    callback(2, null, "Требуется капча", getStatusCode, getBody, postStatusCode, postBody)
                } else if (mainBody.isNotBlank()) {
                    callback(0, mainBody, null, getStatusCode, getBody, postStatusCode, postBody)
                } else {
                    callback(3, null, "Ошибка сети или пустой ответ", getStatusCode, getBody, postStatusCode, postBody)
                }
            } catch (e: Exception) {
                context?.let { logToFile(it, "AUTH ERROR: ${e.message}") }
                callback(3, null, "Ошибка: ${e.message}", null, null, null, null)
            }
        }
    }
}

