package com.neverlands.anlc.ui.main

import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TabHost
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.neverlands.anlc.R
import com.neverlands.anlc.data.GameContentHolder
import com.neverlands.anlc.data.local.ProfileManager
import com.neverlands.anlc.databinding.ActivityMainBinding
import com.neverlands.anlc.forms.LogListActivity
import com.neverlands.anlc.ui.base.BaseActivity
import com.neverlands.anlc.ui.login.ProfilesActivity
import com.neverlands.anlc.webview.JavaScriptInterface
import com.neverlands.anlc.webview.WebViewRequestInterceptor
import com.neverlands.anlc.data.local.ChatManager
import com.neverlands.anlc.data.local.ChatProcessor
import com.neverlands.anlc.data.remote.FileLogger
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var browserGame: WebView
    private lateinit var statuslabelClock: TextView
    private val webViewRequestInterceptor = WebViewRequestInterceptor()
    private lateinit var chatProcessor: ChatProcessor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profile = ProfileManager.getCurrentProfile()

        setupViews()
        setupTabs()
        chatProcessor = ChatProcessor(viewModel)
        setupWebView()
        setupObservers()

        if (profile != null) {
            ChatManager.init(applicationContext, profile)
            viewModel.startHeartbeat()
        } else {
            goToLogin()
            return
        }

        val htmlContent = GameContentHolder.htmlContent
        if (htmlContent != null) {
            browserGame.loadDataWithBaseURL("http://neverlands.ru/", htmlContent, "text/html", "windows-1251", null)
            GameContentHolder.htmlContent = null // Clear the content after use
        } else {
            // This might happen if the user manually starts MainActivity
            // or if there was an error in the auth flow.
            goToLogin()
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, ProfilesActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupViews() {
        statuslabelClock = findViewById(R.id.statuslabelClock)
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            goToLogin()
        }
    }

    private fun setupTabs() {
        binding.tabHost.setup()
        var spec: TabHost.TabSpec = binding.tabHost.newTabSpec("Игра").setContent(R.id.tabGame).setIndicator("Игра")
        binding.tabHost.addTab(spec)
        spec = binding.tabHost.newTabSpec("Контакты").setContent(R.id.tabContacts).setIndicator("Контакты")
        binding.tabHost.addTab(spec)
        spec = binding.tabHost.newTabSpec("Логи").setContent(R.id.tabLogs).setIndicator("Логи")
        binding.tabHost.addTab(spec)
        binding.tabHost.currentTab = 0
    }

    private fun setupWebView() {
        browserGame = binding.browserGame
        browserGame.settings.javaScriptEnabled = true
        browserGame.settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.7258.138 Safari/537.36"
        browserGame.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                FileLogger.log(applicationContext, "onPageFinished: $url")
            }

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                return if (request != null) webViewRequestInterceptor.interceptRequest(request) else null
            }
        }
        browserGame.addJavascriptInterface(JavaScriptInterface(browserGame, viewModel, chatProcessor), "Android")
    }

    private fun setupObservers() {
        viewModel.currentTime.observe(this) { time ->
            statuslabelClock.text = time
        }
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
            else -> super.onOptionsItemSelected(item)
        }
    }
}
