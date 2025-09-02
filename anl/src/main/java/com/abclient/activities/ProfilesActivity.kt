package com.abclient.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.view.View
import android.content.Intent
import com.abclient.R

class ProfilesActivity : AppCompatActivity() {
    private lateinit var listProfiles: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var selectedProfile: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profiles_activity)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        val btnAdd = findViewById<ImageButton>(R.id.btn_add_profile)
        val btnSend = findViewById<ImageButton>(R.id.btn_send_profile)
        val btnView = findViewById<ImageButton>(R.id.btn_view_profile)
        val btnEdit = findViewById<ImageButton>(R.id.btn_edit_profile)
        val btnDelete = findViewById<ImageButton>(R.id.btn_delete_profile)
    val btnLogin = findViewById<Button>(R.id.btn_login)
    listProfiles = findViewById(R.id.list_profiles)
        btnLogin.setOnClickListener {
            val profileName = selectedProfile
            if (profileName == null) {
                Toast.makeText(this, "Выберите профиль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val password = com.abclient.data.ProfileManager.getPassword(profileName) ?: ""
            val autologin = com.abclient.data.ProfileManager.getAutologin(profileName) ?: false
            val useProxy = com.abclient.data.ProfileManager.getUseProxy(profileName) ?: false
            val autoClearCookies = com.abclient.data.ProfileManager.getAutoClearCookies(profileName) ?: false
            val cookies = com.abclient.data.ProfileManager.getCookies(profileName) ?: ""
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("profile", profileName)
            intent.putExtra("password", password)
            intent.putExtra("autologin", autologin)
            intent.putExtra("useProxy", useProxy)
            intent.putExtra("autoClearCookies", autoClearCookies)
            intent.putExtra("cookies", cookies)
            startActivity(intent)
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, getProfileFileNames())
        listProfiles.adapter = adapter
        listProfiles.choiceMode = ListView.CHOICE_MODE_SINGLE

        listProfiles.setOnItemClickListener { _, _, position, _ ->
            selectedProfile = adapter.getItem(position)
        }

        btnAdd.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }
        btnSend.setOnClickListener {
            val profileName = selectedProfile
            if (profileName == null) {
                Toast.makeText(this, "Выберите профиль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val file = java.io.File(filesDir, "login_${profileName}.txt")
            if (!file.exists()) {
                Toast.makeText(this, "Файл профиля не найден", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val uri = androidx.core.content.FileProvider.getUriForFile(
                this,
                packageName + ".provider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(intent, "Отправить профиль"))
        }
        btnView.setOnClickListener {
            val profileName = selectedProfile
            if (profileName == null) {
                Toast.makeText(this, "Выберите профиль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val file = java.io.File(filesDir, "login_${profileName}.txt")
            val text = if (file.exists()) file.readText() else "Файл профиля не найден"
            android.app.AlertDialog.Builder(this)
                .setTitle("Профиль: $profileName")
                .setMessage(text)
                .setPositiveButton("OK", null)
                .show()
        }
        btnEdit.setOnClickListener {
            val profileName = selectedProfile
            if (profileName == null) {
                Toast.makeText(this, "Выберите профиль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, ProfileEditActivity::class.java)
            intent.putExtra("profile", profileName)
            startActivity(intent)
        }
        btnDelete.setOnClickListener {
            val profileName = selectedProfile
            if (profileName == null) {
                Toast.makeText(this, "Выберите профиль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val file = java.io.File(filesDir, "login_${profileName}.txt")
            if (file.exists()) file.delete()
            adapter.clear()
            adapter.addAll(getProfileFileNames())
            adapter.notifyDataSetChanged()
            selectedProfile = null
            Toast.makeText(this, "Профиль удалён", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onResume() {
        super.onResume()
        adapter.clear()
        adapter.addAll(getProfileFileNames())
        adapter.notifyDataSetChanged()
        selectedProfile = null
    }
    private fun getProfileFileNames(): List<String> {
        val files = filesDir.listFiles()
        return files?.filter { it.name.startsWith("login_") && it.name.endsWith(".txt") }
            ?.map { it.name.removePrefix("login_").removeSuffix(".txt") } ?: emptyList()
    }
}
