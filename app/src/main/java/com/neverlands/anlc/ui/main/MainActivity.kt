package com.neverlands.anlc.ui.main

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope // Import lifecycleScope
import com.neverlands.anlc.R
import com.neverlands.anlc.data.local.ProfileManager
import com.neverlands.anlc.databinding.ActivityMainBinding // Assuming ViewBinding will be used
import com.neverlands.anlc.ui.base.BaseActivity
import com.neverlands.anlc.ui.login.ProfilesActivity // For logout navigation
import com.neverlands.anlc.forms.LogListActivity // Import LogListActivity
import com.neverlands.anlc.webview.WebViewRequestInterceptor // Import WebViewRequestInterceptor
import com.neverlands.anlc.webview.JavaScriptInterface // Import JavaScriptInterface
import kotlinx.coroutines.flow.collectLatest // Import collectLatest

/**
 * Главный экран игры.
 * Отображает WebView с игровым контентом и управляет "пульсом" сессии.
 */
class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var browserGame: WebView
    private lateinit var statuslabelClock: TextView
    private val webViewRequestInterceptor = WebViewRequestInterceptor() // Instantiate the interceptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupWebView()
        setupObservers()

        // Запускаем "пульс" сессии, если профиль активен
        if (ProfileManager.getCurrentProfile() != null) {
            viewModel.startHeartbeat()
        } else {
            // Если профиль не установлен (например, прямой запуск MainActivity),
            // перенаправляем на экран выбора профиля
            val intent = Intent(this, ProfilesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Возобновляем "пульс" при возвращении на экран
        if (ProfileManager.getCurrentProfile() != null) {
            viewModel.startHeartbeat()
        }
    }

    override fun onPause() {
        super.onPause()
        // Останавливаем "пульс" при уходе с экрана
        viewModel.stopHeartbeat()
    }

    private fun setupViews() {
        statuslabelClock = findViewById(R.id.statuslabelClock) // Находим TextView для часов
        // TODO: Настроить другие элементы UI, такие как кнопки, меню и т.д.
        // Например, кнопка выхода
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            val intent = Intent(this, ProfilesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupWebView() {
        browserGame = binding.browserGame
        browserGame.settings.javaScriptEnabled = true // Включаем JavaScript
        browserGame.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url == null) return false

                // Check for internal game links (e.g., neverlands.ru)
                if (url.startsWith("http://neverlands.ru", true) || url.startsWith("https://neverlands.ru", true)) {
                    // TODO: Replicate C# GameBeforeNavigate logic here if needed in the future.
                    // The C# code had a commented-out section for "/abc-moveto:"
                    // if (url.startsWith("http://neverlands.ru/abc-moveto:", true)) {
                    //     // Handle internal navigation for navigator feature (currently disabled in C#)
                    //     // For now, just prevent loading in WebView and do nothing.
                    //     return true // Cancel navigation in WebView
                    // }

                    view?.loadUrl(url)
                    return true // Handled by WebView
                } else {
                    // Open external links in a browser
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                    return true // Handled by external browser
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("MainActivity", "WebView Document Completed: $url")
                // TODO: If AppVars.Profile.DoTexLog is true, append to a TextView or send to a logging mechanism.
                // For now, just a simple Log.d
            }

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                if (request == null) return null
                return webViewRequestInterceptor.interceptRequest(request)
            }
        }
        // Add JavaScript interface
        browserGame.addJavascriptInterface(JavaScriptInterface(browserGame), "external")

        // Загружаем начальную страницу игры
        browserGame.loadUrl("http://neverlands.ru/main.php") // Или другую стартовую страницу
    }

    private fun setupObservers() {
        viewModel.currentTime.observe(this) { time ->
            statuslabelClock.text = time // Обновляем часы
        }

        // Observe reloadUrlEvent from ViewModel
        lifecycleScope.launch {
            viewModel.reloadUrlEvent.collectLatest { url ->
                browserGame.loadUrl(url)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_view_logs -> {
                val intent = Intent(this, LogListActivity::class.java)
                startActivity(intent)
                true
            }
            // TODO: Add other menu item handlers here
            else -> super.onOptionsItemSelected(item)
        }
    }
}