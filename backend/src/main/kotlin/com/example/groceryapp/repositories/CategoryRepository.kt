package com.example.groceryapp.repositories

import com.example.groceryapp.database.AppDatabase
import com.example.groceryapp.models.Category
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId

class CategoryRepository {
    private val database = AppDatabase.getDatabase()
    private val collection = database.getCollection<Category>("categories")

    suspend fun findAll(): List<Category> {
        return collection.find().toList()
    }

    suspend fun findById(id: String): Category? {
        val bsonId = try { ObjectId(id) } catch (e: Exception) { return null }
        return collection.find(Filters.eq("_id", bsonId)).firstOrNull()
    }

    suspend fun create(category: Category): Category {
        val result = collection.insertOne(category)
        return category.copy(id = result.insertedId?.asObjectId()?.value?.toHexString())
    }

    suspend fun update(id: String, category: Category): Category? {
        val bsonId = try { ObjectId(id) } catch (e: Exception) { return null }
        val filter = Filters.eq("_id", bsonId)
        val update = com.mongodb.client.model.Updates.combine(
            com.mongodb.client.model.Updates.set("name", category.name),
            com.mongodb.client.model.Updates.set("icon", category.icon),
            com.mongodb.client.model.Updates.set("imageUrl", category.imageUrl),
            com.mongodb.client.model.Updates.set("displayOrder", category.displayOrder)
        )
        collection.updateOne(filter, update)
        return findById(id)
    }

    suspend fun delete(id: String): Boolean {
        val bsonId = try { ObjectId(id) } catch (e: Exception) { return false }
        val result = collection.deleteOne(Filters.eq("_id", bsonId))
        return result.deletedCount > 0
    }
}
