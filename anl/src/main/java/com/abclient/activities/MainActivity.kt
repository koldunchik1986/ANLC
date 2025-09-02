
package com.abclient.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Button
import com.abclient.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var tv: TextView
    private lateinit var btn: Button
    private lateinit var layout: android.widget.LinearLayout
    private var profile: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profile = intent.getStringExtra("profile") ?: ""
        password = com.abclient.data.ProfileManager.getPassword(profile) ?: ""

    layout = android.widget.LinearLayout(this)
    layout.orientation = android.widget.LinearLayout.VERTICAL
    tv = TextView(this)
    tv.text = "Загрузка..."
    btn = Button(this)
    btn.text = "Войти"
    btn.isEnabled = false
    layout.addView(tv)
    layout.addView(btn)

    // Кнопка для открытия game.html и поле с путём
    val btnOpenGameHtml = Button(this)
    btnOpenGameHtml.text = "Открыть game.html"
    btnOpenGameHtml.isEnabled = false
    val tvGameHtmlPath = TextView(this)
    tvGameHtmlPath.text = "Путь к game.html: "
    layout.addView(btnOpenGameHtml)
    layout.addView(tvGameHtmlPath)
    setContentView(layout)

        btn.setOnClickListener {
            tv.text = "Авторизация..."
            btn.isEnabled = false
            btnOpenGameHtml.isEnabled = false
            // Всегда выполняем авторизацию (GET+POST+GET main.php)
            com.abclient.data.AuthManager.authorizeAsync(profile, password, { status: Int, body: String?, errorMsg: String?, getStatus: Int?, getBody: String?, postStatus: Int?, postBody: String? ->
                runOnUiThread {
                    btn.isEnabled = true
                    // Логируем все параметры для диагностики
                    val logText = "status=$status\nbody=$body\nerrorMsg=$errorMsg\ngetStatus=$getStatus\ngetBody=$getBody\npostStatus=$postStatus\npostBody=$postBody"
                    android.util.Log.d("AUTH_LOG", logText)
                    if (status == 0) {
                        tv.text = "Вход успешен!"
                        val url = okhttp3.HttpUrl.Builder().scheme("http").host("neverlands.ru").build()
                        val cookiesList = com.abclient.data.AuthManager.client.cookieJar.loadForRequest(url)
                        val cookies = cookiesList.joinToString("; ") { it.name + "=" + it.value }
                        com.abclient.data.ProfileManager.setCookies(profile, cookies, this)
                        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                val cacheKey = "http://neverlands.ru/game.php"
                                val cached = com.abclient.cache.FileCache.get(this@MainActivity, cacheKey)
                                val (rawHtml, fromCache) = if (cached != null) {
                                    val decoded = com.abclient.filter.ResponseDecoder.decode(cached, null, null, null)
                                    decoded to true
                                } else {
                                    val gameRequest = okhttp3.Request.Builder()
                                        .url(cacheKey)
                                        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.7258.138 Safari/537.36")
                                        .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                                        .addHeader("Accept-Encoding", "gzip, deflate")
                                        .addHeader("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                                        .addHeader("Cookie", cookies)
                                        .build()
                                    val gameResponse = com.abclient.data.AuthManager.client.newCall(gameRequest).execute()
                                    val bytes = gameResponse.body?.bytes()
                                    if (bytes != null) com.abclient.cache.FileCache.put(this@MainActivity, cacheKey, bytes)
                                    val contentEncoding = gameResponse.header("Content-Encoding")
                                    val transferEncoding = gameResponse.header("Transfer-Encoding")
                                    val contentType = gameResponse.header("Content-Type")
                                    val decoded = bytes?.let { com.abclient.filter.ResponseDecoder.decode(it, contentEncoding, transferEncoding, contentType) } ?: ""
                                    gameResponse.close()
                                    decoded to false
                                }
                                val urlLower = cacheKey.lowercase()
                                val gameBody = when {
                                    urlLower.contains("game.php") -> com.abclient.filter.GamePhpFilter.filter(rawHtml)
                                    urlLower.contains("main.php") -> com.abclient.filter.MainPhpFilter.filter(rawHtml)
                                    urlLower.endsWith(".js") -> com.abclient.filter.JsFilter.filter(rawHtml)
                                    urlLower.contains("msg.php") -> com.abclient.filter.MsgPhpFilter.filter(rawHtml)
                                    else -> com.abclient.filter.GameHtmlFilter.filter(rawHtml)
                                }
                                val file = java.io.File(filesDir, "game.html")
                                if (gameBody.isBlank() || !gameBody.trimStart().startsWith("<")) {
                                    runOnUiThread {
                                        tv.text = "Ошибка: не удалось декодировать HTML. Файл не записан."
                                        btn.isEnabled = true
                                    }
                                    return@launch
                                }
                                file.writeText(gameBody)
                                runOnUiThread {
                                    // Показываем путь к файлу и активируем кнопку
                                    tvGameHtmlPath.text = "Путь к game.html: ${file.absolutePath}"
                                    btnOpenGameHtml.isEnabled = true
                                    btnOpenGameHtml.setOnClickListener {
                                        try {
                                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                                            intent.setDataAndType(android.net.Uri.fromFile(file), "text/html")
                                            intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            startActivity(intent)
                                        } catch (e: Exception) {
                                            android.widget.Toast.makeText(this@MainActivity, "Ошибка открытия файла: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                                        }
                                    }
                                    com.abclient.data.AuthManager.startSessionHeartbeat(this@MainActivity, profile)
                                    val intent = android.content.Intent(this@MainActivity, FormMain::class.java)
                                    intent.putExtra("profile", profile)
                                    intent.putExtra("cookies", cookies)
                                    startActivity(intent)
                                    finish()
                                }
                            } catch (e: Exception) {
                                runOnUiThread {
                                    tv.text = "Ошибка запроса game.php: ${e.message}"
                                    btn.isEnabled = true
                                }
                            }
                        }
                    } else {
                        val msg = when (status) {
                            1 -> "Неверный логин или пароль"
                            2 -> "Требуется капча! Зайдите через браузер"
                            3 -> "Ошибка сети: $errorMsg"
                            else -> "Неизвестная ошибка"
                        }
                        tv.text = msg + "\nОтвет сервера:\n" + (body ?: "нет данных") +
                            "\n[GET] Статус-код: ${getStatus ?: "нет"}\n[GET] Тело: ${getBody ?: "нет"}" +
                            "\n[POST] Статус-код: ${postStatus ?: "нет"}\n[POST] Тело: ${postBody ?: "нет"}"
                    }
                }
            }, this)
        }

        // 1. GET neverlands.ru (имитация открытия окна)
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val request = okhttp3.Request.Builder()
                    .url("http://neverlands.ru/")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.7258.138 Safari/537.36")
                    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .addHeader("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                    .build()
                val response = com.abclient.data.AuthManager.client.newCall(request).execute()
                val body = response.body?.string() ?: ""
                response.close()
                runOnUiThread {
                    tv.text = "Страница загружена. Нажмите Войти."
                    btn.isEnabled = true
                }
            } catch (e: Exception) {
                runOnUiThread { tv.text = "Ошибка загрузки: ${e.message}" }
            }
        }
    }
}
