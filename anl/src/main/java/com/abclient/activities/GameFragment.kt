package com.abclient.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.CookieManager
import com.abclient.data.AuthManager
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import androidx.fragment.app.Fragment

class GameFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            val webView = WebView(requireContext())
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true

            val profile = activity?.intent?.getStringExtra("profile") ?: ""
            val cookies = activity?.intent?.getStringExtra("cookies") ?: ""

            // Загружаем HTML из файла game.html
            val file = java.io.File(requireContext().filesDir, "game.html")
            if (file.exists()) {
                val html = file.readText()
                webView.loadDataWithBaseURL("http://neverlands.ru/game.php", html, "text/html", "windows-1251", null)
            }

            // Устанавливаем кастомный WebViewClient
            webView.webViewClient = GameWebViewClient(profile, cookies) { html ->
                webView.loadDataWithBaseURL("http://neverlands.ru/game.php", html, "text/html", "windows-1251", null)
            }
            return webView
    }
}
