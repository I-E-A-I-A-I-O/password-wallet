package com.example.passwordwallet.requests

import com.example.passwordwallet.requests.types.Message
import com.example.passwordwallet.requests.types.User
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

const val BASE_URL = "http://localhost:8000/"

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface Endpoints {
    @POST("users")
    fun registerAccount(@Body user: User): Call<Message>
}

val apiService = retrofit.create(Endpoints::class.java)
