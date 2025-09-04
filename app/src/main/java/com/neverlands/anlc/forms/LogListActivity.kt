package com.neverlands.anlc.forms

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity // Use AppCompatActivity for consistency

import com.neverlands.anlc.R

import java.io.File
import java.util.ArrayList
import java.util.Collections
import java.util.List

class LogListActivity : AppCompatActivity() { // Extend AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_list)

        val logListView = findViewById<ListView>(R.id.logListView)

        val logDir = File(filesDir, "logs")
        if (!logDir.exists()) {
            logDir.mkdir()
        }
        val files = logDir.listFiles { dir, name -> name.startsWith("log-") && name.endsWith(".txt") }

        val logFiles = ArrayList<String>()
        if (files != null) {
            for (file in files) {
                logFiles.add(file.name)
            }
        }
        Collections.sort(logFiles, Collections.reverseOrder())

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, logFiles)
        logListView.adapter = adapter

        logListView.setOnItemClickListener { parent, view, position, id ->
            val filename = parent.getItemAtPosition(position) as String
            val intent = Intent(this@LogListActivity, LogViewerActivity::class.java)
            intent.putExtra("log_filename", filename)
            startActivity(intent)
        }
    }
}