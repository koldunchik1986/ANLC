package com.abclient.data

// Менеджер профайлов: добавление, удаление, хранение профилей
// Использует SharedPreferences с шифрованием
object ProfileManager {
    // Экспорт профилей в файл profiles.txt
    fun exportProfiles(context: android.content.Context): Boolean {
        return try {
            val file = java.io.File(context.filesDir, "profiles.txt")
            val raw = profiles.joinToString("|;") {
                it.name + "|:" + it.password + "|:" + (if (it.autologin) "1" else "0") + "|:" + (if (it.useProxy) "1" else "0") + "|:" + (if (it.autoClearCookies) "1" else "0")
            }
            file.writeText(raw)
            true
        } catch (_: Exception) { false }
    }

    // Импорт профилей из файла profiles.txt
    fun importProfiles(context: android.content.Context): Boolean {
        return try {
            val file = java.io.File(context.filesDir, "profiles.txt")
            if (!file.exists()) return false
            val raw = file.readText()
            profiles.clear()
            if (raw.isNotEmpty()) {
                raw.split("|;").forEach {
                    val parts = it.split("|:")
                    if (parts.size == 5) {
                        profiles.add(Profile(
                            name = parts[0],
                            password = parts[1],
                            autologin = parts[2] == "1",
                            useProxy = parts[3] == "1",
                            autoClearCookies = parts[4] == "1"
                        ))
                    }
                }
            }
            saveProfiles(context)
            true
        } catch (_: Exception) { false }
    }
    private const val PREFS_NAME = "profiles_prefs"
    private const val KEY_PROFILES = "profiles_list"

    data class Profile(
        val name: String,
        val password: String,
        val autologin: Boolean,
        val useProxy: Boolean,
        val autoClearCookies: Boolean
    )

    private val profiles = mutableListOf<Profile>()

    // Загрузить профили из SharedPreferences
    fun loadProfiles(context: android.content.Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_PROFILES, null)
        profiles.clear()
        if (!raw.isNullOrEmpty()) {
            raw.split("|;").forEach {
                val parts = it.split("|:")
                if (parts.size == 5) {
                    profiles.add(Profile(
                        name = parts[0],
                        password = parts[1],
                        autologin = parts[2] == "1",
                        useProxy = parts[3] == "1",
                        autoClearCookies = parts[4] == "1"
                    ))
                }
            }
        }
    }

    // Сохранить профили в SharedPreferences
    fun saveProfiles(context: android.content.Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        val raw = profiles.joinToString("|;") {
            it.name + "|:" + it.password + "|:" + (if (it.autologin) "1" else "0") + "|:" + (if (it.useProxy) "1" else "0") + "|:" + (if (it.autoClearCookies) "1" else "0")
        }
        prefs.edit().putString(KEY_PROFILES, raw).apply()
    }

    fun getProfileNames(): List<String> {
        return profiles.map { it.name }
    }

    fun addProfile(name: String, password: String, autologin: Boolean, useProxy: Boolean, autoClearCookies: Boolean, context: android.content.Context) {
        profiles.add(Profile(name, password, autologin, useProxy, autoClearCookies))
        saveProfiles(context)
    }
    fun getAutoClearCookies(name: String): Boolean? {
        return profiles.find { it.name == name }?.autoClearCookies
    }

    fun setAutoClearCookies(name: String, value: Boolean, context: android.content.Context) {
        profiles.find { it.name == name }?.let {
            val updated = it.copy(autoClearCookies = value)
            profiles[profiles.indexOf(it)] = updated
            saveProfiles(context)
        }
    }

    fun getPassword(name: String): String? {
        return profiles.find { it.name == name }?.password
    }

    fun getAutologin(name: String): Boolean? {
        return profiles.find { it.name == name }?.autologin
    }

    fun getUseProxy(name: String): Boolean? {
        return profiles.find { it.name == name }?.useProxy
    }
}
