package com.example.passwordwallet.requests.types

import com.google.gson.annotations.SerializedName

data class TokenRefreshed(
    @SerializedName("token") val token: String,
)
