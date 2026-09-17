package com.example.groceryapp.data.repository

import com.example.groceryapp.data.cache.CacheManager
import com.example.groceryapp.data.dto.CategoryDto
import com.example.groceryapp.data.dto.toDomain
import com.example.groceryapp.data.network.ApiClient
import com.example.groceryapp.data.network.InMemoryTokenProvider
import com.example.groceryapp.domain.model.Category
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration.Companion.minutes

/**
 * Category repository with aggressive caching.
 * 
 * Caching strategy:
 * - Categories change rarely, so cache for 10 minutes
 * - Very high cache hit rate expected (90%+)
 * 
 * Expected performance:
 * - Cache hit: <50ms (instant)
 * - Cache miss: 300-500ms (network)
 */
class CategoryRepository(
    private val apiClient: ApiClient = ApiClient(InMemoryTokenProvider.getInstance())
) : BaseRepository() {
    
    private val categoriesCache = CacheManager<List<Category>>()
    
    /**
     * Get all categories.
     * Results are cached for 10 minutes.
     */
    suspend fun getCategories(): Flow<Result<List<Category>>> {
        return fetchWithCache(
            cacheKey = "all_categories",
            cache = categoriesCache,
            ttl = 10.minutes,
            transform = { dtos: List<CategoryDto> -> dtos.map { it.toDomain() } }
        ) {
            apiClient.client.get("/categories")
        }
    }
    
    /**
     * Invalidate category cache.
     * Call this after creating/updating/deleting categories.
     */
    suspend fun invalidateCache() {
        categoriesCache.clear()
    }
}
