package com.example.passwordwallet.requests

import android.content.Context
import android.util.Log
import com.example.passwordwallet.requests.types.PasswordValid
import com.example.passwordwallet.requests.types.RefreshToken
import com.example.passwordwallet.requests.types.requests.Login
import com.example.passwordwallet.requests.types.requests.User
import com.example.passwordwallet.requests.types.requests.UserPassword
import com.example.passwordwallet.requests.types.responses.Message
import com.example.passwordwallet.requests.types.responses.OKLogin
import com.example.passwordwallet.requests.types.responses.TokenRefreshed
import com.example.passwordwallet.room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun registerAccount(user: User, onResponse: ((code: Int, message: String) -> Unit)?) {
    val call = Api.getInstance().registerAccount(user)
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
    val call = Api.getInstance().login(login)
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

fun isTokenValid(accessToken: String): Boolean {
    return try {
        val call = Api.getInstance().isTokenValid("Bearer $accessToken")
        val response = call.execute()
        response.isSuccessful
    } catch (e: Exception) {
        Log.d("Network exception", e.stackTraceToString())
        false
    }
}

fun refreshToken(refreshToken: String): RefreshToken {
    return try {
        val call = Api.getInstance().refreshToken("Bearer $refreshToken")
        val response = call.execute()
        if (response.isSuccessful) {
            RefreshToken("OK", response.body()?.token, true)
        } else {
            val errorBody = parseError(response as Response<Any>)
            RefreshToken(errorBody.message, null, false)
        }
    } catch (e: Exception) {
        Log.d("Network exception", e.stackTraceToString())
        RefreshToken("Couldn't connect to the server", null, false)
    }
}

fun isPasswordValid(password: String, accessToken: String): PasswordValid {
    return try {
        val call = Api.getInstance().isPasswordValid("Bearer $accessToken", UserPassword(password))
        val response = call.execute()
        val message: String = if (response.isSuccessful) {
            response.body()?.message!!
        } else {
            parseError(response as Response<Any>).message
        }
        PasswordValid(
            message,
            response.isSuccessful
        )
    } catch (e: Exception) {
        Log.d("Exception", e.stackTraceToString())
        PasswordValid("Network error", false)
    }
}
