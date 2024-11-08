package com.example.mysql.ui.presentation

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysql.RetrofitInstance
import com.example.mysql.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Nothing)
    val registerState = _registerState.asStateFlow()


    fun registerUser(name: String, email: String, password: String) {
        _registerState.value = RegisterState.Loading

        Log.d("RegisterViewModel", "Name: $name, Email: $email, Password: $password")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = RetrofitInstance.retrofitApi.registerUser(User(name, email, password))

                Log.d("RegisterViewModel", "Response code: ${result.code()}")
                Log.d("RegisterViewModel", "Response body: ${result.body()}")

                if (result.isSuccessful && result.body() != null) {
                    val responseBody = result.body()!!

                    if (!responseBody.error) {
                        with(sharedPreferences.edit()) {
                            putString("email", email)
                            apply()
                        }
                        _registerState.value = RegisterState.Success(responseBody.message)
                    } else {
                        Log.d("RegisterViewModel", "Registration failed: ${responseBody.message}")
                        _registerState.value = RegisterState.Error(responseBody.message)
                    }
                } else {
                    Log.d("RegisterViewModel", "Unsuccessful response or null body")
                    _registerState.value = RegisterState.Error("Something went wrong")
                }
            } catch (e: Exception) {
                Log.d("RegisterViewModel", "Exception occurred: ${e.message}")
                _registerState.value = RegisterState.Error("Something went wrong")
            }
        }
    }
}

sealed class RegisterState {
    data object Nothing : RegisterState()
    data object Loading : RegisterState()
    data class Error(val message: String) : RegisterState()
    data class Success(val message: String) : RegisterState()
}
