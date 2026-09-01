package com.example.groceryapp.services

import com.example.groceryapp.models.Category
import com.example.groceryapp.repositories.CategoryRepository

class CategoryService(
    private val repository: CategoryRepository = CategoryRepository(),
    private val productRepository: com.example.groceryapp.repositories.ProductRepository = com.example.groceryapp.repositories.ProductRepository()
) {
    suspend fun getAllCategories(): List<Category> {
        return repository.findAll()
    }

    suspend fun createCategory(category: Category): Result<Category> {
        if (category.name.isBlank()) return Result.failure(Exception("Category name is required"))
        return try {
            val created = repository.create(category)
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCategory(id: String, category: Category): Result<Category> {
        if (category.name.isBlank()) return Result.failure(Exception("Category name is required"))
        return try {
            val updated = repository.update(id, category)
            if (updated != null) Result.success(updated)
            else Result.failure(Exception("Category not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(id: String): Result<Unit> {
        // Check if there are products in this category
        val products = productRepository.find(categoryId = id)
        if (products.isNotEmpty()) {
            return Result.failure(Exception("Cannot delete category with associated products"))
        }

        return try {
            val success = repository.delete(id)
            if (success) Result.success(Unit)
            else Result.failure(Exception("Category not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
