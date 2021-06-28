package com.example.passwordwallet.requests.types

data class RefreshToken(
    val message: String,
    val token: String?,
    val success: Boolean,
)

data class PasswordPosted(
    val message: String,
    val id: String?,
    val success: Boolean
)
