package com.example.groceryapp.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.OrderRepository
import com.example.groceryapp.domain.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class OrderDetailsState {
    object Loading : OrderDetailsState()
    data class Success(val order: Order) : OrderDetailsState()
    data class Error(val message: String) : OrderDetailsState()
}

class OrderDetailsViewModel(private val repository: OrderRepository = OrderRepository()) : ViewModel() {
    private val _state = MutableStateFlow<OrderDetailsState>(OrderDetailsState.Loading)
    val state: StateFlow<OrderDetailsState> = _state.asStateFlow()

    fun loadOrderDetails(orderId: String) {
        viewModelScope.launch {
            _state.value = OrderDetailsState.Loading
            repository.getOrderDetails(orderId).collect { result ->
                result.onSuccess { order ->
                    _state.value = OrderDetailsState.Success(order)
                }.onFailure {
                    _state.value = OrderDetailsState.Error(it.message ?: "Failed to load order details")
                }
            }
        }
    }
}
