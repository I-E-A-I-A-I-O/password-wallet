package com.example.passwordwallet.requests.types.responses

import com.google.gson.annotations.SerializedName

data class PostedPassword(
    @SerializedName("message") val message: String,
    @SerializedName("id") val id: String,
)
