package com.example.mysql.ui.presentation

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mysql.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Nothing)
    val loginState = _loginState.asStateFlow()

    init {
        checkExistingToken()
    }

    fun loginUser(email: String, password: String) {
        _loginState.value = LoginState.Loading

        Log.d("LoginViewModel", "Email: $email, Password: $password")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = RetrofitInstance.retrofitApi.loginUser(email, password)

                Log.d("LoginViewModel", "Response code: ${result.code()}")
                Log.d("LoginViewModel", "Response body: ${result.body()}")

                // Ensure the response is not null
                val responseBody = result.body()

                // Log response body details
                if (responseBody != null) {
                    Log.d("LoginViewModel", "User: ${responseBody.user}")
                    Log.d("LoginViewModel", "Response message: ${responseBody.result.message}")

                    // Check the login response structure here
                    if (!responseBody.result.error && responseBody.user != null) {
                        val authToken = responseBody.user.auth_token
                        Log.d("LoginViewModel", "Auth token: $authToken")
                        if (authToken != null) {
                            // Store token in SharedPreferences
                            with(sharedPreferences.edit()) {
                                putString("email", email)
                                putString("token", authToken)
                                apply()
                            }
                            _loginState.value = LoginState.Success(authToken)
                        } else {
                            Log.d("LoginViewModel", "Auth token is null")
                            _loginState.value = LoginState.Error("Login failed, token missing")
                        }
                    } else {
                        Log.d("LoginViewModel", "Login failed with message: ${responseBody.result.message}")
                        _loginState.value = LoginState.Error(responseBody.result.message)
                    }
                } else {
                    Log.d("LoginViewModel", "Response body is null")
                    _loginState.value = LoginState.Error("Something went wrong")
                }
            } catch (e: Exception) {
                Log.d("LoginViewModel", "Exception occurred: ${e.message}")
                _loginState.value = LoginState.Error("Something went wrong")
            }
        }


    }

    private fun checkExistingToken() {
        val token = sharedPreferences.getString("token", "")
        if (!token.isNullOrEmpty()) {
            _loginState.value = LoginState.Success(token)
        }
    }
}



sealed class LoginState {
    data object Nothing : LoginState()
    data object Loading : LoginState()
    data class Error(val message: String) : LoginState()
    data class Success(val token: String) : LoginState()
}

