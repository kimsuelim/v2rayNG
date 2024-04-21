package com.v2ray.ang.cloud

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.v2ray.ang.AppConfig
import com.v2ray.ang.cloud.Http.getApiHost
import com.v2ray.ang.util.AngConfigManager.importBatchConfig
import com.v2ray.ang.util.MmkvManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException


object ServerManager {
    suspend fun syncServerWithCloud() {
        // REF: https://developer.android.com/kotlin/coroutines#use-coroutines-for-main-safety
        // Move the execution of the coroutine to the I/O dispatcher
        return withContext(Dispatchers.IO) {
            // Blocking network request code
            try {
                val resp = getServerFromCloud()
                val typeToken = object : TypeToken<List<Map<String, Any>>>() {}.type
                val cloudServers = Gson().fromJson<List<Map<String, Any>>>(resp, typeToken)
                val serverList = MmkvManager.decodeServerList()

                cloudServers.forEach { server ->
                    val url = server["shareUrl"].toString()
                    val config = server["config"] as Map<*, *>
                    val domainAndPort = config["add"].toString() + ":" + config["port"].toString()
                    val protocol = config["protocol"].toString() + "(" + config["net"] + "+" + config["tls"] + ")"
                    var isAdded = false

                    serverList.forEach {
                        val serverConfig = MmkvManager.decodeServerConfig(it)
                        if (serverConfig != null) {
                            if (serverConfig.getV2rayPointProtocol() == protocol && serverConfig.getV2rayPointDomainAndPort() == domainAndPort) {
                                if (server["state"] != "RUNNING") {
                                    MmkvManager.removeServer(it)
                                }

                                isAdded = true
                            }
                        }
                    }

                    if (!isAdded) {
                        if (server["state"] == "RUNNING") {
                            importBatchConfig(url, "", true)
                        }
                    }
                }
            } catch (e: IOException) {
                Log.d(AppConfig.ANG_PACKAGE, e.toString())
            }
        }
    }

    private fun getServerFromCloud(): String {
        val url = getApiHost() + "/server"
        return try {
            Http.get(url)
        } catch (e: IOException) {
            Log.d(AppConfig.ANG_PACKAGE, e.toString())
            throw e
        }
    }
}