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
}
