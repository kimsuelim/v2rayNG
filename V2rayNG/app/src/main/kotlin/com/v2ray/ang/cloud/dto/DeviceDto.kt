package com.v2ray.ang.cloud.dto

data class DeviceDto(
    val uuid: String,
    val networkInfo: List<Map<String, Any>>,
    val softwareInfo: Map<String, Any>,
)