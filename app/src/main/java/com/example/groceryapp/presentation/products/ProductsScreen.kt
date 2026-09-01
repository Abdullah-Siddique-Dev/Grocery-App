package com.example.groceryapp.presentation.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceryapp.domain.model.Product
import com.example.groceryapp.ui.components.ProductImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    categoryId: String?,
    onProductClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ProductsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    LaunchedEffect(categoryId) {
        viewModel.loadProducts(categoryId)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Products") })
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it, categoryId) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search products...") },
                    singleLine = true
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is ProductsState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ProductsState.Empty -> Text("No products found", Modifier.align(Alignment.Center))
                is ProductsState.Error -> Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadProducts(categoryId) }) { Text("Retry") }
                }
                is ProductsState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(s.products) { product ->
                            ProductItem(
                                product = product, 
                                onClick = { onProductClick(product.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(product.name) },
        supportingContent = { Text("${product.price} ${product.unit}") },
        leadingContent = {
            ProductImage(
                imageUrl = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier.size(56.dp)
            )
        },
        modifier = Modifier.clickable { onClick() }
    )
}
