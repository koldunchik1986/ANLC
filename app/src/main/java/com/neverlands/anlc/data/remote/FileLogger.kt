package com.neverlands.anlc.data.remote

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileLogger {
    fun log(context: Context, text: String) {
        try {
            val file = File(context.filesDir, "auth_log.txt")
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            file.appendText("[$timestamp] $text\n\n")
        } catch (e: Exception) {
            // Ignore exceptions during logging
        }
    }
}


