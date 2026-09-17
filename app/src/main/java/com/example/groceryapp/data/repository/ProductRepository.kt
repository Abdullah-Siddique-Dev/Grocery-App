package com.example.groceryapp.data.repository

import com.example.groceryapp.data.cache.CacheManager
import com.example.groceryapp.data.dto.ProductDto
import com.example.groceryapp.data.dto.toDomain
import com.example.groceryapp.data.network.ApiClient
import com.example.groceryapp.data.network.InMemoryTokenProvider
import com.example.groceryapp.domain.model.Product
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration.Companion.minutes

/**
 * Product repository with aggressive caching for optimal performance.
 * 
 * Caching strategy:
 * - All products: 5 minutes TTL
 * - Category-filtered: 5 minutes TTL
 * - Search results: 3 minutes TTL
 * - Individual product: 10 minutes TTL
 * 
 * Expected performance:
 * - Cache hit: <50ms (instant)
 * - Cache miss: 500-1000ms (network + transform)
 * - 70%+ cache hit rate in normal usage
 */
class ProductRepository(
    private val apiClient: ApiClient = ApiClient(InMemoryTokenProvider.getInstance())
) : BaseRepository() {
    
    // Separate caches for different query types
    private val productsListCache = CacheManager<List<Product>>()
    private val productDetailCache = CacheManager<Product>()
    
    /**
     * Get products with optional filtering by category and search query.
     * Results are cached for 5 minutes (3 minutes for search).
     */
    suspend fun getProducts(categoryId: String? = null, query: String? = null): Flow<Result<List<Product>>> {
        val cacheKey = buildCacheKey("products", "categoryId" to categoryId, "q" to query)
        val ttl = if (query != null) 3.minutes else 5.minutes
        
        return fetchWithCache(
            cacheKey = cacheKey,
            cache = productsListCache,
            ttl = ttl,
            transform = { dtos: List<ProductDto> -> dtos.map { it.toDomain() } }
        ) {
            apiClient.client.get("/products") {
                if (categoryId != null) parameter("categoryId", categoryId)
                if (query != null) parameter("q", query)
            }
        }
    }
    
    /**
     * Get single product by ID.
     * Results are cached for 10 minutes.
     */
    suspend fun getProductById(productId: String): Flow<Result<Product>> {
        return fetchWithCache(
            cacheKey = "product_$productId",
            cache = productDetailCache,
            ttl = 10.minutes,
            transform = { dto: ProductDto -> dto.toDomain() }
        ) {
            apiClient.client.get("/products/$productId")
        }
    }
    
    /**
     * Invalidate all product caches.
     * Call this after creating/updating/deleting products.
     */
    suspend fun invalidateCache() {
        productsListCache.clear()
        productDetailCache.clear()
    }
}
