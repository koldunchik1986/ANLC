package com.neverlands.anlc.auth

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class SimpleCookieJar : CookieJar {

    private val cookieStore = mutableMapOf<String, List<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url.host] = cookies
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url.host] ?: emptyList()
    }

    fun getCookies(host: String): List<Cookie> {
        return cookieStore[host] ?: emptyList()
    }

    fun clearCookies(host: String) {
        cookieStore.remove(host)
    }

    fun getAllCookies(): Map<String, List<Cookie>> {
        return cookieStore
    }
}
