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
        val existingUser = usersCollection.find(Filters.eq("email", dummyEmail)).firstOrNull()
        
        if (existingUser == null) {
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
            println("Admin user seeded: $dummyEmail")
        }

        // Force reset product/category tables to populate full commercial 60+ item portfolio
        categoriesCollection.deleteMany(Filters.empty())
        productsCollection.deleteMany(Filters.empty())

        // 2. Seed Premium Commercial Categories
        val categoriesList = listOf(
            Category(name = "Vegetables", icon = "🥬", imageUrl = "https://images.unsplash.com/photo-1566385101042-1a0aa0c12e8c?w=400&q=80", displayOrder = 1),
            Category(name = "Fruits", icon = "🍎", imageUrl = "https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=400&q=80", displayOrder = 2),
            Category(name = "Dairy & Eggs", icon = "🥛", imageUrl = "https://images.unsplash.com/photo-1628088062854-d1870b4553da?w=400&q=80", displayOrder = 3),
            Category(name = "Bakery & Bread", icon = "🍞", imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=400&q=80", displayOrder = 4),
            Category(name = "Meat & Protein", icon = "🥩", imageUrl = "https://images.unsplash.com/photo-1604503468506-a8da13d82791?w=400&q=80", displayOrder = 5),
            Category(name = "Pantry Essentials", icon = "🥫", imageUrl = "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=400&q=80", displayOrder = 6),
            Category(name = "Snacks & Treats", icon = "🍫", imageUrl = "https://images.unsplash.com/photo-1566478989037-eec170784d0b?w=400&q=80", displayOrder = 7),
            Category(name = "Beverages", icon = "🥤", imageUrl = "https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=400&q=80", displayOrder = 8)
        )
        categoriesCollection.insertMany(categoriesList)

        val seededCategories = categoriesCollection.find().toList()
        val vegId = seededCategories.find { it.name == "Vegetables" }?.id ?: "veg"
        val fruitId = seededCategories.find { it.name == "Fruits" }?.id ?: "fruit"
        val dairyId = seededCategories.find { it.name == "Dairy & Eggs" }?.id ?: "dairy"
        val bakeryId = seededCategories.find { it.name == "Bakery & Bread" }?.id ?: "bakery"
        val meatId = seededCategories.find { it.name == "Meat & Protein" }?.id ?: "meat"
        val pantryId = seededCategories.find { it.name == "Pantry Essentials" }?.id ?: "pantry"
        val snackId = seededCategories.find { it.name == "Snacks & Treats" }?.id ?: "snack"
        val beverageId = seededCategories.find { it.name == "Beverages" }?.id ?: "beverage"

        val premiumProducts = mutableListOf<Product>()

        // Helper to add items quickly
        fun addProd(name: String, desc: String, cat: String, prc: Double, unt: String, img: String, stock: Int = 80) {
            premiumProducts.add(
                Product(
                    name = name, description = desc, categoryId = cat, price = prc, unit = unt,
                    imageUrl = img, stockQuantity = stock, isAvailable = true, createdAt = Instant.now().toString()
                )
            )
        }

        // --- 1. VEGETABLES (8 products) ---
        addProd("Fresh Organic Spinach", "Farm-fresh organic green spinach leaves, perfect for vibrant salads and healthy cooking.", vegId, 1.99, "250g", "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400&q=80")
        addProd("Vine-Ripened Tomatoes", "Juicy and plump red vine tomatoes, rich in sweet flavor and antioxidants.", vegId, 2.49, "1kg", "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=400&q=80")
        addProd("Fresh Broccoli Crowns", "Crisp, nutrient-dense green broccoli heads directly harvested from local farms.", vegId, 1.89, "500g", "https://images.unsplash.com/photo-1459411621453-7b03977f4bfc?w=400&q=80")
        addProd("Organic Crisp Carrots", "Sweet, crunchy premium carrots rich in beta-carotene and vital nutrients.", vegId, 1.49, "1kg", "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=400&q=80")
        addProd("Bell Peppers Trio Pack", "A beautiful assortment of premium red, yellow, and green sweet bell peppers.", vegId, 3.29, "3 pack", "https://images.unsplash.com/photo-1563565375-f3fdfdbefa83?w=400&q=80")
        addProd("Fresh Iceberg Lettuce", "Crisp, refreshing, and clean iceberg lettuce, ideal for custom burgers and fresh tacos.", vegId, 1.79, "1 head", "https://images.unsplash.com/photo-1622206151226-18ca2c9ab4a1?w=400&q=80")
        addProd("Organic English Cucumber", "Hydrating, thin-skinned crisp English seedless cucumbers for salads.", vegId, 1.25, "1 piece", "https://images.unsplash.com/photo-1604977042946-1eecc30f269e?w=400&q=80")
        addProd("Fresh Red Onions", "Pungent and flavorful premium red onions with brilliant ruby coloring.", vegId, 1.69, "1kg", "https://images.unsplash.com/photo-1618228943037-a2efade64be9?w=400&q=80")

        // --- 2. FRUITS (8 products) ---
        addProd("Gala Red Apples", "Extra sweet, crunchy imported Gala apples with bright red skins.", fruitId, 3.49, "1kg", "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400&q=80")
        addProd("Organic Cavendish Bananas", "Perfectly ripe, naturally sweet golden yellow organic bananas bundle.", fruitId, 1.89, "1 bundle", "https://images.unsplash.com/photo-1603833665858-e61d17a86224?w=400&q=80")
        addProd("Fresh Sweet Strawberries", "Plump, delicious, freshly picked garden strawberries full of juicy flavor.", fruitId, 2.99, "400g", "https://images.unsplash.com/photo-1464965911861-746a04b4bca6?w=400&q=80")
        addProd("Juicy Navel Oranges", "Zesty, rich in Vitamin C, high-juice sweet seedless navel oranges.", fruitId, 2.79, "1kg", "https://images.unsplash.com/photo-1547514701-42782101795e?w=400&q=80")
        addProd("Premium Honey Mangoes", "Velvety, fiberless luxury mangoes boasting a heavenly sweet tropical fragrance.", fruitId, 4.99, "1kg", "https://images.unsplash.com/photo-1553279768-865429fa0078?w=400&q=80")
        addProd("Seedless Red Watermelon", "Crisp, deeply hydrating premium seedless sweet watermelon slices.", fruitId, 5.49, "1 piece", "https://images.unsplash.com/photo-1587049352846-4a222e784778?w=400&q=80")
        addProd("Fresh Seedless Green Grapes", "Crispy, sweet, popping green grapes sourced from prime global vineyards.", fruitId, 3.99, "500g", "https://images.unsplash.com/photo-1601570465377-fb4a99008a4a?w=400&q=80")
        addProd("Organic Fresh Blueberries", "Antioxidant-rich, superfood fresh organic plump sweet blueberries.", fruitId, 3.25, "125g", "https://images.unsplash.com/photo-1601004890684-d8cbf643f5f2?w=400&q=80")

        // --- 3. DAIRY & EGGS (8 products) ---
        addProd("Whole Farm Milk", "Creamy, homogenized organic whole pasteurized cow's milk from happy fields.", dairyId, 3.19, "1 Gallon", "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=400&q=80")
        addProd("Authentic Greek Yogurt", "Thick, ultra-creamy traditional unsweetened plain Greek yogurt bowl.", dairyId, 4.49, "500g", "https://images.unsplash.com/photo-1571212515416-fca2e2218d1f?w=400&q=80")
        addProd("Aged Cheddar Cheese Block", "Sharp, rich, deeply flavor-aged gourmet cheddar cheese block.", dairyId, 3.89, "250g", "https://images.unsplash.com/photo-1452195100486-9cc805987862?w=400&q=80")
        addProd("Pasture-Raised Brown Eggs", "Large, healthy farm brown eggs rich in high-quality protein and nutrients.", dairyId, 4.29, "12 pack", "https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?w=400&q=80")
        addProd("Organic Creamery Butter", "Rich, churned cream golden salted premium creamery butter.", dairyId, 2.99, "200g", "https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=400&q=80")
        addProd("Fresh Organic Cottage Cheese", "Low-fat, high-protein soft curds fresh cottage cheese snack tub.", dairyId, 2.79, "400g", "https://images.unsplash.com/photo-1551462147-ff29053bfc14?w=400&q=80")
        addProd("Premium Whipping Cream", "Rich culinary whipping cream, perfect for fine dessert toppings and gourmet sauces.", dairyId, 2.19, "250ml", "https://images.unsplash.com/photo-1528750951167-53d4a5c9b43c?w=400&q=80")
        addProd("Probiotic Almond Milk", "Creamy, plant-based unsweetened alternative almond nut drink milk.", dairyId, 3.59, "1L", "https://images.unsplash.com/photo-1568651350595-30d5a6e4ccac?w=400&q=80")

        // --- 4. BAKERY & BREAD (8 products) ---
        addProd("Artisanal Whole Wheat Bread", "Freshly baked, stone-ground high-fiber whole grain wheat sandwich loaf.", bakeryId, 2.69, "500g", "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=400&q=80")
        addProd("All-Butter French Croissants", "Flaky, multi-layered golden butter croissants straight from the oven.", bakeryId, 3.99, "4 pack", "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=400&q=80")
        addProd("New York Style Bagels", "Chewy, dense premium bakery bagels coated with premium sesame seeds.", bakeryId, 3.49, "5 pack", "https://images.unsplash.com/photo-1612970230037-e2f0066e56d4?w=400&q=80")
        addProd("Traditional French Baguette", "Crispy crust, light airy center long authentic Parisian bread baguette.", bakeryId, 1.99, "1 piece", "https://images.unsplash.com/photo-1549931319-a545dcf3bc73?w=400&q=80")
        addProd("Gourmet Blueberry Muffins", "Soft, moist café-style sweet muffins bursting with real whole blueberries.", bakeryId, 4.25, "4 pack", "https://images.unsplash.com/photo-1607958996333-41aef7caefaa?w=400&q=80")
        addProd("Soft Honey Dinner Rolls", "Fluffy, golden sweet honey-glazed bakery fresh dinner buns.", bakeryId, 2.89, "12 pack", "https://images.unsplash.com/photo-1543340713-140304523363?w=400&q=80")
        addProd("Gluten-Free Seeded Bread", "Wholesome gluten-free artisanal loaf baked with nutritious ancient grains.", bakeryId, 4.79, "400g", "https://images.unsplash.com/photo-1586444248902-2f64eddc13df?w=400&q=80")
        addProd("Chocolate Chip Soft Cookies", "Decadent bakery cookies loaded with melting premium Belgian chocolate chips.", bakeryId, 3.49, "6 pack", "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=400&q=80")

        // --- 5. MEAT & PROTEIN (8 products) ---
        addProd("Organic Chicken Breasts", "Lean, skinless boneless premium farm chicken breast cutlets.", meatId, 8.99, "500g", "https://images.unsplash.com/photo-1604503468506-a8da13d82791?w=400&q=80")
        addProd("Premium Ribeye Steak", "Marbled, juicy, ultra-tender choice beef ribeye cut for gourmet steak.", meatId, 15.99, "400g", "https://images.unsplash.com/photo-1588168333986-5078d3ae3976?w=400&q=80")
        addProd("Fresh Atlantic Salmon Fillet", "Rich in Omega-3, vibrant pink cold-water ocean salmon fillet.", meatId, 12.49, "300g", "https://images.unsplash.com/photo-1611171711912-e18a2d1bdc82?w=400&q=80")
        addProd("Lean Ground Beef", "85% lean premium grass-fed ground beef, ideal for juicy grill burgers.", meatId, 6.49, "500g", "https://images.unsplash.com/photo-1607623814075-e51df1bdc82f?w=400&q=80")
        addProd("Smoked Lean Turkey Slices", "Premium deli thin deli-cut natural hardwood-smoked turkey breast.", meatId, 4.99, "200g", "https://images.unsplash.com/photo-1544025162-d76694265947?w=400&q=80")
        addProd("Premium Pork Chop Cuts", "Thick-cut tender bone-in pork loin chops from local ranches.", meatId, 7.89, "600g", "https://images.unsplash.com/photo-1602484299166-08e1a47d6df8?w=400&q=80")
        addProd("Fresh Cold-Water Shrimps", "Peeled, deveined, fresh sweet juicy oceanic white dinner shrimps.", meatId, 9.99, "400g", "https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=400&q=80")
        addProd("Gourmet Breakfast Sausages", "Savory heritage pork sausages infused with subtle aromatic sage and herbs.", meatId, 4.39, "350g", "https://images.unsplash.com/photo-1532242117846-ac00f5011a87?w=400&q=80")

        // --- 6. PANTRY ESSENTIALS (8 products) ---
        addProd("Italian Penne Rigate Pasta", "100% durum wheat semolina authentic coarse-cut premium penne pasta.", pantryId, 1.79, "500g", "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=400&q=80")
        addProd("Premium Basmati Rice", "Aromatic, long-grain slender aged fragrant pure white Basmati rice.", pantryId, 4.99, "2kg", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400&q=80")
        addProd("Extra Virgin Olive Oil", "Cold-pressed first-harvest Mediterranean cooking extra virgin olive oil.", pantryId, 9.49, "750ml", "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=400&q=80")
        addProd("Raw Organic Clover Honey", "100% pure, unfiltered golden honey gathered from lush organic pastures.", pantryId, 5.25, "400g", "https://images.unsplash.com/photo-1587049352846-4a222e784778?w=400&q=80")
        addProd("Organic Tomato Basil Sauce", "Rich tomato paste simmered with fragrant sweet basil leaves and olive oil.", pantryId, 2.89, "650g", "https://images.unsplash.com/photo-1601314002592-b8734bca6604?w=400&q=80")
        addProd("Iodized Fine Sea Salt", "Pure mineral fine-grain sea salt container for ultimate kitchen seasoning.", pantryId, 1.15, "500g", "https://images.unsplash.com/photo-1608797178974-15b35a61d121?w=400&q=80")
        addProd("Organic Apple Cider Vinegar", "Unfiltered wellness apple cider vinegar with 'the mother' enzyme culture.", pantryId, 3.79, "500ml", "https://images.unsplash.com/photo-1622484211148-716499879796?w=400&q=80")
        addProd("Premium Canned Black Beans", "Tender, high-protein black turtle beans soaked and ready for meals.", pantryId, 1.29, "400g", "https://images.unsplash.com/photo-1551248429-4043bcaad3bd?w=400&q=80")

        // --- 7. SNACKS & TREATS (8 products) ---
        addProd("Sea Salt Potato Chips", "Hand-cooked, extra-crunchy kettle style sea salted thin potato chips.", snackId, 2.39, "150g", "https://images.unsplash.com/photo-1566478989037-eec170784d0b?w=400&q=80")
        addProd("Dark Chocolate Bar 70%", "Rich Belgian dark cocoa bar with velvet texture and subtle fruit notes.", snackId, 2.99, "100g", "https://images.unsplash.com/photo-1481391243133-f96216dcb5d2?w=400&q=80")
        addProd("Cinema Butter Popcorn", "Light, fluffy gourmet popped corn fully glazed with rich savory butter.", snackId, 1.99, "100g", "https://images.unsplash.com/photo-1578849278619-e73505e9610f?w=400&q=80")
        addProd("Roasted Salted Cashews", "Premium jumbo cashew nuts roasted dry and sprinkled with fine sea salt.", snackId, 5.49, "200g", "https://images.unsplash.com/photo-1534119394531-eecc30f269e?w=400&q=80")
        addProd("Fruit Jelly Gummy Bears", "Chewy, delicious fruit juice concentrates shaped into colorful gummy candy.", snackId, 1.79, "250g", "https://images.unsplash.com/photo-1581798459219-318e76aecc7b?w=400&q=80")
        addProd("Organic Tortilla Nachos", "Crisp golden yellow corn tortilla nacho chips for dipping salsa.", snackId, 2.69, "200g", "https://images.unsplash.com/photo-1518047601542-79f18c655718?w=400&q=80")
        addProd("Baked Rice Crackers", "Light Asian savory rice crackers baked crispy and seasoned with soy glaze.", snackId, 2.25, "120g", "https://images.unsplash.com/photo-1613082442327-0bb8ef19cb9f?w=400&q=80")
        addProd("Peanut Butter Cups", "Creamy milk chocolate shells packed tight with delicious rich salty peanut butter.", snackId, 3.19, "4 pack", "https://images.unsplash.com/photo-1590080875515-8a3a8dc5735e?w=400&q=80")

        // --- 8. BEVERAGES (8 products) ---
        addProd("Cold Pressed Orange Juice", "100% pure squeezed Florida orange juice with rich natural sweet pulp.", beverageId, 3.89, "1L", "https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=400&q=80")
        addProd("Arabica Coffee Beans", "Medium roasted specialty organic Arabica coffee whole beans block.", beverageId, 8.49, "500g", "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=400&q=80")
        addProd("Organic Japanese Green Tea", "Soothing, pure organic sencha green tea sachets packed with antioxidants.", beverageId, 3.99, "20 pack", "https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=400&q=80")
        addProd("Pure Spring Mineral Water", "Crisp, cold alpine spring drinking water bottled directly at the source.", beverageId, 1.25, "1.5L", "https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&q=80")
        addProd("Sparkling Lime Soda", "Fizzy, carbonated pure mineral water infused with refreshing natural key lime.", beverageId, 1.89, "4 pack", "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=400&q=80")
        addProd("Organic Tomato Juice", "Savory vine tomato juice seasoned perfectly with a splash of fine sea salt.", beverageId, 2.49, "1L", "https://images.unsplash.com/photo-1546173159-315724a31696?w=400&q=80")
        addProd("Natural Sweet Apple Cider", "Pressed sweet orchard apples spiced with a hint of warm ground cinnamon.", beverageId, 3.79, "1L", "https://images.unsplash.com/photo-1559525347-fe9fa0dbd9f9?w=400&q=80")
        addProd("Probiotic Ginger Kombucha", "Fermented effervescent living green tea culture bursting with spicy organic ginger root.", beverageId, 3.29, "450ml", "https://images.unsplash.com/photo-1594911771146-a492f2b3e41b?w=400&q=80")

        productsCollection.insertMany(premiumProducts)
        println("✅ Successfully seeded ${premiumProducts.size} premium products across 8 categories.")
    }
}
