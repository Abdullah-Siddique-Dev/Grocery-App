package com.example.groceryapp.services

import com.example.groceryapp.models.Product
import com.example.groceryapp.repositories.ProductRepository

class ProductService(private val repository: ProductRepository = ProductRepository()) {
    suspend fun getProducts(categoryId: String? = null, query: String? = null): List<Product> {
        return repository.find(categoryId, query)
    }

    suspend fun getProductById(id: String): Product? {
        return repository.findById(id)
    }

    suspend fun createProduct(product: Product): Result<Product> {
        if (product.name.isBlank() || product.price <= 0.0 || product.categoryId.isBlank() || product.stockQuantity < 0) {
            return Result.failure(Exception("Valid name, price, categoryId, and non-negative stock are required"))
        }
        return try {
            val created = repository.create(product)
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(id: String, product: Product): Result<Product> {
        if (product.name.isBlank() || product.price <= 0.0 || product.stockQuantity < 0) {
            return Result.failure(Exception("Valid name, price, and non-negative stock are required"))
        }
        return try {
            val updated = repository.update(id, product)
            if (updated != null) Result.success(updated)
            else Result.failure(Exception("Product not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(id: String): Result<Unit> {
        return try {
            val success = repository.delete(id)
            if (success) Result.success(Unit)
            else Result.failure(Exception("Product not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
