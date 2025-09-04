package com.abclient.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.AdapterView
import com.abclient.R
import com.abclient.data.ProfileManager

// Главное окно приветствия
// Кнопки: Добавить профиль, Настройки, Вход в игру
// Список профилей для выбора
class WelcomeActivity : AppCompatActivity() {

    private lateinit var btnAddProfile: Button
    private lateinit var btnSettings: Button
    private lateinit var btnLogin: Button
    private lateinit var btnExportProfiles: Button
    private lateinit var btnImportProfiles: Button
    private lateinit var btnViewLog: Button
    private lateinit var listProfiles: ListView
    private var selectedProfile: String? = null
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)
        // Очистить лог при запуске
        try {
            val file = java.io.File(this.filesDir, "log.txt")
            if (file.exists()) file.writeText("")
        } catch (_: Exception) {}

    btnAddProfile = findViewById(R.id.btn_add_profile)
    btnSettings = findViewById(R.id.btn_settings)
    btnLogin = findViewById(R.id.btn_login)
    btnExportProfiles = findViewById(R.id.btn_export_profiles)
    btnImportProfiles = findViewById(R.id.btn_import_profiles)
    btnViewLog = findViewById(R.id.btn_view_log)
    listProfiles = findViewById(R.id.list_profiles)
        btnExportProfiles.setOnClickListener {
            val ok = com.abclient.data.ProfileManager.exportProfiles(this)
            val msg = if (ok) "Профили экспортированы в profiles.txt" else "Ошибка экспорта профилей"
            android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
        }

        btnImportProfiles.setOnClickListener {
            val ok = com.abclient.data.ProfileManager.importProfiles(this)
            val msg = if (ok) "Профили импортированы из profiles.txt" else "Ошибка импорта профилей"
            android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
            // Обновить список профилей
            adapter.clear()
            adapter.addAll(com.abclient.data.ProfileManager.getProfileNames())
            adapter.notifyDataSetChanged()
        }

        btnViewLog.setOnClickListener {
            try {
                val file = java.io.File(this.filesDir, "log.txt")
                val log = if (file.exists()) file.readText() else "Лог пуст"
                val intent = android.content.Intent(android.content.Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(android.content.Intent.EXTRA_TEXT, log)
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ABClient log.txt")
                startActivity(android.content.Intent.createChooser(intent, "Отправить лог"))
            } catch (_: Exception) {
                android.widget.Toast.makeText(this, "Ошибка открытия лога", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, ProfileManager.getProfileNames())
        listProfiles.adapter = adapter
        listProfiles.choiceMode = ListView.CHOICE_MODE_SINGLE

        listProfiles.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            selectedProfile = adapter.getItem(position)
            btnLogin.isEnabled = true
        }

        btnLogin.isEnabled = false

        btnAddProfile.setOnClickListener {
            val intent = android.content.Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }
        btnSettings.setOnClickListener {
            val intent = android.content.Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            val profile = selectedProfile
            if (profile == null) {
                android.widget.Toast.makeText(this, "Выберите профиль", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = android.content.Intent(this, MainActivity::class.java)
            intent.putExtra("profile", profile)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Обновить список профилей после возврата с ProfileEditActivity
        adapter.clear()
        adapter.addAll(ProfileManager.getProfileNames())
        adapter.notifyDataSetChanged()
        selectedProfile = null
        btnLogin.isEnabled = false
    }
}
