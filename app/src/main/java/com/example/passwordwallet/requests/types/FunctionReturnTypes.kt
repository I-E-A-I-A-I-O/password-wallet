package com.example.passwordwallet.requests.types

import com.example.passwordwallet.requests.types.requests.PasswordDescription

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

data class PasswordValid(
    val message: String,
    val valid: Boolean,
)

data class GetPasswords(
    val message: String,
    val success: Boolean,
    val content: List<PasswordDescription>
)
