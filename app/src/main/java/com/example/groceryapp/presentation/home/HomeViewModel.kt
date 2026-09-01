package com.example.groceryapp.presentation.home

import androidx.lifecycle.ViewModel

import com.example.groceryapp.data.repository.AuthRepository
import com.example.groceryapp.domain.model.User
import kotlinx.coroutines.flow.Flow

class HomeViewModel(private val authRepository: AuthRepository = AuthRepository()) : ViewModel() {
    val currentUser: Flow<User?> = authRepository.currentUser
}
