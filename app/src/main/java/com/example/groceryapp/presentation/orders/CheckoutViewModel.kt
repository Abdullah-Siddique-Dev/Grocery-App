package com.example.groceryapp.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.CartRepository
import com.example.groceryapp.data.repository.OrderRepository
import com.example.groceryapp.data.repository.UserRepository
import com.example.groceryapp.domain.model.Address
import com.example.groceryapp.domain.model.Cart
import com.example.groceryapp.domain.model.PaymentMethod
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
    private val cartRepository: CartRepository = CartRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val state: StateFlow<CheckoutState> = _state.asStateFlow()

    private val _cart = MutableStateFlow<Cart?>(null)
    val cart: StateFlow<Cart?> = _cart.asStateFlow()

    private val _address = MutableStateFlow<Address?>(null)
    val address: StateFlow<Address?> = _address.asStateFlow()

    init {
        loadCart()
        loadUserAddress()
    }

    private fun loadCart() {
        viewModelScope.launch {
            cartRepository.getCart().collect { result ->
                result.onSuccess { _cart.value = it }
            }
        }
    }

    private fun loadUserAddress() {
        viewModelScope.launch {
            userRepository.getUserProfile().collect { result ->
                result.onSuccess { _address.value = it.address }
            }
        }
    }

    fun placeOrder(deliveryAddress: Address, paymentMethod: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY) {
        viewModelScope.launch {
            _state.value = CheckoutState.Loading
            
            val orderResult = orderRepository.placeOrder(deliveryAddress, paymentMethod)

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

    fun updateAddress(address: Address) {
        viewModelScope.launch {
            userRepository.updateAddress(address).onSuccess {
                _address.value = it.address
            }
        }
    }
}
