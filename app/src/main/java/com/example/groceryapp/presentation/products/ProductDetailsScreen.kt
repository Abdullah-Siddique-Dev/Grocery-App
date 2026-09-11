package com.example.groceryapp.presentation.products

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceryapp.domain.model.Product
import com.example.groceryapp.domain.model.Review
import com.example.groceryapp.ui.components.*
import com.example.groceryapp.ui.theme.*

@Composable
fun ProductDetailsScreen(
    productId: String,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    viewModel: ProductDetailsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val reviewsState by viewModel.reviewsState.collectAsState()
    val isFavorited by viewModel.isFavorited.collectAsState()
    val isAddedToCart by viewModel.isAddedToCart.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    Scaffold(
        containerColor = SurfaceWhite,
        topBar = {
            ProductDetailsTopBar(
                onBack = onBack,
                isFavorite = isFavorited,
                onFavoriteClick = { 
                    (state as? ProductDetailsState.Success)?.product?.let { 
                        viewModel.toggleFavorite(it) 
                    }
                }
            )
        },
        bottomBar = {
            if (state is ProductDetailsState.Success) {
                val product = (state as ProductDetailsState.Success).product
                ProductDetailsBottomBar(
                    price = product.price,
                    onAddToCart = { viewModel.addToCart(product) },
                    isAdded = isAddedToCart
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val detailsState = state) {
                is ProductDetailsState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = EmeraldPrimary)
                    }
                }
                is ProductDetailsState.Error -> {
                    EmptyState(
                        title = "Error",
                        description = detailsState.message,
                        icon = Icons.Default.Error
                    )
                }
                is ProductDetailsState.Success -> {
                    ProductDetailsContent(
                        product = detailsState.product,
                        reviewsState = reviewsState
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsTopBar(
    onBack: () -> Unit,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = FreshBackground,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                }
            }
        },
        actions = {
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = FreshBackground,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else TextPrimary
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
fun ProductDetailsContent(
    product: Product,
    reviewsState: ReviewsState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Large Image Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(FreshBackground)
        ) {
            ProductImage(
                imageUrl = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-24).dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = SurfaceWhite
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = product.unit,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Price and Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${product.price}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldPrimary
                        )
                    )
                    
                    Surface(
                        color = EmeraldLight,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "4.8", 
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldOnLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Description
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = product.description.ifEmpty { "Experience the freshness of our premium ${product.name}. Carefully selected and delivered with love." },
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Reviews Section
                when (reviewsState) {
                    is ReviewsState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = EmeraldPrimary)
                    }
                    is ReviewsState.Success -> {
                        if (reviewsState.reviews.reviews.isNotEmpty()) {
                            Text(
                                text = "Reviews (${reviewsState.reviews.reviews.size})",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            reviewsState.reviews.reviews.take(5).forEach { review ->
                                ReviewItem(review)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                    is ReviewsState.Error -> {
                        // Silently fail or show simple text
                    }
                }
                
                Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom bar
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FreshBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Shopper", 
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Row {
                    repeat(review.rating) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(14.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun ProductDetailsBottomBar(
    price: Double,
    onAddToCart: () -> Unit,
    isAdded: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        color = SurfaceWhite
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Price",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "$$price",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                )
            }
            
            PrimaryButton(
                text = if (isAdded) "Added to Cart" else "Add to Cart",
                onClick = onAddToCart,
                enabled = !isAdded,
                modifier = Modifier.weight(1.5f)
            )
        }
    }
}
