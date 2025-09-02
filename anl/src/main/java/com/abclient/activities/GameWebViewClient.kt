package com.abclient.activities

import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.os.Handler
import android.os.Looper
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class GameWebViewClient(
    private val profile: String,
    private val cookies: String,
    private val onHtmlUpdate: (String) -> Unit
) : WebViewClient() {
    private val client = OkHttpClient()

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url?.toString() ?: return false
        Handler(Looper.getMainLooper()).post {
            fetchAndUpdate(view, url)
        }
        return true
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url == null) return false
        Handler(Looper.getMainLooper()).post {
            fetchAndUpdate(view, url)
        }
        return true
    }

    private fun fetchAndUpdate(view: WebView?, url: String) {
        val ctx = view?.context ?: return
        // Проверяем кэш
        val cached = com.abclient.cache.FileCache.get(ctx, url)
        if (cached != null) {
            val rawHtml = com.abclient.filter.ResponseDecoder.decode(cached, null, null, null)
            val urlLower = url.lowercase()
            val html = when {
                urlLower.contains("game.php") -> com.abclient.filter.GamePhpFilter.filter(rawHtml)
                urlLower.contains("main.php") -> com.abclient.filter.MainPhpFilter.filter(rawHtml)
                urlLower.endsWith(".js") -> com.abclient.filter.JsFilter.filter(rawHtml)
                urlLower.contains("msg.php") -> com.abclient.filter.MsgPhpFilter.filter(rawHtml)
                else -> com.abclient.filter.GameHtmlFilter.filter(rawHtml)
            }
            ctx.filesDir.let { dir ->
                val file = java.io.File(dir, "game.html")
                file.writeText(html)
            }
            onHtmlUpdate(html)
            return
        }
        // Если нет в кэше — HTTP-запрос
        val req = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/139.0.7258.138 Safari/537.36")
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
            .addHeader("Accept-Encoding", "gzip, deflate")
            .addHeader("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
            .addHeader("Cookie", cookies)
            .build()
        try {
            val resp = client.newCall(req).execute()
            val bytes = resp.body?.bytes()
            // Сохраняем в кэш
            if (bytes != null) com.abclient.cache.FileCache.put(ctx, url, bytes)
            val contentEncoding = resp.header("Content-Encoding")
            val transferEncoding = resp.header("Transfer-Encoding")
            val contentType = resp.header("Content-Type")
            val rawHtml = bytes?.let { com.abclient.filter.ResponseDecoder.decode(it, contentEncoding, transferEncoding, contentType) } ?: ""
            val urlLower = url.lowercase()
            val html = when {
                urlLower.contains("game.php") -> com.abclient.filter.GamePhpFilter.filter(rawHtml)
                urlLower.contains("main.php") -> com.abclient.filter.MainPhpFilter.filter(rawHtml)
                urlLower.endsWith(".js") -> com.abclient.filter.JsFilter.filter(rawHtml)
                urlLower.contains("msg.php") -> com.abclient.filter.MsgPhpFilter.filter(rawHtml)
                else -> com.abclient.filter.GameHtmlFilter.filter(rawHtml)
            }
            resp.close()
            ctx.filesDir.let { dir ->
                val file = java.io.File(dir, "game.html")
                file.writeText(html)
            }
            onHtmlUpdate(html)
        } catch (e: Exception) {
            onHtmlUpdate("Ошибка запроса: ${e.message}")
        }
    }
}
