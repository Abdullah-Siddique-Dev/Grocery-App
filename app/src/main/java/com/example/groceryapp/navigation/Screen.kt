package com.example.groceryapp.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Categories : Screen("categories")
    object Products : Screen("products?categoryId={categoryId}") {
        fun createRoute(categoryId: String? = null) = if (categoryId != null) "products?categoryId=$categoryId" else "products"
    }
    object ProductDetails : Screen("product_details/{productId}") {
        fun createRoute(productId: String) = "product_details/$productId"
    }
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object Orders : Screen("orders")
    object OrderDetails : Screen("order_details/{orderId}") {
        fun createRoute(orderId: String) = "order_details/$orderId"
    }
    object Profile : Screen("profile")
}
