package com.example.groceryapp.repositories

import com.example.groceryapp.database.AppDatabase
import com.example.groceryapp.models.User
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId

class UserRepository {
    private val database = AppDatabase.getDatabase()
    private val collection = database.getCollection<User>("users")

    suspend fun findByEmail(email: String): User? {
        return collection.find(Filters.eq("email", email)).firstOrNull()
    }

    suspend fun findById(id: String): User? {
        return try {
            collection.find(Filters.eq("_id", ObjectId(id))).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun create(user: User): User {
        val result = collection.insertOne(user)
        return user.copy(id = result.insertedId?.asObjectId()?.value?.toHexString())
    }

    suspend fun update(id: String, name: String, phoneNumber: String, address: String): User? {
        val bsonId = try { ObjectId(id) } catch (e: Exception) { return null }
        val filter = Filters.eq("_id", bsonId)
        val update = com.mongodb.client.model.Updates.combine(
            com.mongodb.client.model.Updates.set("name", name),
            com.mongodb.client.model.Updates.set("phoneNumber", phoneNumber),
            com.mongodb.client.model.Updates.set("address", address)
        )
        collection.updateOne(filter, update)
        return findById(id)
    }
}
