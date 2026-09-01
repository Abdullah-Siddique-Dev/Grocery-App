package com.example.groceryapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.AuthRepository
import com.example.groceryapp.data.repository.UserRepository
import com.example.groceryapp.domain.model.Address
import com.example.groceryapp.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
    object Unauthenticated : ProfileState()
}

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut: StateFlow<Boolean> = _isLoggedOut.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            userRepository.getUserProfile().collect { result ->
                result.onSuccess { user ->
                    _state.value = ProfileState.Success(user)
                }.onFailure {
                    _state.value = ProfileState.Error(it.message ?: "Failed to load profile")
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.updateFcmToken(null)
            authRepository.logout()
            _isLoggedOut.value = true
        }
    }

    fun updateAddress(address: Address) {
        viewModelScope.launch {
            userRepository.updateAddress(address).onSuccess {
                _state.value = ProfileState.Success(it)
            }.onFailure {
                _state.value = ProfileState.Error(it.message ?: "Failed to update address")
            }
        }
    }
}
