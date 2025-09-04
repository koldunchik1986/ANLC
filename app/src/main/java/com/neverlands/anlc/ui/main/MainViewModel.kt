package com.neverlands.anlc.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.neverlands.anlc.data.local.ProfileManager
import com.neverlands.anlc.data.remote.AuthRepository
import com.neverlands.anlc.ui.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для главного экрана игры.
 * Отвечает за логику поддержания сессии и обновления UI.
 */
class MainViewModel : BaseViewModel() {

    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = _currentTime

    private val _reloadUrlEvent = MutableSharedFlow<String>()
    val reloadUrlEvent = _reloadUrlEvent.asSharedFlow()

    private var heartbeatJob: Job? = null

    /**
     * Запускает "пульс" сессии.
     */
    fun startHeartbeat() {
        ProfileManager.getCurrentProfile()?.let { profile ->
            // Start the session heartbeat from AuthRepository
            AuthRepository.startSessionHeartbeat { time ->
                _currentTime.postValue(time)
            }

            // Start periodic WebView reloads
            heartbeatJob?.cancel() // Cancel any existing job
            heartbeatJob = viewModelScope.launch {
                while (true) {
                    _reloadUrlEvent.emit("http://neverlands.ru/main.php")
                    delay(60000) // Reload every 60 seconds (adjust as needed)
                }
            }
        }
    }

    /**
     * Останавливает "пульс" сессии.
     */
    fun stopHeartbeat() {
        AuthRepository.stopSessionHeartbeat()
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

    /**
     * Выполняет выход из системы.
     */
    fun logout() {
        AuthRepository.logout()
        ProfileManager.setCurrentProfile(null) // Очищаем текущий профиль
    }
}