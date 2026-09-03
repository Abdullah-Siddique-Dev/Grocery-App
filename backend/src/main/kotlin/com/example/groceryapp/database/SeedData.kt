package com.example.groceryapp.database

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.groceryapp.models.*
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import java.time.Instant

object SeedData {
    suspend fun seedIfNeeded() {
        val db = AppDatabase.getDatabase()
        val categoriesCollection = db.getCollection<Category>("categories")
        val productsCollection = db.getCollection<Product>("products")
        val usersCollection = db.getCollection<User>("users")

        // 1. Seed Dummy User
        val dummyEmail = "admin@example.com"
        
        // Delete any existing admin user to ensure clean data
        usersCollection.deleteMany(Filters.eq("email", dummyEmail))
        
        // Create fresh admin user
        val passwordHash = BCrypt.withDefaults().hashToString(12, "admin123".toCharArray())
        val dummyUser = User(
            name = "Admin User",
            email = dummyEmail,
            passwordHash = passwordHash,
            phoneNumber = "1234567890",
            address = Address(
                fullName = "Admin User",
                phoneNumber = "1234567890",
                addressLine = "123 Main St",
                city = "City",
                postalCode = "12345"
            ),
            role = UserRole.ADMIN,
            createdAt = Instant.now().toString()
        )
        usersCollection.insertOne(dummyUser)
        println("Admin user seeded: $dummyEmail / admin123")

        // 2. Seed Categories
        if (categoriesCollection.countDocuments() == 0L) {
            val categories = listOf(
                Category(name = "Fruits", icon = "fruit_icon", imageUrl = "https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=500", displayOrder = 1),
                Category(name = "Vegetables", icon = "veg_icon", imageUrl = "https://images.unsplash.com/photo-1566385101042-1a0aa0c12e8c?w=500", displayOrder = 2),
                Category(name = "Dairy", icon = "dairy_icon", imageUrl = "https://images.unsplash.com/photo-1628088062854-d1870b4553da?w=500", displayOrder = 3)
            )
            categoriesCollection.insertMany(categories)
        }

        // 3. Seed Products
        if (productsCollection.countDocuments() == 0L) {
            val categories = categoriesCollection.find().toList()
            val fruitsId = categories.find { it.name == "Fruits" }?.id ?: "fruits"
            val vegsId = categories.find { it.name == "Vegetables" }?.id ?: "vegs"
            val dairyId = categories.find { it.name == "Dairy" }?.id ?: "dairy"

            val products = listOf(
                Product(
                    name = "Red Apple",
                    description = "Fresh and crunchy red apples from the orchard.",
                    categoryId = fruitsId,
                    price = 2.99,
                    unit = "kg",
                    imageUrl = "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=500",
                    stockQuantity = 100,
                    isAvailable = true,
                    createdAt = Instant.now().toString()
                ),
                Product(
                    name = "Organic Carrot",
                    description = "Organic carrots rich in beta-carotene.",
                    categoryId = vegsId,
                    price = 1.50,
                    unit = "kg",
                    imageUrl = "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=500",
                    stockQuantity = 200,
                    isAvailable = true,
                    createdAt = Instant.now().toString()
                ),
                Product(
                    name = "Fresh Milk",
                    description = "Whole milk from local dairy farms.",
                    categoryId = dairyId,
                    price = 3.49,
                    unit = "L",
                    imageUrl = "https://images.unsplash.com/photo-1563636619-e9107da4a7bb?w=500",
                    stockQuantity = 50,
                    isAvailable = true,
                    createdAt = Instant.now().toString()
                )
            )
            productsCollection.insertMany(products)
            println("Initial products seeded.")
        } else {
            // Update existing products with image URLs if they are missing
            val existingProducts = productsCollection.find().toList()
            existingProducts.forEach { product ->
                if (product.imageUrl.isBlank() || product.imageUrl.startsWith("http://placeholder")) {
                    val newUrl = when {
                        product.name.contains("Apple", true) -> "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=500"
                        product.name.contains("Carrot", true) -> "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=500"
                        product.name.contains("Milk", true) -> "https://images.unsplash.com/photo-1563636619-e9107da4a7bb?w=500"
                        else -> "https://images.unsplash.com/photo-1542838132-92c53300491e?w=500" // Default grocery image
                    }
                    productsCollection.updateOne(
                        Filters.eq("_id", product.id),
                        com.mongodb.client.model.Updates.set("imageUrl", newUrl)
                    )
                }
            }
        }
    }
}
