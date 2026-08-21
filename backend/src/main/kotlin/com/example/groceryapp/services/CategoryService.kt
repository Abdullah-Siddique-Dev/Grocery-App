package com.example.groceryapp.services

import com.example.groceryapp.models.Category
import com.example.groceryapp.repositories.CategoryRepository

class CategoryService(private val repository: CategoryRepository = CategoryRepository()) {
    suspend fun getAllCategories(): List<Category> {
        return repository.findAll()
    }
}
