package com.example.groceryapp.repositories

import com.example.groceryapp.database.AppDatabase
import com.example.groceryapp.models.Order
import com.example.groceryapp.models.OrderStatus
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

    suspend fun findAll(): List<Order> {
        return collection.find()
            .sort(Sorts.descending("placedAt"))
            .toList()
    }

    suspend fun findById(orderId: String, userId: String? = null): Order? {
        val bsonId = try { ObjectId(orderId) } catch (e: Exception) { return null }
        val filters = mutableListOf(Filters.eq("_id", bsonId))
        userId?.let { filters.add(Filters.eq("userId", it)) }
        
        return collection.find(Filters.and(filters)).firstOrNull()
    }

    suspend fun updateStatus(orderId: String, newStatus: OrderStatus): Boolean {
        val bsonId = try { ObjectId(orderId) } catch (e: Exception) { return false }
        val result = collection.updateOne(
            Filters.eq("_id", bsonId),
            com.mongodb.client.model.Updates.set("status", newStatus)
        )
        return result.modifiedCount > 0
    }
}
