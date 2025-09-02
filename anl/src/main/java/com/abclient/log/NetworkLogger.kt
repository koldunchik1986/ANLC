package com.abclient.log

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object NetworkLogger {
    private fun getLogsDir(context: Context): File {
        val dir = File(context.filesDir, "Logs/Network")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }
    fun log(context: Context, text: String) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = sdf.format(Date())
        val file = File(getLogsDir(context), "Log_network.txt")
        file.appendText("[$timestamp] $text\n")
    }
}
