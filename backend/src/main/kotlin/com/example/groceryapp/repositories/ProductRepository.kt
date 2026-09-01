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
        val bsonId = try { ObjectId(id) } catch (e: Exception) { return null }
        return collection.find(Filters.eq("_id", bsonId)).firstOrNull()
    }

    suspend fun create(product: Product): Product {
        val result = collection.insertOne(product)
        return product.copy(id = result.insertedId?.asObjectId()?.value?.toHexString())
    }

    suspend fun update(id: String, product: Product): Product? {
        val bsonId = try { ObjectId(id) } catch (e: Exception) { return null }
        val filter = Filters.eq("_id", bsonId)
        val update = com.mongodb.client.model.Updates.combine(
            com.mongodb.client.model.Updates.set("name", product.name),
            com.mongodb.client.model.Updates.set("description", product.description),
            com.mongodb.client.model.Updates.set("categoryId", product.categoryId),
            com.mongodb.client.model.Updates.set("price", product.price),
            com.mongodb.client.model.Updates.set("unit", product.unit),
            com.mongodb.client.model.Updates.set("imageUrl", product.imageUrl),
            com.mongodb.client.model.Updates.set("stockQuantity", product.stockQuantity),
            com.mongodb.client.model.Updates.set("isAvailable", product.isAvailable)
        )
        collection.updateOne(filter, update)
        return findById(id)
    }

    suspend fun delete(id: String): Boolean {
        val bsonId = try { ObjectId(id) } catch (e: Exception) { return false }
        val result = collection.deleteOne(Filters.eq("_id", bsonId))
        return result.deletedCount > 0
    }

    suspend fun decrementStock(productId: String, quantity: Int): Boolean {
        val bsonId = try { ObjectId(productId) } catch (e: Exception) { return false }
        
        // Atomic update: only decrement if enough stock exists
        val result = collection.updateOne(
            Filters.and(
                Filters.eq("_id", bsonId),
                Filters.gte("stockQuantity", quantity)
            ),
            com.mongodb.client.model.Updates.inc("stockQuantity", -quantity)
        )
        
        if (result.modifiedCount > 0) {
            // Check if stock reached zero to auto-set availability
            val updated = findById(productId)
            if (updated != null && updated.stockQuantity == 0) {
                collection.updateOne(Filters.eq("_id", bsonId), com.mongodb.client.model.Updates.set("isAvailable", false))
            }
            return true
        }
        return false
    }

    suspend fun incrementStock(productId: String, quantity: Int): Boolean {
        val bsonId = try { ObjectId(productId) } catch (e: Exception) { return false }
        val result = collection.updateOne(
            Filters.eq("_id", bsonId),
            com.mongodb.client.model.Updates.inc("stockQuantity", quantity)
        )
        
        if (result.modifiedCount > 0) {
            // If we added stock, ensure it's available
            collection.updateOne(Filters.eq("_id", bsonId), com.mongodb.client.model.Updates.set("isAvailable", true))
            return true
        }
        return false
    }
}
