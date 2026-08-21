package com.example.groceryapp.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.OrderRepository
import com.example.groceryapp.domain.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class OrdersState {
    object Loading : OrdersState()
    data class Success(val orders: List<Order>) : OrdersState()
    object Empty : OrdersState()
    data class Error(val message: String) : OrdersState()
}

class OrdersViewModel(private val repository: OrderRepository = OrderRepository()) : ViewModel() {
    private val _state = MutableStateFlow<OrdersState>(OrdersState.Loading)
    val state: StateFlow<OrdersState> = _state.asStateFlow()

    fun loadOrders(userId: String) {
        viewModelScope.launch {
            _state.value = OrdersState.Loading
            repository.getOrderHistory(userId).collect { result ->
                result.onSuccess { list ->
                    _state.value = if (list.isEmpty()) OrdersState.Empty else OrdersState.Success(list)
                }.onFailure {
                    _state.value = OrdersState.Error(it.message ?: "Failed to load orders")
                }
            }
        }
    }
}
