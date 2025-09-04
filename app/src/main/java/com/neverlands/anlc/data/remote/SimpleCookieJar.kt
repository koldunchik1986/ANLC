package com.neverlands.anlc.data.remote

import android.webkit.CookieManager
import android.net.Uri
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * Простое хранилище кук в памяти, синхронизирующееся с Android WebView CookieManager.
 */
class SimpleCookieJar : CookieJar {
    private val cookieStore = mutableMapOf<String, MutableList<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        val hostCookies = cookieStore.getOrPut(host) { mutableListOf() }
        hostCookies.addAll(cookies)

        // Synchronize cookies to WebView CookieManager
        val cookieManager = CookieManager.getInstance()
        for (cookie in cookies) {
            cookieManager.setCookie(url.toString(), cookie.toString())
        }
        cookieManager.flush() // Ensure cookies are written to persistent storage
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = mutableListOf<Cookie>()
        // Add cookies from our internal store
        cookieStore[url.host]?.let { cookies.addAll(it) }

        // Get cookies from WebView CookieManager for the given URL
        val webViewCookies = CookieManager.getInstance().getCookie(url.toString())
        if (!webViewCookies.isNullOrEmpty()) {
            // Parse the cookie string from WebView and add to our list
            // This parsing is simplified and might not handle all cookie attributes correctly.
            // For full fidelity, a more robust parsing is needed.
            webViewCookies.split("; ").forEach {
                val parts = it.split("=", limit = 2)
                if (parts.size == 2) {
                    // Simplified cookie creation. Real OkHttp.Cookie.parse needs more context.
                    // This might not be fully accurate for all cookie attributes (domain, path, expiry).
                    // For a robust solution, consider using a library or more complex parsing.
                    try {
                        val cookie = Cookie.Builder()
                            .name(parts[0])
                            .value(parts[1])
                            .domain(url.host) // Assume domain from request URL
                            .build()
                        cookies.add(cookie)
                    } catch (e: IllegalArgumentException) {
                        // Handle cases where cookie parsing fails (e.g., invalid characters)
                        e.printStackTrace()
                    }
                }
            }
        }
        return cookies.distinctBy { it.name } // Remove duplicates, prioritizing cookies from our store
    }

    /**
     * Очищает куки для указанного хоста.
     * @param host Хост, для которого нужно очистить куки.
     */
    fun clearCookies(host: String) {
        cookieStore.remove(host)
        // Clear cookies from WebView CookieManager for the host
        val cookieManager = CookieManager.getInstance()
        val uri = Uri.parse("http://$host") // Use http for clearing, as it's domain-based
        val cookies = cookieManager.getCookie(uri.toString())
        if (!cookies.isNullOrEmpty()) {
            cookies.split("; ").forEach {
                val parts = it.split("=", limit = 2)
                if (parts.size == 2) {
                    cookieManager.setCookie(uri.toString(), "${parts[0]}=; expires=Thu, 01 Jan 1970 00:00:00 GMT")
                }
            }
        }
        cookieManager.flush()
    }

    /**
     * Возвращает все куки для указанного хоста.
     * @param host Хост, для которого нужно получить куки.
     * @return Список куки.
     */
    fun getCookies(host: String): List<Cookie> {
        return cookieStore[host] ?: emptyList()
    }
}
