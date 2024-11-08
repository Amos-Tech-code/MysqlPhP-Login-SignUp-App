package com.example.mysql.ui.presentation

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysql.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _welcomeMessage = MutableStateFlow("Loading...")
    val welcomeMessage = _welcomeMessage.asStateFlow()

    fun getUserDetails() {
        val email = sharedPreferences.getString("email", "")
        val token = sharedPreferences.getString("token", "")

        if (email != null && token != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result = RetrofitInstance.retrofitApi.getUserDetails(email, token)
                    if (result.isSuccessful && result.body() != null) {
                        withContext(Dispatchers.Main) {
                            val userName = result.body()?.user?.name ?: "User"
                            _welcomeMessage.value = "Welcome, $userName"
                        }
                    } else {
                        _welcomeMessage.value = "Something went wrong"
                    }
                } catch (e: Exception) {
                    Log.d("checkApiError", e.message.toString())
                    _welcomeMessage.value = "Something went wrong"
                }
            }
        }
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
    }
}
