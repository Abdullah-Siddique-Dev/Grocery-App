package com.example.groceryapp.database

import com.example.groceryapp.models.Category
import com.example.groceryapp.models.Product
import kotlinx.coroutines.flow.count
import java.time.Instant

object SeedData {
    suspend fun seedIfNeeded() {
        val db = AppDatabase.getDatabase()
        val categoriesCollection = db.getCollection<Category>("categories")
        val productsCollection = db.getCollection<Product>("products")

        if (categoriesCollection.countDocuments() == 0L) {
            val categories = listOf(
                Category(name = "Fruits", icon = "fruit_icon", imageUrl = "fruits.jpg", displayOrder = 1),
                Category(name = "Vegetables", icon = "veg_icon", imageUrl = "vegs.jpg", displayOrder = 2),
                Category(name = "Dairy", icon = "dairy_icon", imageUrl = "dairy.jpg", displayOrder = 3)
            )
            categoriesCollection.insertMany(categories)
        }

        if (productsCollection.countDocuments() == 0L) {
            // We'd need to fetch category IDs here to link them properly if we wanted real IDs
            // But for a simple seed, we can just insert some products
            val products = listOf(
                Product(
                    name = "Apple",
                    description = "Fresh red apples",
                    categoryId = "placeholder_fruits",
                    price = 2.99,
                    unit = "kg",
                    imageUrl = "apple.jpg",
                    stockQuantity = 100,
                    isAvailable = true,
                    createdAt = Instant.now().toString()
                ),
                Product(
                    name = "Carrot",
                    description = "Crunchy orange carrots",
                    categoryId = "placeholder_vegs",
                    price = 1.50,
                    unit = "kg",
                    imageUrl = "carrot.jpg",
                    stockQuantity = 200,
                    isAvailable = true,
                    createdAt = Instant.now().toString()
                )
            )
            productsCollection.insertMany(products)
        }
    }
}
