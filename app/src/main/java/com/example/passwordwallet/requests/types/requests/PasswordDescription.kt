package com.example.passwordwallet.requests.types.requests

import com.google.gson.annotations.SerializedName

data class PasswordDescription(
    @SerializedName("id") val id: String,
    @SerializedName("description") val description: String,
    @SerializedName("password") val password: String,
)
