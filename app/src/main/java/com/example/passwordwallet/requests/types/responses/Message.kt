package com.example.passwordwallet.requests.types.responses

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("message") val message: String = "There went something wrong.",
)
