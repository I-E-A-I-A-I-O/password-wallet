package com.example.passwordwallet.requests.types

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("message") val message: String = "There went something wrong.",
    @SerializedName("extra") val extra: Any?
)
