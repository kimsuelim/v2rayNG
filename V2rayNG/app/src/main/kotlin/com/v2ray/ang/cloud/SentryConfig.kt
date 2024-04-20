package com.v2ray.ang.cloud

import com.v2ray.ang.util.MmkvManager
import io.sentry.Sentry

object SentryConfig {
    fun attachCustomContext() {
        Sentry.configureScope { scope ->
            val serverList = MmkvManager.decodeServerList()
            val context = serverList.map {
                val serverConfig = MmkvManager.decodeServerConfig(it)
                mapOf(
                    "name" to serverConfig?.remarks,
                    "address" to serverConfig?.getV2rayPointDomainAndPort(),
                    "protocol" to serverConfig?.getV2rayPointProtocol()
                )
            }

            scope.setContexts("Server", context)
        }
    }
}
