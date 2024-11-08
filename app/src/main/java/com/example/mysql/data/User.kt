package com.example.mysql.data

data class User(
    val name: String,
    val email: String,
    val password : String,
    val auth_token: String? = null
)
