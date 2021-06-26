package com.example.passwordwallet.requests

import com.example.passwordwallet.requests.types.Message
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

fun parseError(response: Response<Any>): Message {
    val converter: Converter<ResponseBody, Message> =
        retrofit.responseBodyConverter(Message::class.java, arrayOfNulls(0))
    return try {
        val responseBody = response.errorBody()
        if (responseBody != null) {
            converter.convert(responseBody) ?: Message()
        } else {
            Message()
        }
    } catch (e: IOException) {
        Message()
    }
}