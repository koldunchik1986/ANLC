package com.neverlands.anlc.data.local.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize // Add this import

/**
 * Модель данных для профиля пользователя.
 * Хранит всю необходимую информацию для входа и конфигурации.
 * @param userNick Игровой ник.
 * @param encryptedPassword Зашифрованный пароль для входа в игру.
 * @param encryptedPasswordFlash Зашифрованный пароль для флеш-запросов (если отличается).
 * @param useAutoLogon Флаг автоматического входа.
 * @param useProxy Флаг использования прокси.
 * @param autoClearCookies Флаг автоматической очистки кук перед входом.
 * @param configPasswordHash MD5-хеш пароля от конфигурации (для проверки правильности пароля).
 */
@Parcelize // Add this annotation
data class Profile(
    val userNick: String,
    var encryptedPassword: String = "",
    var encryptedPasswordFlash: String = "",
    var useAutoLogon: Boolean = false,
    var useProxy: Boolean = true,
    var autoClearCookies: Boolean = true,
    var configPasswordHash: String? = null
) : Parcelable // Implement Parcelable