
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
        setContentView(layout)

        btn.setOnClickListener {
            tv.text = "Авторизация..."
            btn.isEnabled = false
            com.abclient.data.AuthManager.authorizeAsync(profile, password, { status, errorMsg, body ->
                runOnUiThread {
                    btn.isEnabled = true
                    when (status) {
                        0 -> {
                            tv.text = "Вход успешен!" // Здесь можно запускать heartbeat и UI
                            com.abclient.data.AuthManager.startSessionHeartbeat(this, profile)
                        }
                        else -> {
                            val msg = when (status) {
                                1 -> "Неверный логин или пароль"
                                2 -> "Требуется капча! Зайдите через браузер"
                                3 -> "Ошибка сети: $errorMsg"
                                else -> "Неизвестная ошибка"
                            }
                            tv.text = msg + "\nОтвет сервера:\n" + (body ?: "нет данных")
                        }
                    }
                }
            }, this)
        }

        // 1. GET neverlands.ru (имитация открытия окна)
        GlobalScope.launch(Dispatchers.IO) {
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
