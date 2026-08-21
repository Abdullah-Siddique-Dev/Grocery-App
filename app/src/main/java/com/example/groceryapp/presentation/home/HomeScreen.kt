package com.example.groceryapp.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateToCategories: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
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

        Button(onClick = onNavigateToCategories, modifier = Modifier.fillMaxWidth()) {
            Text("Browse Categories")
        }

        Button(onClick = onNavigateToProducts, modifier = Modifier.fillMaxWidth()) {
            Text("All Products")
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
