package com.abclient.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.abclient.R

// Главное окно приветствия
// Кнопки: Добавить профиль, Настройки, Вход в игру
// Список профилей для выбора
class WelcomeActivity : AppCompatActivity() {
    private lateinit var btnProfiles: Button
    private lateinit var btnSettings: Button
    private lateinit var btnViewLog: Button
    private lateinit var btnLogs: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)

    btnProfiles = findViewById(R.id.btn_profiles)
    btnSettings = findViewById(R.id.btn_settings)
    btnViewLog = findViewById(R.id.btn_view_log)
    btnLogs = findViewById(R.id.btn_logs)

        btnProfiles.setOnClickListener {
            val intent = android.content.Intent(this, ProfilesActivity::class.java)
            startActivity(intent)
        }
        btnSettings.setOnClickListener {
            val intent = android.content.Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        btnViewLog.setOnClickListener {
            try {
                val file = java.io.File(this.filesDir, "log.txt")
                if (!file.exists()) {
                    android.widget.Toast.makeText(this, "Файл log.txt не найден", android.widget.Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val uri = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    packageName + ".provider",
                    file
                )
                val intent = android.content.Intent(android.content.Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(android.content.Intent.EXTRA_STREAM, uri)
                intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(android.content.Intent.createChooser(intent, "Отправить лог"))
            }
            catch (e: Exception) {
                android.widget.Toast.makeText(this, "Ошибка открытия лога: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
        btnLogs.setOnClickListener {
            val intent = android.content.Intent(this, LogsActivity::class.java)
            startActivity(intent)
        }
    }
}
