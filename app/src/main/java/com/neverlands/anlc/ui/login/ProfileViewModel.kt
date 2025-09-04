package com.neverlands.anlc.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.neverlands.anlc.data.local.ProfileManager
import com.neverlands.anlc.data.local.model.Profile
import com.neverlands.anlc.data.remote.AuthRepository
import com.neverlands.anlc.ui.base.BaseViewModel
import com.neverlands.anlc.util.CryptoHelper
import com.neverlands.anlc.data.remote.AuthResult
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана входа и управления профилями.
 * Отвечает за логику загрузки, сохранения, шифрования/дешифрования профилей и авторизации.
 */
class ProfileViewModel : BaseViewModel() {

    private val _profiles = MutableLiveData<List<Profile>>()
    val profiles: LiveData<List<Profile>> = _profiles

    private val _authResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> = _authResult

    /**
     * Загружает список профилей.
     */
    fun loadProfiles() {
        _profiles.value = ProfileManager.getProfiles()
    }

    /**
     * Авторизует пользователя.
     * @param profile Профиль для авторизации.
     * @param password Пароль для расшифровки данных профиля.
     */
    fun authorize(profile: Profile, password: String) {
        viewModelScope.launch {
            AuthRepository.authorize(profile, password) { result ->
                _authResult.value = result
            }
        }
    }

    /**
     * Добавляет или обновляет профиль.
     * @param profile Профиль для добавления/обновления.
     * @param rawPassword Нешифрованный пароль для шифрования.
     * @param rawPasswordFlash Нешифрованный флеш-пароль для шифрования.
     * @param configPassword Пароль для шифрования конфигурации.
     */
    fun addOrUpdateProfile(
        profile: Profile,
        rawPassword: String,
        rawPasswordFlash: String,
        configPassword: String
    ) {
        viewModelScope.launch {
            profile.encryptedPassword = CryptoHelper.encryptString(rawPassword, configPassword)
            profile.encryptedPasswordFlash = CryptoHelper.encryptString(rawPasswordFlash, configPassword)
            profile.configPasswordHash = CryptoHelper.passwordToHash(configPassword)
            ProfileManager.addProfile(profile)
            loadProfiles() // Обновляем список после сохранения
        }
    }

    /**
     * Удаляет профиль.
     * @param profile Профиль для удаления.
     */
    fun removeProfile(profile: Profile) {
        viewModelScope.launch {
            ProfileManager.removeProfile(profile)
            loadProfiles() // Обновляем список после удаления
        }
    }
}
