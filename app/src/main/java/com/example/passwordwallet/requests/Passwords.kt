package com.example.passwordwallet.requests

import android.util.Log
import com.example.passwordwallet.requests.types.GetPasswords
import com.example.passwordwallet.requests.types.PasswordPosted
import com.example.passwordwallet.requests.types.requests.PasswordDescription
import com.example.passwordwallet.requests.types.requests.PostPassword
import com.example.passwordwallet.requests.types.requests.UserPassword
import com.example.passwordwallet.requests.types.responses.Message
import com.example.passwordwallet.utils.SessionValues
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

fun getPasswordsFromServer(token: String): GetPasswords {
    return try {
        val call = Api.getInstance().getPasswords("Bearer $token", UserPassword(SessionValues.password!!))
        val response = call.execute()
        if (response.isSuccessful) {
            GetPasswords("OK", true, response.body()!!)
        } else {
            val errorBody = parseError(response as Response<Any>)
            GetPasswords(errorBody.message, false, listOf())
        }
    } catch (e: Exception) {
        Log.d("Network exception", e.stackTraceToString())
        GetPasswords("Error retrieving passwords. Check internet connection.", false, listOf())
    }
}

fun deletePassword(id: String, token: String): Message {
    return try {
        val call = Api.getInstance().deletePassword("Bearer $token", id)
        val response = call.execute()
        if (response.isSuccessful) {
            response.body()!!
        } else {
            parseError(response.errorBody() as Response<Any>)
        }
    } catch (e: Exception) {
        Message()
    }
}
