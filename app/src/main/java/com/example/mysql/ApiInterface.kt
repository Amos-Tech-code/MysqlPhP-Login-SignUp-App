package com.example.mysql

import com.example.mysql.data.GlobalResponse
import com.example.mysql.data.Result
import com.example.mysql.data.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {

    @FormUrlEncoded
    @POST("login.php")
    suspend fun loginUser(@Field("email") email: String, @Field("password") password: String): Response<GlobalResponse>

    @FormUrlEncoded
    @POST("user-details.php")
    suspend fun getUserDetails(@Field("email") email : String, @Field("token") token : String) : Response<GlobalResponse>

    @POST("register.php")
    suspend fun registerUser(@Body user: User): Response<Result>
}