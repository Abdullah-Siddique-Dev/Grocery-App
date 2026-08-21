package com.example.groceryapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.groceryapp.presentation.auth.LoginScreen
import com.example.groceryapp.presentation.auth.RegisterScreen
import com.example.groceryapp.presentation.cart.CartScreen
import com.example.groceryapp.presentation.categories.CategoriesScreen
import com.example.groceryapp.presentation.home.HomeScreen
import com.example.groceryapp.presentation.orders.CheckoutScreen
import com.example.groceryapp.presentation.orders.OrderDetailsScreen
import com.example.groceryapp.presentation.orders.OrdersScreen
import com.example.groceryapp.presentation.products.ProductDetailsScreen
import com.example.groceryapp.presentation.products.ProductsScreen
import com.example.groceryapp.presentation.profile.ProfileScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCategories = { navController.navigate(Screen.Categories.route) },
                onNavigateToProducts = { navController.navigate(Screen.Products.createRoute()) },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }
        composable(Screen.Categories.route) {
            CategoriesScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate(Screen.Products.createRoute(categoryId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Products.route,
            arguments = listOf(navArgument("categoryId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
            ProductsScreen(
                categoryId = categoryId,
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetails.createRoute(productId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.ProductDetails.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailsScreen(
                productId = productId,
                onBack = { navController.popBackStack() },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) }
            )
        }
        composable(Screen.Cart.route) {
            CartScreen(
                onBack = { navController.popBackStack() },
                onNavigateToCheckout = { navController.navigate(Screen.Checkout.route) }
            )
        }
        composable(Screen.Checkout.route) {
            CheckoutScreen(
                onOrderPlaced = { orderId ->
                    navController.navigate(Screen.OrderDetails.createRoute(orderId)) {
                        popUpTo(Screen.Cart.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Orders.route) {
            OrdersScreen(
                onOrderClick = { orderId ->
                    navController.navigate(Screen.OrderDetails.createRoute(orderId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.OrderDetails.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailsScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
