package com.example.groceryapp.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceryapp.domain.model.UserRole

@Composable
fun HomeScreen(
    onNavigateToCategories: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "Smart Grocery App",
            style = MaterialTheme.typography.headlineLarge
        )

        if (currentUser?.role == UserRole.ADMIN) {
            Button(
                onClick = onNavigateToAdmin,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Admin Dashboard")
            }
        }

        Button(onClick = onNavigateToCategories, modifier = Modifier.fillMaxWidth()) {
            Text("Browse Categories")
        }

        Button(onClick = onNavigateToProducts, modifier = Modifier.fillMaxWidth()) {
            Text("All Products")
        }

        OutlinedButton(onClick = onNavigateToFavorites, modifier = Modifier.fillMaxWidth()) {
            Text("My Favorites")
        }

        OutlinedButton(onClick = onNavigateToCart, modifier = Modifier.fillMaxWidth()) {
            Text("My Cart")
        }

        OutlinedButton(onClick = onNavigateToOrders, modifier = Modifier.fillMaxWidth()) {
            Text("Order History")
        }

        OutlinedButton(onClick = onNavigateToProfile, modifier = Modifier.fillMaxWidth()) {
            Text("User Profile")
        }
    }
}
