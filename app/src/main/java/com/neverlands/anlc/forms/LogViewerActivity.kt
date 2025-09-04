package com.neverlands.anlc.forms

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity // Use AppCompatActivity for consistency

import com.neverlands.anlc.R

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class LogViewerActivity : AppCompatActivity() { // Extend AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_viewer)

        val logContentView = findViewById<TextView>(R.id.logContentView)
        val filename = intent.getStringExtra("log_filename")

        if (filename != null) {
            title = filename
            val logFile = File(File(filesDir, "logs"), filename)
            try {
                val fis = FileInputStream(logFile)
                val reader = BufferedReader(InputStreamReader(fis))
                val sb = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line).append('\n')
                }
                reader.close()
                fis.close()
                logContentView.text = sb.toString()
            } catch (e: Exception) {
                logContentView.text = "Error reading log file: " + e.message
            }
        } else {
            logContentView.text = "No log file specified."
        }
    }
}
