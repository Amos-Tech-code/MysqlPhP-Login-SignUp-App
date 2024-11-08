package com.example.mysql

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val retrofitApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://0efc-2c0f-6300-d00-3a00-f868-39fd-334c-122e.ngrok-free.app/Testsql/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}