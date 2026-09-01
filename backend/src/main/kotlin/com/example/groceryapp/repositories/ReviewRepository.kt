package com.example.groceryapp.repositories

import com.example.groceryapp.database.AppDatabase
import com.example.groceryapp.models.Review
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId

class ReviewRepository {
    private val database = AppDatabase.getDatabase()
    private val collection = database.getCollection<Review>("reviews")

    suspend fun findAllByProductId(productId: String): List<Review> {
        return collection.find(Filters.eq("productId", productId))
            .sort(Sorts.descending("createdAt"))
            .toList()
    }

    suspend fun findByUserAndProduct(userId: String, productId: String): Review? {
        return collection.find(
            Filters.and(
                Filters.eq("userId", userId),
                Filters.eq("productId", productId)
            )
        ).firstOrNull()
    }

    suspend fun findById(id: String): Review? {
        val bsonId = try { ObjectId(id) } catch (e: Exception) { return null }
        return collection.find(Filters.eq("_id", bsonId)).firstOrNull()
    }

    suspend fun create(review: Review): Review {
        val result = collection.insertOne(review)
        return review.copy(id = result.insertedId?.asObjectId()?.value?.toHexString())
    }

    suspend fun update(id: String, rating: Int, comment: String): Review? {
        val bsonId = try { ObjectId(id) } catch (e: Exception) { return null }
        val update = com.mongodb.client.model.Updates.combine(
            com.mongodb.client.model.Updates.set("rating", rating),
            com.mongodb.client.model.Updates.set("comment", comment)
        )
        collection.updateOne(Filters.eq("_id", bsonId), update)
        return findById(id)
    }

    suspend fun delete(id: String): Boolean {
        val bsonId = try { ObjectId(id) } catch (e: Exception) { return false }
        val result = collection.deleteOne(Filters.eq("_id", bsonId))
        return result.deletedCount > 0
    }
}
