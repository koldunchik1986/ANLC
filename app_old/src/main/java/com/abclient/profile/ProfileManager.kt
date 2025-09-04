package com.abclient.profile

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class UserProfile(
    val login: String,
    val created: String,
    val cookies: Map<String, String> = emptyMap(),
    val lastUsed: String = ""
)

object ProfileManager {
    private const val FILE_NAME = "profiles.json"
    private val json = Json { prettyPrint = true }

    fun getProfiles(context: Context): List<UserProfile> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()
        return try {
            json.decodeFromString(file.readText())
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveProfiles(context: Context, profiles: List<UserProfile>) {
        val file = File(context.filesDir, FILE_NAME)
        file.writeText(json.encodeToString(profiles))
    }

    fun addProfile(context: Context, profile: UserProfile) {
        val profiles = getProfiles(context).toMutableList()
        profiles.removeAll { it.login == profile.login }
        profiles.add(profile)
        saveProfiles(context, profiles)
    }

    fun removeProfile(context: Context, login: String) {
        val profiles = getProfiles(context).filter { it.login != login }
        saveProfiles(context, profiles)
    }
}
