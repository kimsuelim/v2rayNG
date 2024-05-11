package com.v2ray.ang.cloud

import com.v2ray.ang.BuildConfig
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


object Http {
    @Throws(IOException::class)
    fun get(urlStr: String): String {
        val url = URL(urlStr)
        val conn = url.openConnection() as HttpURLConnection
        val auth = TokenManager.getAccessToken()

        conn.setRequestProperty("User-agent", "v2rayNG/${BuildConfig.VERSION_NAME}")
        conn.setRequestProperty("Authorization", "Bearer $auth")
        conn.useCaches = false

        if (conn.responseCode == 401) throw UserNotAuthorizedException(conn.responseMessage)

        return conn.inputStream.use {
            it.bufferedReader().readText()
        }
    }

    @Throws(IOException::class)
    fun post(urlStr: String, jsonBody: String): String {
        val url = URL(urlStr)
        val auth = TokenManager.getAccessToken()

        (url.openConnection() as? HttpURLConnection)?.run {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; utf-8")
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-agent", "v2rayNG/${BuildConfig.VERSION_NAME}")
            setRequestProperty("Authorization", "Bearer $auth")
            doOutput = true
            outputStream.write(jsonBody.toByteArray())

            if (responseCode == 401) throw UserNotAuthorizedException(responseMessage)

            return inputStream.use { it.bufferedReader().readText() }
        }

        return Exception("Cannot open HttpURLConnection").toString()
    }

    fun getApiHost(): String {
       return BuildConfig.API_HOST_URL
    }
}

class UserNotAuthorizedException(message: String) : Exception(message)
