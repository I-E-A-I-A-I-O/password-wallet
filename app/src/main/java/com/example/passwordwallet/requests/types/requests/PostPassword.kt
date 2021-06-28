package com.example.passwordwallet.requests.types.requests

import com.google.gson.annotations.SerializedName

data class PostPassword(
    @SerializedName("description") val description: String,
    @SerializedName("password") val password: String,
    @SerializedName("master_pass") val masterPass: String,
)
