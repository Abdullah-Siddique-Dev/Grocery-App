package com.example.groceryapp.services

import com.example.groceryapp.models.*
import com.example.groceryapp.repositories.CartRepository
import com.example.groceryapp.repositories.ProductRepository
import java.time.Instant

class CartService(
    private val cartRepository: CartRepository = CartRepository(),
    private val productRepository: ProductRepository = ProductRepository()
) {

    suspend fun getCart(userId: String): Cart {
        return cartRepository.getCartByUserId(userId) ?: createEmptyCart(userId)
    }

    suspend fun getCartResponse(userId: String): CartResponse {
        val cart = getCart(userId)
        val items = cart.items.map { cartItem ->
            val product = productRepository.findById(cartItem.productId)
            CartItemResponse(
                productId = cartItem.productId,
                productName = product?.name ?: "Unknown Product",
                productImageUrl = product?.imageUrl ?: "",
                quantity = cartItem.quantity,
                price = cartItem.priceAtAdd,
                subtotal = cartItem.quantity * cartItem.priceAtAdd,
                stockQuantity = product?.stockQuantity ?: 0
            )
        }
        val total = items.sumOf { it.subtotal }
        return CartResponse(
            id = cart.id,
            userId = cart.userId,
            items = items,
            total = total,
            updatedAt = cart.updatedAt
        )
    }

    suspend fun addItem(userId: String, productId: String, quantity: Int): Result<CartResponse> {
        if (quantity <= 0) return Result.failure<CartResponse>(Exception("Quantity must be greater than zero"))

        val product = productRepository.findById(productId) ?: return Result.failure<CartResponse>(Exception("Product not found"))
        if (!product.isAvailable || product.stockQuantity <= 0) return Result.failure<CartResponse>(Exception("Product is out of stock"))

        val cart = getCart(userId)
        val existingItems = cart.items.toMutableList()
        val existingItemIndex = existingItems.indexOfFirst { it.productId == productId }

        val totalQuantity = if (existingItemIndex != -1) {
            existingItems[existingItemIndex].quantity + quantity
        } else {
            quantity
        }

        if (totalQuantity > product.stockQuantity) {
            return Result.failure<CartResponse>(Exception("Insufficient stock. Only ${product.stockQuantity} available."))
        }

        if (existingItemIndex != -1) {
            val existingItem = existingItems[existingItemIndex]
            existingItems[existingItemIndex] = existingItem.copy(
                quantity = totalQuantity,
                priceAtAdd = product.price
            )
        } else {
            existingItems.add(CartItem(productId, quantity, product.price))
        }

        val updatedCart = cart.copy(items = existingItems, updatedAt = Instant.now().toString())
        cartRepository.saveCart(updatedCart)
        return Result.success(getCartResponse(userId))
    }

    suspend fun updateQuantity(userId: String, productId: String, quantity: Int): Result<CartResponse> {
        if (quantity <= 0) return removeItem(userId, productId)

        val product = productRepository.findById(productId) ?: return Result.failure<CartResponse>(Exception("Product not found"))
        if (quantity > product.stockQuantity) {
            return Result.failure<CartResponse>(Exception("Insufficient stock. Only ${product.stockQuantity} available."))
        }

        val cart = cartRepository.getCartByUserId(userId) ?: return Result.failure<CartResponse>(Exception("Cart not found"))
        val existingItems = cart.items.toMutableList()
        val itemIndex = existingItems.indexOfFirst { it.productId == productId }

        if (itemIndex == -1) return Result.failure<CartResponse>(Exception("Item not found in cart"))

        existingItems[itemIndex] = existingItems[itemIndex].copy(
            quantity = quantity,
            priceAtAdd = product.price
        )

        val updatedCart = cart.copy(items = existingItems, updatedAt = Instant.now().toString())
        cartRepository.saveCart(updatedCart)
        return Result.success(getCartResponse(userId))
    }

    suspend fun removeItem(userId: String, productId: String): Result<CartResponse> {
        val cart = cartRepository.getCartByUserId(userId) ?: return Result.failure<CartResponse>(Exception("Cart not found"))
        val existingItems = cart.items.toMutableList()
        
        if (existingItems.removeIf { it.productId == productId }) {
            val updatedCart = cart.copy(items = existingItems, updatedAt = Instant.now().toString())
            cartRepository.saveCart(updatedCart)
            return Result.success(getCartResponse(userId))
        }
        
        return Result.failure<CartResponse>(Exception("Item not found in cart"))
    }


    suspend fun clearCart(userId: String): Result<Unit> {
        cartRepository.deleteCart(userId)
        return Result.success(Unit)
    }

    private fun createEmptyCart(userId: String): Cart {
        return Cart(userId = userId, items = emptyList(), updatedAt = Instant.now().toString())
    }
}
