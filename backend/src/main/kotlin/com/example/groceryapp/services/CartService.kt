package com.example.groceryapp.services

import com.example.groceryapp.models.Cart
import com.example.groceryapp.models.CartItem
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

    suspend fun addItem(userId: String, productId: String, quantity: Int): Result<Cart> {
        if (quantity <= 0) return Result.failure(Exception("Quantity must be greater than zero"))

        val product = productRepository.findById(productId) ?: return Result.failure(Exception("Product not found"))
        if (!product.isAvailable) return Result.failure(Exception("Product is not available"))

        val cart = getCart(userId)
        val existingItems = cart.items.toMutableList()
        val existingItemIndex = existingItems.indexOfFirst { it.productId == productId }

        if (existingItemIndex != -1) {
            val existingItem = existingItems[existingItemIndex]
            existingItems[existingItemIndex] = existingItem.copy(
                quantity = existingItem.quantity + quantity,
                priceAtAdd = product.price // Update to authoritative current price
            )
        } else {
            existingItems.add(CartItem(productId, quantity, product.price))
        }

        val updatedCart = cart.copy(items = existingItems, updatedAt = Instant.now().toString())
        cartRepository.saveCart(updatedCart)
        return Result.success(updatedCart)
    }

    suspend fun updateQuantity(userId: String, productId: String, quantity: Int): Result<Cart> {
        if (quantity <= 0) return removeItem(userId, productId)

        val cart = cartRepository.getCartByUserId(userId) ?: return Result.failure(Exception("Cart not found"))
        val existingItems = cart.items.toMutableList()
        val itemIndex = existingItems.indexOfFirst { it.productId == productId }

        if (itemIndex == -1) return Result.failure(Exception("Item not found in cart"))

        // Fetch authoritative price again to ensure it is current
        val product = productRepository.findById(productId) ?: return Result.failure(Exception("Product not found"))
        
        existingItems[itemIndex] = existingItems[itemIndex].copy(
            quantity = quantity,
            priceAtAdd = product.price
        )

        val updatedCart = cart.copy(items = existingItems, updatedAt = Instant.now().toString())
        cartRepository.saveCart(updatedCart)
        return Result.success(updatedCart)
    }

    suspend fun removeItem(userId: String, productId: String): Result<Cart> {
        val cart = cartRepository.getCartByUserId(userId) ?: return Result.failure(Exception("Cart not found"))
        val existingItems = cart.items.toMutableList()
        
        if (existingItems.removeIf { it.productId == productId }) {
            val updatedCart = cart.copy(items = existingItems, updatedAt = Instant.now().toString())
            cartRepository.saveCart(updatedCart)
            return Result.success(updatedCart)
        }
        
        return Result.failure(Exception("Item not found in cart"))
    }

    suspend fun clearCart(userId: String): Result<Unit> {
        cartRepository.deleteCart(userId)
        return Result.success(Unit)
    }

    private fun createEmptyCart(userId: String): Cart {
        return Cart(userId = userId, items = emptyList(), updatedAt = Instant.now().toString())
    }
}
