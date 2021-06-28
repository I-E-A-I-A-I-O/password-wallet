package com.example.passwordwallet.requests

fun testConnection(): Boolean {
    val call = Api.getTestInstance().testConnection()
    val response = call.execute()
    return response.isSuccessful
}
