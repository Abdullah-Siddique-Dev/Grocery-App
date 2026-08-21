package com.example.groceryapp.repositories

import com.example.groceryapp.database.AppDatabase
import com.example.groceryapp.models.Order
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId

class OrderRepository {
    private val database = AppDatabase.getDatabase()
    private val collection = database.getCollection<Order>("orders")

    suspend fun create(order: Order): Order {
        val result = collection.insertOne(order)
        return order.copy(id = result.insertedId?.asObjectId()?.value?.toHexString())
    }

    suspend fun findAllByUserId(userId: String): List<Order> {
        return collection.find(Filters.eq("userId", userId))
            .sort(Sorts.descending("placedAt"))
            .toList()
    }

    suspend fun findById(userId: String, orderId: String): Order? {
        val bsonId = try { ObjectId(orderId) } catch (e: Exception) { return null }
        return collection.find(
            Filters.and(
                Filters.eq("_id", bsonId),
                Filters.eq("userId", userId)
            )
        ).firstOrNull()
    }
}
