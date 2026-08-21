package com.example.groceryapp.presentation.products

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: String,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    viewModel: ProductDetailsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val isAddedToCart by viewModel.isAddedToCart.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    LaunchedEffect(isAddedToCart) {
        if (isAddedToCart) {
            onNavigateToCart()
            viewModel.resetAddedToCart()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Product Details") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is ProductDetailsState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ProductDetailsState.Error -> Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadProduct(productId) }) { Text("Retry") }
                }
                is ProductDetailsState.Success -> {
                    val product = s.product
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(product.name, style = MaterialTheme.typography.headlineMedium)
                        Text("${product.price} per ${product.unit}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Description", style = MaterialTheme.typography.titleMedium)
                        Text(product.description)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Availability: ${if (product.isAvailable) "In Stock" else "Out of Stock"}")
                        if (product.isAvailable) {
                            Text("Stock Quantity: ${product.stockQuantity}")
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = { viewModel.addToCart(product) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = product.isAvailable
                        ) {
                            Text("Add to Cart")
                        }
                    }
                }
            }
        }
    }
}
