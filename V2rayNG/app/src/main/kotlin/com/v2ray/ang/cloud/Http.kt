package com.v2ray.ang.cloud

import com.v2ray.ang.BuildConfig
import com.v2ray.ang.util.Utils
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


object Http {
    @Throws(IOException::class)
    fun get(urlStr: String): String {
        val url = URL(urlStr)
        val conn = url.openConnection()
//        val encodedAuth = Base64.encodeToString(auth.toByteArray(), Base64.DEFAULT)
        val auth = BuildConfig.HTTP_BASIC_AUTH_USER + ":" + BuildConfig.HTTP_BASIC_AUTH_PASSWORD

        conn.setRequestProperty("User-agent", "v2rayNG/${BuildConfig.VERSION_NAME}")
        conn.setRequestProperty("Authorization", "Basic ${Utils.encode(auth)}")
        conn.useCaches = false

        return conn.inputStream.use {
            it.bufferedReader().readText()
        }
    }

    @Throws(IOException::class)
    fun post(urlStr: String, jsonBody: String): String {
        val url = URL(urlStr)
        val auth = BuildConfig.HTTP_BASIC_AUTH_USER + ":" + BuildConfig.HTTP_BASIC_AUTH_PASSWORD

        (url.openConnection() as? HttpURLConnection)?.run {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; utf-8")
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Authorization", "Basic ${Utils.encode(auth)}")
            doOutput = true
            outputStream.write(jsonBody.toByteArray())

            return inputStream.use { it.bufferedReader().readText() }
        }

        return Exception("Cannot open HttpURLConnection").toString()
    }

    fun getApiHost(): String {
       return BuildConfig.API_HOST_URL
    }
}