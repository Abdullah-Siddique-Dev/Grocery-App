package com.example.groceryapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.login(email, password)
            result.onSuccess {
                _authState.value = AuthState.Success("Login Successful")
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Login failed")
            }
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        phoneNumber: String,
        address: String
    ) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || phoneNumber.isBlank() || address.isBlank()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.register(name, email, password, phoneNumber, address)
            result.onSuccess {
                _authState.value = AuthState.Success("Registration Successful")
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Registration failed")
            }
        }
    }

    fun clearState() {
        _authState.value = AuthState.Idle
    }
}
