package com.example.passwordwallet.requests.types.responses

import com.google.gson.annotations.SerializedName

data class OKLogin(
    @SerializedName("message") val message: String,
    @SerializedName("name") val name: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
)