package com.example.passwordwallet.requests

import android.util.Log

fun testConnection(): Boolean {
    return try {
        val call = Api.getTestInstance().testConnection()
        val response = call.execute()
        response.isSuccessful
    } catch (e: Exception) {
        Log.d("Err", e.stackTraceToString())
        false
    }
}
