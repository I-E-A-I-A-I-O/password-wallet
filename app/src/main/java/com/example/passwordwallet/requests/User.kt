package com.example.passwordwallet.requests

import com.example.passwordwallet.requests.types.Message
import com.example.passwordwallet.requests.types.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun registerAccount(user: User, onResponse: ((status: Int, reason: String) -> Unit)?) {
    val call = apiService.registerAccount(user)
    call.enqueue(object: Callback<Message> {
        override fun onResponse(call: Call<Message>, response: Response<Message>) {
            if (onResponse == null) {
                return
            }
            if (response.isSuccessful) {
                val body = response.body() ?: return
                onResponse(response.code(), body.message)
            } else {
                val body = parseError(response as Response<Any>)
                onResponse(response.code(), body.message)
            }
        }

        override fun onFailure(call: Call<Message>, t: Throwable) {
            if (onResponse != null) {
                onResponse(500, "Couldn't connect to Server")
            }
        }
    })
}
