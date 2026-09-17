package com.example.groceryapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.groceryapp.presentation.admin.*
import com.example.groceryapp.presentation.auth.LoginScreen
import com.example.groceryapp.presentation.auth.RegisterScreen
import com.example.groceryapp.presentation.cart.CartScreen
import com.example.groceryapp.presentation.categories.CategoriesScreen
import com.example.groceryapp.presentation.favorites.FavoritesScreen
import com.example.groceryapp.presentation.home.HomeScreen
import com.example.groceryapp.presentation.orders.CheckoutScreen
import com.example.groceryapp.presentation.orders.OrderDetailsScreen
import com.example.groceryapp.presentation.orders.OrdersScreen
import com.example.groceryapp.presentation.products.ProductDetailsScreen
import com.example.groceryapp.presentation.products.ProductsScreen
import com.example.groceryapp.presentation.profile.ProfileScreen
import com.example.groceryapp.presentation.notifications.NotificationsScreen
import com.example.groceryapp.presentation.offers.OffersScreen
import com.example.groceryapp.presentation.orders.OrderTrackingScreen
import com.example.groceryapp.presentation.help.HelpScreen
import com.example.groceryapp.ui.theme.EmeraldPrimary
import com.example.groceryapp.ui.theme.TextMuted

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem(
        Screen.Home.route, 
        "Home", 
        Icons.Filled.Home, 
        Icons.Outlined.Home
    )
    object Categories : BottomNavItem(
        Screen.Categories.route, 
        "Browse", 
        Icons.Filled.Search, 
        Icons.Outlined.Search
    )
    object Cart : BottomNavItem(
        Screen.Cart.route, 
        "Cart", 
        Icons.Filled.ShoppingCart, 
        Icons.Outlined.ShoppingCart
    )
    object Orders : BottomNavItem(
        Screen.Orders.route, 
        "Orders", 
        Icons.Filled.ReceiptLong, 
        Icons.Outlined.ReceiptLong
    )
    object Profile : BottomNavItem(
        Screen.Profile.route, 
        "Profile", 
        Icons.Filled.Person, 
        Icons.Outlined.Person
    )
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Categories,
        BottomNavItem.Cart,
        BottomNavItem.Orders,
        BottomNavItem.Profile
    )

    val showBottomNav = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            icon = { 
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon, 
                                    contentDescription = item.title 
                                ) 
                            },
                            label = { Text(item.title) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldPrimary,
                                selectedTextColor = EmeraldPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
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
                    onNavigateToProducts = { categoryId -> 
                        navController.navigate(Screen.Products.createRoute(categoryId)) 
                    },
                    onNavigateToProductDetails = { productId ->
                        navController.navigate(Screen.ProductDetails.createRoute(productId))
                    },
                    onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                    onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                    onNavigateToAdmin = { navController.navigate(Screen.AdminDashboard.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToOffers = { navController.navigate(Screen.Offers.route) }
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
                    onBack = { navController.popBackStack() },
                    onNavigateToTracking = { id -> 
                        navController.navigate(Screen.OrderTracking.createRoute(id)) 
                    }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() },
                    onNavigateToHelp = { navController.navigate(Screen.Help.route) }
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onProductClick = { productId ->
                        navController.navigate(Screen.ProductDetails.createRoute(productId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // New Feature Screens
            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Offers.route) {
                OffersScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.OrderTracking.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderTrackingScreen(
                    orderId = orderId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Help.route) {
                HelpScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Admin
            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(
                    onNavigateToOrders = { navController.navigate(Screen.AdminOrders.route) },
                    onNavigateToProducts = { navController.navigate(Screen.AdminProducts.route) },
                    onNavigateToCategories = { navController.navigate(Screen.AdminCategories.route) },
                    onNavigateToUsers = { navController.navigate(Screen.AdminUsers.route) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.AdminOrders.route) {
                AdminOrdersScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.AdminProducts.route) {
                AdminProductsScreen(
                    onEditProduct = { productId ->
                        navController.navigate(Screen.AdminProductEdit.createRoute(productId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.AdminProductEdit.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: "new"
                AdminProductEditScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.AdminCategories.route) {
                AdminCategoriesScreen(
                    onEditCategory = { categoryId ->
                        navController.navigate(Screen.AdminCategoryEdit.createRoute(categoryId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.AdminCategoryEdit.route,
                arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: "new"
                AdminCategoryEditScreen(
                    categoryId = categoryId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.AdminUsers.route) {
                AdminUsersScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
