package com.abclient.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.view.View
import android.content.Intent
import com.abclient.R
import java.io.File

class LogsActivity : AppCompatActivity() {
    private lateinit var listLogs: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var selectedLog: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.logs_activity)

        listLogs = findViewById(R.id.list_logs)
        val btnView = findViewById<ImageButton>(R.id.btn_view_log)
        val btnSend = findViewById<ImageButton>(R.id.btn_send_log)
        val btnDelete = findViewById<ImageButton>(R.id.btn_delete_log)
        val btnBack = findViewById<ImageButton>(R.id.btn_back)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, getLogFileNames())
        listLogs.adapter = adapter
        listLogs.choiceMode = ListView.CHOICE_MODE_SINGLE

        listLogs.setOnItemClickListener { _, _, position, _ ->
            selectedLog = adapter.getItem(position)
        }

        btnView.setOnClickListener {
            val logName = selectedLog
            if (logName == null) {
                Toast.makeText(this, "Выберите лог", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val file = File(getLogsDir(), logName)
            val text = if (file.exists()) file.readText() else "Файл лога не найден"
            android.app.AlertDialog.Builder(this)
                .setTitle("Лог: $logName")
                .setMessage(text)
                .setPositiveButton("OK", null)
                .show()
        }
        btnSend.setOnClickListener {
            val logName = selectedLog
            if (logName == null) {
                Toast.makeText(this, "Выберите лог", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val file = File(getLogsDir(), logName)
            if (!file.exists()) {
                Toast.makeText(this, "Файл лога не найден", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val uri = androidx.core.content.FileProvider.getUriForFile(
                this,
                packageName + ".provider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(intent, "Отправить лог"))
        }
        btnDelete.setOnClickListener {
            val logName = selectedLog
            if (logName == null) {
                Toast.makeText(this, "Выберите лог", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val file = File(getLogsDir(), logName)
            if (file.exists()) file.delete()
            adapter.clear()
            adapter.addAll(getLogFileNames())
            adapter.notifyDataSetChanged()
            selectedLog = null
            Toast.makeText(this, "Лог удалён", Toast.LENGTH_SHORT).show()
        }
        btnBack.setOnClickListener {
            finish()
        }
    }
    override fun onResume() {
    super.onResume()
    adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, getLogFileNames())
    listLogs.adapter = adapter
    selectedLog = null
    }
    private fun getLogsDir(): File {
    val dir = File(filesDir, "Logs/Network")
    if (!dir.exists()) dir.mkdirs()
    return dir
    }
    private fun getLogFileNames(): List<String> {
        val dir = getLogsDir()
        val files = dir.listFiles()
        return files?.filter { it.isFile && it.name.startsWith("Log_") && it.name.endsWith(".txt") }
            ?.map { it.name } ?: emptyList()
    }
}
