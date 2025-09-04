package com.abclient.auth

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset

suspend fun saveAuthConfig(context: Context, configName: String, config: AuthConfig) = withContext(Dispatchers.IO) {
    val configDir = File(context.filesDir, "configs/auth")
    if (!configDir.exists()) configDir.mkdirs()
    val configFile = File(configDir, configName)
    val json = JSONObject()
    json.put("loginUrl", config.loginUrl)
    json.put("encoding", config.encoding)
    json.put("fields", JSONObject(config.fields))
    json.put("cookies", JSONArray(config.cookies))
    json.put("headers", JSONObject(config.headers))
    json.put("method", config.method)
    configFile.writeText(json.toString(2), Charset.forName("UTF-8"))
}
