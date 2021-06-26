package com.example.passwordwallet.requests

import com.example.passwordwallet.requests.types.Login
import com.example.passwordwallet.requests.types.Message
import com.example.passwordwallet.requests.types.OKLogin
import com.example.passwordwallet.requests.types.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun registerAccount(user: User, onResponse: ((code: Int, message: String) -> Unit)?) {
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

fun login(login: Login, onResponse: ((accepted: Boolean, message: String, data: OKLogin?) -> Unit)?) {
    val call = apiService.login(login)
    call.enqueue(object : Callback<OKLogin> {
        override fun onResponse(call: Call<OKLogin>, response: Response<OKLogin>) {
            if (onResponse == null) {
                return
            }
            if (response.isSuccessful) {
                val body = response.body() ?: return
                onResponse(true, body.message, body)
            } else {
                val body = parseError(response as Response<Any>)
                onResponse(false, body.message, null)
            }
        }

        override fun onFailure(call: Call<OKLogin>, t: Throwable) {
            if (onResponse != null) {
                onResponse(false, "Couldn't connect to Server", null)
            }
        }
    })
}
