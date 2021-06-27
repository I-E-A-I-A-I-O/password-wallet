package com.example.passwordwallet.requests

import com.example.passwordwallet.requests.types.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

const val BASE_URL = "https://password-wallet-app.herokuapp.com/"

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface Endpoints {
    @POST("users/")
    fun registerAccount(@Body user: User): Call<Message>
    @POST("session/login")
    fun login(@Body login: Login): Call<OKLogin>
    @POST("session/token/refresh")
    fun refreshToken(@Header("Authorization") refreshToken: String): Call<TokenRefreshed>
    @GET("session/token/state")
    fun isTokenValid(@Header("Authorization") accessToken: String): Call<Message>
}

object api {
    @Volatile private var instance: Endpoints? = null

    fun getIntance(): Endpoints {
        return instance ?: synchronized(this) {
            instance ?: createRetrofit().also { instance = it }
        }
    }

    private fun createRetrofit(): Endpoints {
        return retrofit.create(Endpoints::class.java)
    }
}
