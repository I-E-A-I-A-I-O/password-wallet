package com.example.passwordwallet.requests

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.passwordwallet.requests.types.*
import com.example.passwordwallet.room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

fun registerAccount(user: User, onResponse: ((code: Int, message: String) -> Unit)?) {
    val call = api.getIntance().registerAccount(user)
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
    val call = api.getIntance().login(login)
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

private fun isTokenValid(accessToken: String, onResponse: (accepted: Boolean) -> Unit) {
    val call = api.getIntance().isTokenValid("Bearer $accessToken")
    call.enqueue(object: Callback<Message> {
        override fun onResponse(call: Call<Message>, response: Response<Message>) {
            onResponse(response.isSuccessful)
        }

        override fun onFailure(call: Call<Message>, t: Throwable) {
            onResponse(false)
        }
    })
}

private fun checkForRefresh(
    accessToken: String, refreshToken: String,
    onResponse: (success: Boolean, newToken: String?) -> Unit
) {
    isTokenValid(accessToken) {
        if (it) {
            onResponse(true, null)
        } else {
            val call = api.getIntance().refreshToken("Bearer $refreshToken")
            call.enqueue(object: Callback<TokenRefreshed> {
                override fun onResponse(
                    call: Call<TokenRefreshed>,
                    response: Response<TokenRefreshed>
                ) {
                    if (response.isSuccessful) {
                        onResponse(true, response.body()?.token)
                    } else {
                        onResponse(false, null)
                    }
                }

                override fun onFailure(call: Call<TokenRefreshed>, t: Throwable) {
                    onResponse(false, null)
                }
            })
        }
    }
}

fun validateToken(
    accessToken: String,
    refreshToken: String,
    context: Context,
    scope: CoroutineScope,
    onResponse: (success: Boolean, newToken: String?) -> Unit
    ) {
    checkForRefresh(accessToken, refreshToken) {
        success, newToken ->
        if (!success) {
            onResponse(success, null)
        } else {
            if (newToken != null) {
                scope.launch(Dispatchers.IO) {
                    AppDatabase.getInstance(context).userDao().updateAccessToken(newToken, accessToken)
                    onResponse(true, newToken)
                }
            } else {
                onResponse(true, null)
            }
        }
    }
}
