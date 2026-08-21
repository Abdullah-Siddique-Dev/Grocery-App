package com.example.groceryapp.repositories

import com.example.groceryapp.database.AppDatabase
import com.example.groceryapp.models.Cart
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import kotlinx.coroutines.flow.firstOrNull

class CartRepository {
    private val database = AppDatabase.getDatabase()
    private val collection = database.getCollection<Cart>("carts")

    suspend fun getCartByUserId(userId: String): Cart? {
        return collection.find(Filters.eq("userId", userId)).firstOrNull()
    }

    suspend fun saveCart(cart: Cart) {
        val options = ReplaceOptions().upsert(true)
        collection.replaceOne(Filters.eq("userId", cart.userId), cart, options)
    }

    suspend fun deleteCart(userId: String) {
        collection.deleteOne(Filters.eq("userId", userId))
    }
}
