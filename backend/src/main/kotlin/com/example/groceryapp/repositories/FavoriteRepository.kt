package com.example.groceryapp.repositories

import com.example.groceryapp.database.AppDatabase
import com.example.groceryapp.models.Favorite
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId

class FavoriteRepository {
    private val database = AppDatabase.getDatabase()
    private val collection = database.getCollection<Favorite>("favorites")

    suspend fun findAllByUserId(userId: String): List<Favorite> {
        return collection.find(Filters.eq("userId", userId)).toList()
    }

    suspend fun findByUserAndProduct(userId: String, productId: String): Favorite? {
        return collection.find(
            Filters.and(
                Filters.eq("userId", userId),
                Filters.eq("productId", productId)
            )
        ).firstOrNull()
    }

    suspend fun create(favorite: Favorite): Favorite {
        val result = collection.insertOne(favorite)
        return favorite.copy(id = result.insertedId?.asObjectId()?.value?.toHexString())
    }

    suspend fun delete(userId: String, productId: String): Boolean {
        val result = collection.deleteOne(
            Filters.and(
                Filters.eq("userId", userId),
                Filters.eq("productId", productId)
            )
        )
        return result.deletedCount > 0
    }
}
