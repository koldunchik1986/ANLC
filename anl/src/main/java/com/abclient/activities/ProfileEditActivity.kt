package com.abclient.activities

import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.abclient.R
import com.abclient.data.ProfileManager

// Окно добавления/редактирования профиля
class ProfileEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_edit_activity)

        val editName = findViewById<EditText>(R.id.edit_profile_name)
        val editPassword = findViewById<EditText>(R.id.edit_profile_password)
        val checkboxAutologin = findViewById<android.widget.CheckBox>(R.id.checkbox_autologin)
        val checkboxProxy = findViewById<android.widget.CheckBox>(R.id.checkbox_proxy)
        val checkboxAutoClearCookies = findViewById<android.widget.CheckBox>(R.id.checkbox_auto_clear_cookies)
        val btnSave = findViewById<Button>(R.id.btn_save_profile)
        val btnCheck = findViewById<Button>(R.id.btn_check_connection)

        btnCheck.setOnClickListener {
            val name = editName.text.toString().trim()
            // Очищаем cookies для текущего профиля перед авторизацией
            try {
                val url = okhttp3.HttpUrl.Builder().scheme("http").host("neverlands.ru").build()
                (com.abclient.data.AuthManager.client.cookieJar as? com.abclient.data.AuthManager.SimpleCookieJar)?.saveFromResponse(url, emptyList())
                com.abclient.data.AuthManager.profileCookies[name] = emptyList()
            } catch (_: Exception) {}
            val password = editPassword.text.toString()
            val autologin = checkboxAutologin.isChecked
            val useProxy = checkboxProxy.isChecked
            val autoClearCookies = checkboxAutoClearCookies.isChecked
            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Введите имя и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // ...cookies не очищаем при проверке соединения...
            Toast.makeText(this, "Проверка соединения...", Toast.LENGTH_SHORT).show()
            // Используем единый User-Agent для всех запросов
            val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36"
            com.abclient.data.AuthManager.authorizeAsync(name, password,
                { status: Int, mainBody: String?, errorMsg: String?, getStatus: Int?, getBody: String?, postStatus: Int?, postBody: String? ->
                    runOnUiThread {
                        val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
                        val logsDir = java.io.File(this.filesDir, "Logs/Network")
                        if (!logsDir.exists()) logsDir.mkdirs()
                        val logFile = java.io.File(logsDir, "Log_${name}_$timestamp.txt")
                        val url = okhttp3.HttpUrl.Builder().scheme("http").host("neverlands.ru").build()
                        val cookiesList = com.abclient.data.AuthManager.client.cookieJar.loadForRequest(url)
                        val cookies = cookiesList.joinToString("; ") { it.name + "=" + it.value }
                        val logText = StringBuilder()
                        logText.append("Профиль: $name\nПароль: $password\nAutologin: $autologin\nProxy: $useProxy\nAutoClearCookies: $autoClearCookies\n")
                        logText.append("Статус: $status\nОшибка: ${errorMsg ?: "нет"}\n")
                        // ...логирование GET/POST, как было...
                        try {
                            logFile.writeText(logText.toString())
                            Toast.makeText(this, "Лог-файл создан: Log_${name}_$timestamp.txt", Toast.LENGTH_SHORT).show()
                        } catch (_: Exception) {}
                        when (status) {
                            0 -> {
                                // Сохраняем профиль и cookies
                                com.abclient.data.ProfileManager.removeProfile(name, this)
                                com.abclient.data.ProfileManager.addProfile(name, password, autologin, useProxy, autoClearCookies, this, cookies)
                                try {
                                    val loginFile = java.io.File(this.filesDir, "login_${name}.txt")
                                    val profileData = "Имя: $name\nПароль: $password\nAutologin: $autologin\nProxy: $useProxy\nAutoClearCookies: $autoClearCookies\nCookies: $cookies"
                                    loginFile.writeText(profileData)
                                } catch (_: Exception) {}
                                Toast.makeText(this, "Профиль и cookies сохранены! Сессия завершена.", Toast.LENGTH_LONG).show()
                                finish()
                            }
                            1 -> Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_LONG).show()
                            2 -> Toast.makeText(this, "Требуется капча!", Toast.LENGTH_LONG).show()
                            else -> Toast.makeText(this, "Ошибка: $errorMsg", Toast.LENGTH_LONG).show()
                        }
                    }
                }, this)
        }

        btnSave.setOnClickListener {
            val name = editName.text.toString().trim()
            val password = editPassword.text.toString()
            val autologin = checkboxAutologin.isChecked
            val useProxy = checkboxProxy.isChecked
            val autoClearCookies = checkboxAutoClearCookies.isChecked
            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Введите имя и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ProfileManager.addProfile(name, password, autologin, useProxy, autoClearCookies, this)
            try {
                val loginFile = java.io.File(this.filesDir, "login_${name}.txt")
                val profileData = "Имя: $name\nПароль: $password\nAutologin: $autologin\nProxy: $useProxy\nAutoClearCookies: $autoClearCookies\nCookies: "
                loginFile.writeText(profileData)
            } catch (_: Exception) {}
            Toast.makeText(this, "Профиль сохранён", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
