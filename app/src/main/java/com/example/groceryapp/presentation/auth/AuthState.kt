package com.example.groceryapp.presentation.auth

import com.example.groceryapp.domain.model.UserRole

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String, val role: UserRole = UserRole.CUSTOMER) : AuthState()
    data class Error(val message: String) : AuthState()
}
