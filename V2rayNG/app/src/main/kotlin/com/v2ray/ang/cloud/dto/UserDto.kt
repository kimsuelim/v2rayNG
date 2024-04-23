package com.v2ray.ang.cloud.dto

data class UserDto(
    val id: Long = 0,
    val name: String,
    val email: String,
    val emailVerified: Boolean = false,
    val password: String
)