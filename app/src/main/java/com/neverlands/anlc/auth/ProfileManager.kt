package com.neverlands.anlc.auth

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object ProfileManager {

    data class Profile(
        val login: String,
        val password: String,
        val autologin: Boolean,
        val useProxy: Boolean,
        val autoClearCookies: Boolean
    )

    private var profiles = mutableListOf<Profile>()
    private lateinit var file: File

    fun init(filesDir: File) {
        file = File(filesDir, "profiles.json")
        if (file.exists()) {
            val json = file.readText()
            val type = object : TypeToken<List<Profile>>() {}.type
            profiles = Gson().fromJson(json, type)
        }
    }

    fun getProfiles(): List<Profile> {
        return profiles
    }

    fun addProfile(profile: Profile) {
        profiles.add(profile)
        saveProfiles()
    }

    fun removeProfile(profile: Profile) {
        profiles.remove(profile)
        saveProfiles()
    }

    fun updateProfile(oldProfile: Profile, newProfile: Profile) {
        val index = profiles.indexOf(oldProfile)
        if (index != -1) {
            profiles[index] = newProfile
            saveProfiles()
        }
    }

    private fun saveProfiles() {
        val json = Gson().toJson(profiles)
        file.writeText(json)
    }
}
