package com.example.groceryapp.services

import com.example.groceryapp.models.Favorite
import com.example.groceryapp.models.Product
import com.example.groceryapp.repositories.FavoriteRepository
import com.example.groceryapp.repositories.ProductRepository
import java.time.Instant

class FavoriteService(
    private val favoriteRepository: FavoriteRepository = FavoriteRepository(),
    private val productRepository: ProductRepository = ProductRepository()
) {

    suspend fun getFavorites(userId: String): List<Product> {
        val favorites = favoriteRepository.findAllByUserId(userId)
        return favorites.mapNotNull { favorite ->
            productRepository.findById(favorite.productId)
        }
    }

    suspend fun addFavorite(userId: String, productId: String): Result<Unit> {
        val product = productRepository.findById(productId) ?: return Result.failure(Exception("Product not found"))
        
        val existing = favoriteRepository.findByUserAndProduct(userId, productId)
        if (existing != null) return Result.success(Unit) // Already favorited

        val favorite = Favorite(
            userId = userId,
            productId = productId,
            createdAt = Instant.now().toString()
        )
        favoriteRepository.create(favorite)
        return Result.success(Unit)
    }

    suspend fun removeFavorite(userId: String, productId: String): Result<Unit> {
        val deleted = favoriteRepository.delete(userId, productId)
        return if (deleted) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Favorite not found"))
        }
    }

    suspend fun isFavorited(userId: String, productId: String): Boolean {
        return favoriteRepository.findByUserAndProduct(userId, productId) != null
    }
}
