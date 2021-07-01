package com.example.passwordwallet.requests

import com.example.passwordwallet.requests.types.requests.*
import com.example.passwordwallet.requests.types.responses.Message
import com.example.passwordwallet.requests.types.responses.OKLogin
import com.example.passwordwallet.requests.types.responses.PostedPassword
import com.example.passwordwallet.requests.types.responses.TokenRefreshed
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

const val BASE_URL = "https://password-wallet-app.herokuapp.com/"
const val TEST_URL = "https://clients3.google.com"

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val testRetrofit: Retrofit = Retrofit.Builder()
    .baseUrl(TEST_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface TestEndpoints {
    @POST("/generate_204")
    fun testConnection(): Call<Any>
}

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
    @POST("users/password")
    fun isPasswordValid(@Header("Authorization") accessToken: String, @Body password: UserPassword): Call<Message>
    @POST("passwords/list")
    fun getPasswords(@Header("Authorization") accessToken: String, @Body password: UserPassword): Call<List<PasswordDescription>>
    @DELETE("passwords/{id}")
    fun deletePassword(@Header("Authorization") accessToken: String, @Path("id") id: String): Call<Message>
}

object Api {
    @Volatile private var instance: Endpoints? = null
    @Volatile private var testInstance: TestEndpoints? = null

    fun getTestInstance(): TestEndpoints {
        return testInstance ?: synchronized(this) {
            testInstance ?: createTest().also { testInstance = it }
        }
    }

    fun getInstance(): Endpoints {
        return instance ?: synchronized(this) {
            instance ?: createRetrofit().also { instance = it }
        }
    }

    private fun createTest(): TestEndpoints {
        return testRetrofit.create(TestEndpoints::class.java)
    }

    private fun createRetrofit(): Endpoints {
        return retrofit.create(Endpoints::class.java)
    }
}
