package com.example.groceryapp.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.AdminRepository
import com.example.groceryapp.domain.model.Order
import com.example.groceryapp.domain.model.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AdminOrdersState {
    object Loading : AdminOrdersState()
    data class Success(val orders: List<Order>) : AdminOrdersState()
    data class Error(val message: String) : AdminOrdersState()
}

class AdminOrdersViewModel(private val repository: AdminRepository = AdminRepository()) : ViewModel() {
    private val _state = MutableStateFlow<AdminOrdersState>(AdminOrdersState.Loading)
    val state: StateFlow<AdminOrdersState> = _state.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _state.value = AdminOrdersState.Loading
            repository.getAllOrders().collect { result ->
                result.onSuccess {
                    _state.value = AdminOrdersState.Success(it)
                }.onFailure {
                    _state.value = AdminOrdersState.Error(it.message ?: "Failed to load orders")
                }
            }
        }
    }

    fun updateStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status).onSuccess {
                loadOrders()
            }
        }
    }
}
