package com.example.groceryapp.repositories

import com.example.groceryapp.database.AppDatabase
import com.example.groceryapp.models.Product
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.conversions.Bson
import org.bson.types.ObjectId

class ProductRepository {
    private val database = AppDatabase.getDatabase()
    private val collection = database.getCollection<Product>("products")

    suspend fun find(categoryId: String? = null, query: String? = null): List<Product> {
        val filters = mutableListOf<Bson>()
        
        if (!categoryId.isNullOrBlank()) {
            filters.add(Filters.eq("categoryId", categoryId))
        }
        
        if (!query.isNullOrBlank()) {
            filters.add(Filters.or(
                Filters.regex("name", query, "i"),
                Filters.regex("description", query, "i")
            ))
        }

        val filter = if (filters.isEmpty()) Filters.empty() else Filters.and(filters)
        return collection.find(filter).toList()
    }

    suspend fun findById(id: String): Product? {
        return try {
            collection.find(Filters.eq("_id", ObjectId(id))).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }
}
