package com.neverlands.anlc.data.remote

import com.neverlands.anlc.data.remote.api.ApiClient
import com.neverlands.anlc.data.remote.interceptor.BrowserHeadersInterceptor
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.Collections

object ApiClientFactory {

    val cookieJar = WebViewCookieJar()

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectionSpecs(Collections.singletonList(ConnectionSpec.CLEARTEXT))
        .followRedirects(true)
        .addInterceptor(BrowserHeadersInterceptor())
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .cookieJar(cookieJar)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://neverlands.ru/")
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    val apiClient: ApiClient = retrofit.create(ApiClient::class.java)
}
