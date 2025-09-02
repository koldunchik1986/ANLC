package com.abclient.cache

import android.content.Context
import java.io.File

object FileCache {
    // Получить кэшированный ответ по URL
    fun get(context: Context, url: String): ByteArray? {
        val file = File(context.cacheDir, urlToFileName(url))
        return if (file.exists()) file.readBytes() else null
    }

    // Сохранить ответ в кэш
    fun put(context: Context, url: String, data: ByteArray) {
        val file = File(context.cacheDir, urlToFileName(url))
        file.writeBytes(data)
    }

    // Преобразовать URL в имя файла
    private fun urlToFileName(url: String): String {
        return url.replace(Regex("[^a-zA-Z0-9._-]"), "_")
    }

    // Очистить кэш
    fun clear(context: Context) {
        context.cacheDir.listFiles()?.forEach { it.delete() }
    }
}
