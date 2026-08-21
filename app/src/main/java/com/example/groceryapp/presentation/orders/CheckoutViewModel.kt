package com.example.groceryapp.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.CartRepository
import com.example.groceryapp.data.repository.OrderRepository
import com.example.groceryapp.domain.model.OrderItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CheckoutState {
    object Idle : CheckoutState()
    object Loading : CheckoutState()
    data class Success(val orderId: String) : CheckoutState()
    data class Error(val message: String) : CheckoutState()
}

class CheckoutViewModel(
    private val orderRepository: OrderRepository = OrderRepository(),
    private val cartRepository: CartRepository = CartRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val state: StateFlow<CheckoutState> = _state.asStateFlow()

    fun placeOrder(userId: String, deliveryAddress: String) {
        if (deliveryAddress.isBlank()) {
            _state.value = CheckoutState.Error("Delivery address is required")
            return
        }

        viewModelScope.launch {
            _state.value = CheckoutState.Loading
            
            // The backend is authoritative and retrieves items/total from the user's active cart.
            val orderResult = orderRepository.placeOrder(
                userId = userId,
                items = emptyList(), // Ignored by repository API call
                totalAmount = 0.0,   // Ignored by repository API call
                deliveryAddress = deliveryAddress
            )

            orderResult.onSuccess { order ->
                _state.value = CheckoutState.Success(order.id)
            }.onFailure {
                _state.value = CheckoutState.Error(it.message ?: "Failed to place order")
            }
        }
    }

    fun resetState() {
        _state.value = CheckoutState.Idle
    }
}
