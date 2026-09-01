package com.example.groceryapp.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.AdminRepository
import com.example.groceryapp.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AdminUsersState {
    object Loading : AdminUsersState()
    data class Success(val users: List<User>) : AdminUsersState()
    data class Error(val message: String) : AdminUsersState()
}

class AdminUsersViewModel(private val repository: AdminRepository = AdminRepository()) : ViewModel() {
    private val _state = MutableStateFlow<AdminUsersState>(AdminUsersState.Loading)
    val state: StateFlow<AdminUsersState> = _state.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _state.value = AdminUsersState.Loading
            repository.getAllUsers().collect { result ->
                result.onSuccess {
                    _state.value = AdminUsersState.Success(it)
                }.onFailure {
                    _state.value = AdminUsersState.Error(it.message ?: "Failed to load users")
                }
            }
        }
    }
}
