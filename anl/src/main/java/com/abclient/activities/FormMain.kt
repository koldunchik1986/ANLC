package com.abclient.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.abclient.R

class FormMain : AppCompatActivity() {
    private lateinit var toolbarGame: Toolbar
    private lateinit var tabControlLeft: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var statuslabelClock: TextView
    private lateinit var statuslabelLocation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_main)

        toolbarGame = findViewById(R.id.toolbarGame)
        setSupportActionBar(toolbarGame)

        tabControlLeft = findViewById(R.id.tabControlLeft)
        viewPager = findViewById(R.id.viewPager)
        statuslabelClock = findViewById(R.id.statuslabelClock)
        statuslabelLocation = findViewById(R.id.statuslabelLocation)

        val tabTitles = listOf("Игра", "Чат", "Инвентарь", "Контакты")
        viewPager.adapter = FormMainPagerAdapter(this, tabTitles)
        TabLayoutMediator(tabControlLeft, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        statuslabelClock.text = getServerTime()
        statuslabelLocation.text = "Начальная локация"

        // --- Добавляем кнопку и поле для отправки game.html ---
    val layout = findViewById<android.widget.LinearLayout>(R.id.formMain) ?: window.decorView.rootView as android.view.ViewGroup
        val btnSendGameHtml = android.widget.Button(this)
        btnSendGameHtml.text = "Отправить game.html"
        val tvGameHtmlPath = android.widget.TextView(this)
        val file = java.io.File(filesDir, "game.html")
        tvGameHtmlPath.text = "Путь к game.html: ${file.absolutePath}"
        val paramsBtn = android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT)
        val paramsTv = android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT)
        btnSendGameHtml.layoutParams = paramsBtn
        tvGameHtmlPath.layoutParams = paramsTv
        if (layout is android.widget.LinearLayout) {
            layout.addView(btnSendGameHtml, 0)
            layout.addView(tvGameHtmlPath, 1)
        } else if (layout is android.view.ViewGroup) {
            layout.addView(btnSendGameHtml)
            layout.addView(tvGameHtmlPath)
        }
        btnSendGameHtml.setOnClickListener {
            try {
                val uri = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    applicationContext.packageName + ".provider",
                    file
                )
                val intent = android.content.Intent(android.content.Intent.ACTION_SEND)
                intent.type = "text/html"
                intent.putExtra(android.content.Intent.EXTRA_STREAM, uri)
                intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(android.content.Intent.createChooser(intent, "Отправить game.html через..."))
            } catch (e: Exception) {
                android.widget.Toast.makeText(this, "Ошибка отправки файла: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
        }
        // --- конец блока ---
    }

    fun getServerTime(): String {
        return "12:34:56"
    }

    fun ShowFishTip() {
        // Показывает подсказку по рыбалке (реализуйте по необходимости)
    }
}
