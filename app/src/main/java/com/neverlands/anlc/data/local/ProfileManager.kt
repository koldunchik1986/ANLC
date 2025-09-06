package com.neverlands.anlc.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neverlands.anlc.data.local.model.Profile
import java.io.File

/**
 * Управляет профилями пользователей: загрузка, сохранение, поиск.
 * Хранит все профили в одном JSON-файле.
 */
object ProfileManager {

    private lateinit var profilesFile: File
    private val gson = Gson()
    private var profiles: MutableList<Profile> = mutableListOf()
    private var currentProfile: Profile? = null

    /**
     * Инициализирует менеджер профилей.
     * @param context Контекст приложения для доступа к файловому хранилищу.
     */
    fun init(context: Context) {
        profilesFile = File(context.filesDir, "profiles.json")
        loadProfiles()
    }

    /**
     * Загружает профили из JSON-файла.
     */
    private fun loadProfiles() {
        if (profilesFile.exists()) {
            val json = profilesFile.readText()
            val type = object : TypeToken<MutableList<Profile>>() {}.type
            profiles = gson.fromJson(json, type) ?: mutableListOf()
        }
    }

    /**
     * Сохраняет текущий список профилей в JSON-файл.
     */
    private fun saveProfiles() {
        val json = gson.toJson(profiles)
        profilesFile.writeText(json)
    }

    /**
     * Возвращает список всех профилей.
     * @return Список профилей.
     */
    fun getProfiles(): List<Profile> {
        return profiles
    }

    /**
     * Находит профиль по логину.
     * @param login Логин для поиска.
     * @return Найденный профиль или null.
     */
    fun getProfileByLogin(login: String): Profile? {
        return profiles.find { it.userNick.equals(login, ignoreCase = true) }
    }

    /**
     * Устанавливает текущий активный профиль.
     * @param profile Профиль, который становится активным.
     */
    fun setCurrentProfile(profile: Profile?) {
        currentProfile = profile
    }

    /**
     * Возвращает текущий активный профиль.
     * @return Текущий профиль или null.
     */
    fun getCurrentProfile(): Profile? {
        return currentProfile
    }

    /**
     * Добавляет новый профиль и сохраняет список.
     * @param profile Новый профиль.
     */
    fun addProfile(profile: Profile) {
        // Удаляем старый профиль с таким же ником, если он есть
        profiles.removeAll { it.userNick.equals(profile.userNick, ignoreCase = true) }
        profiles.add(profile)
        saveProfiles()
    }

    /**
     * Удаляет профиль и сохраняет список.
     * @param profile Профиль для удаления.
     */
    fun removeProfile(profile: Profile) {
        profiles.remove(profile)
        saveProfiles()
    }

    /**
     * Сохраняет текущий активный профиль.
     */
    fun saveCurrentProfile() {
        currentProfile?.let { profile ->
            // Find and replace the old profile with the updated currentProfile
            val index = profiles.indexOfFirst { it.userNick.equals(profile.userNick, ignoreCase = true) }
            if (index != -1) {
                profiles[index] = profile
            } else {
                // This case should ideally not happen if currentProfile is always from the loaded list
                // But as a fallback, add it if not found
                profiles.add(profile)
            }
            saveProfiles()
        }
    }
}