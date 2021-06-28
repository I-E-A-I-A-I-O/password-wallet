package com.example.passwordwallet.requests

import android.util.Log
import com.example.passwordwallet.requests.types.PasswordPosted
import com.example.passwordwallet.requests.types.requests.PostPassword
import retrofit2.Response
import java.lang.Exception

fun insertPassword(token: String, password: PostPassword): PasswordPosted {
    return try {
        val call = Api.getInstance().savePassword("Bearer $token", password)
        val response = call.execute()
        if (response.isSuccessful) {
            PasswordPosted(response.body()?.message!!, response.body()?.id, true)
        } else {
            val errorBody = parseError(response as Response<Any>)
            PasswordPosted(errorBody.message, null, false)
        }
    } catch (e: Exception) {
        Log.d("Network exception", e.stackTraceToString())
        PasswordPosted("Couldn't connect to the server", null, false)
    }
}
