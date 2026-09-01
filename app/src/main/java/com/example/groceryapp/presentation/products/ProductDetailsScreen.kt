package com.example.groceryapp.presentation.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceryapp.domain.model.Product
import com.example.groceryapp.domain.model.Review
import com.example.groceryapp.ui.components.ProductImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: String,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    viewModel: ProductDetailsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val reviewsState by viewModel.reviewsState.collectAsState()
    val isAddedToCart by viewModel.isAddedToCart.collectAsState()
    val isFavorited by viewModel.isFavorited.collectAsState()

    var showReviewDialog by remember { mutableStateOf(false) }

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
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val product = (state as? ProductDetailsState.Success)?.product
                    if (product != null) {
                        IconButton(onClick = { viewModel.toggleFavorite(product) }) {
                            Icon(
                                imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorited) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
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
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            ProductImage(
                                imageUrl = product.imageUrl,
                                contentDescription = product.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            )
                        }
                        
                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(product.name, style = MaterialTheme.typography.headlineMedium)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${product.price} per ${product.unit}", 
                                        style = MaterialTheme.typography.titleLarge, 
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    if (reviewsState is ReviewsState.Success) {
                                        val summary = (reviewsState as ReviewsState.Success).reviews.summary
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                                            Text(
                                                text = String.format("%.1f (%d)", summary.averageRating, summary.totalReviews),
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(start = 4.dp)
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                val stockColor = when {
                                    product.stockQuantity == 0 || !product.isAvailable -> MaterialTheme.colorScheme.error
                                    product.stockQuantity < 10 -> Color(0xFFFFA500) // Orange
                                    else -> Color(0xFF4CAF50) // Green
                                }
                                
                                val stockText = when {
                                    product.stockQuantity == 0 || !product.isAvailable -> "Out of Stock"
                                    product.stockQuantity < 10 -> "Only ${product.stockQuantity} left!"
                                    else -> "In Stock (${product.stockQuantity})"
                                }

                                Text(
                                    text = stockText,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = stockColor,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(product.description, style = MaterialTheme.typography.bodyMedium)
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Customer Reviews", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    TextButton(onClick = { showReviewDialog = true }) {
                                        Text("Write a Review")
                                    }
                                }
                            }
                        }

                        when (val r = reviewsState) {
                            is ReviewsState.Loading -> item { Box(Modifier.fillMaxWidth().padding(16.dp)) { CircularProgressIndicator(Modifier.align(Alignment.Center)) } }
                            is ReviewsState.Error -> item { Text(r.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp)) }
                            is ReviewsState.Success -> {
                                if (r.reviews.reviews.isEmpty()) {
                                    item { Text("No reviews yet. Be the first to review!", modifier = Modifier.padding(16.dp)) }
                                } else {
                                    items(r.reviews.reviews) { review ->
                                        ReviewItem(review)
                                    }
                                }
                            }
                        }
                    }

                    // Add to Cart Button fixed at bottom
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomCenter) {
                        Button(
                            onClick = { viewModel.addToCart(product) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = product.isAvailable && product.stockQuantity > 0
                        ) {
                            Text(if (product.stockQuantity > 0 && product.isAvailable) "Add to Cart" else "Unavailable")
                        }
                    }
                }
            }
        }
    }

    if (showReviewDialog) {
        ReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, comment ->
                viewModel.submitReview(productId, rating, comment)
                showReviewDialog = false
            }
        )
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(review.userName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(review.createdAt.take(10), style = MaterialTheme.typography.bodySmall)
        }
        Row(modifier = Modifier.padding(vertical = 4.dp)) {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (index < review.rating) Color(0xFFFFC107) else Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Text(review.comment, style = MaterialTheme.typography.bodyMedium)
        HorizontalDivider(modifier = Modifier.padding(top = 16.dp), thickness = 0.5.dp)
    }
}

@Composable
fun ReviewDialog(onDismiss: () -> Unit, onSubmit: (Int, String) -> Unit) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Write a Review") },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    repeat(5) { index ->
                        IconButton(onClick = { rating = index + 1 }) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < rating) Color(0xFFFFC107) else Color.LightGray
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Your Comment") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, comment) },
                enabled = comment.isNotBlank()
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
