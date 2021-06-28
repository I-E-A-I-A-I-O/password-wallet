package com.example.passwordwallet.requests.types.requests

import com.google.gson.annotations.SerializedName

data class UserPassword(
    @SerializedName("password") val password: String
)
