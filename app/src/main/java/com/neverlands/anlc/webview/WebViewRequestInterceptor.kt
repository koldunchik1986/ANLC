package com.neverlands.anlc.webview

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.neverlands.anlc.data.remote.ApiClientFactory
import java.io.ByteArrayInputStream

class WebViewRequestInterceptor {

    fun interceptRequest(request: WebResourceRequest): WebResourceResponse? {
        val url = request.url.toString()
        if (url.endsWith(".js")) {
            try {
                val client = ApiClientFactory.okHttpClient
                val okHttpRequest = okhttp3.Request.Builder().url(url).build()
                val response = client.newCall(okHttpRequest).execute()
                if (response.isSuccessful) {
                    val bytes = response.body?.bytes() ?: return null
                    val stream = ByteArrayInputStream(bytes)
                    return WebResourceResponse("text/javascript", "windows-1251", stream)
                }
            } catch (e: Exception) {
                return null
            }
        }
        return null
    }
}
