package com.example.groceryapp.presentation.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceryapp.presentation.cart.CartViewModel
import com.example.groceryapp.ui.components.*
import com.example.groceryapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    categoryId: String?,
    onProductClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ProductsViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    LaunchedEffect(categoryId) {
        viewModel.loadProducts(categoryId)
    }

    Scaffold(
        containerColor = FreshBackground,
        topBar = {
            Surface(
                color = SurfaceWhite,
                shadowElevation = 2.dp
            ) {
                Column {
                    GroceryTopBar(
                        title = if (categoryId != null) "Category" else "Search",
                        showBackButton = true,
                        onBackClick = onBack
                    )
                    GrocerySearchBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChange(it, categoryId) },
                        placeholder = "Search for fresh groceries...",
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is ProductsState.Loading -> {
                    ProductsLoadingState()
                }
                is ProductsState.Empty -> {
                    EmptyState(
                        title = "No products found",
                        description = "We couldn't find any products matching your search.",
                        icon = Icons.Default.SearchOff
                    )
                }
                is ProductsState.Error -> {
                    EmptyState(
                        title = "Error",
                        description = s.message,
                        icon = Icons.Default.Error
                    )
                }
                is ProductsState.Success -> {
                    Column {
                        Text(
                            text = "${s.products.size} Products found",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                        )
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(s.products) { product ->
                                ProductCard(
                                    product = product,
                                    onProductClick = onProductClick,
                                    onAddClick = { cartViewModel.addToCart(product) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductsLoadingState() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        repeat(6) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(GrocerySurfaceVariant)
                )
            }
        }
    }
}
