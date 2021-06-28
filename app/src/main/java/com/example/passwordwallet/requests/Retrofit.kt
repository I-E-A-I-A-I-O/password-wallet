package com.example.passwordwallet.requests

import com.example.passwordwallet.requests.types.requests.Login
import com.example.passwordwallet.requests.types.requests.PostPassword
import com.example.passwordwallet.requests.types.requests.User
import com.example.passwordwallet.requests.types.responses.Message
import com.example.passwordwallet.requests.types.responses.OKLogin
import com.example.passwordwallet.requests.types.responses.PostedPassword
import com.example.passwordwallet.requests.types.responses.TokenRefreshed
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
    @POST("passwords/")
    fun savePassword(@Header("Authorization") accessToken: String, @Body password: PostPassword): Call<PostedPassword>
}

object Api {
    @Volatile private var instance: Endpoints? = null

    fun getInstance(): Endpoints {
        return instance ?: synchronized(this) {
            instance ?: createRetrofit().also { instance = it }
        }
    }

    private fun createRetrofit(): Endpoints {
        return retrofit.create(Endpoints::class.java)
    }
}
