package com.neverlands.anlc.data.remote

import com.neverlands.anlc.data.remote.api.ApiClient
import com.neverlands.anlc.data.remote.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Фабрика для создания и предоставления синглтона ApiClient.
 * Используется вместо Hilt, пока он временно отключен.
 */
object ApiClientFactory {

    val authInterceptor = AuthInterceptor()
    val cookieJar = SimpleCookieJar()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .cookieJar(cookieJar)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://neverlands.ru/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiClient: ApiClient = retrofit.create(ApiClient::class.java)
}
