package com.example.groceryapp.repositories

import com.example.groceryapp.database.AppDatabase
import com.example.groceryapp.models.Category
import kotlinx.coroutines.flow.toList

class CategoryRepository {
    private val database = AppDatabase.getDatabase()
    private val collection = database.getCollection<Category>("categories")

    suspend fun findAll(): List<Category> {
        return collection.find().toList()
    }
}
