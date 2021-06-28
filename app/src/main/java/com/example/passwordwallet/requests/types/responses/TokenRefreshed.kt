package com.example.passwordwallet.requests.types.responses

import com.google.gson.annotations.SerializedName

data class TokenRefreshed(
    @SerializedName("token") val token: String,
)
