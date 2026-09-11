package com.example.groceryapp.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceryapp.ui.components.*
import com.example.groceryapp.ui.theme.*

@Composable
fun CartScreen(
    onBack: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    viewModel: CartViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = FreshBackground,
        topBar = {
            GroceryTopBar(
                title = "My Cart",
                showBackButton = false
            )
        },
        bottomBar = {
            (state as? CartState.Success)?.let { success ->
                CartBottomBar(
                    totalPrice = success.total,
                    onCheckout = onNavigateToCheckout
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val cartState = state) {
                is CartState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = EmeraldPrimary)
                    }
                }
                is CartState.Empty -> {
                    EmptyState(
                        title = "Your cart is empty",
                        description = "Looks like you haven't added anything to your cart yet.",
                        icon = Icons.Default.ShoppingCart
                    )
                }
                is CartState.Error -> {
                    EmptyState(
                        title = "Error",
                        description = cartState.message,
                        icon = Icons.Default.Error
                    )
                }
                is CartState.Success -> {
                    CartContent(
                        items = cartState.items,
                        onIncrement = { item -> viewModel.updateQuantity(item.productId, item.quantity + 1) },
                        onDecrement = { item -> viewModel.updateQuantity(item.productId, item.quantity - 1) },
                        onRemove = { item -> viewModel.removeItem(item.productId) }
                    )
                }
            }
        }
    }
}

@Composable
fun CartContent(
    items: List<CartItemUiState>,
    onIncrement: (CartItemUiState) -> Unit,
    onDecrement: (CartItemUiState) -> Unit,
    onRemove: (CartItemUiState) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            CartItemCard(
                item = item,
                onIncrement = { onIncrement(item) },
                onDecrement = { onDecrement(item) },
                onRemove = { onRemove(item) }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(100.dp)) // Extra space for bottom bar
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItemUiState,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceWhite,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            Surface(
                modifier = Modifier.size(90.dp),
                shape = RoundedCornerShape(16.dp),
                color = FreshBackground
            ) {
                ProductImage(
                    imageUrl = item.productImageUrl,
                    contentDescription = item.productName,
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.productName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
                
                Text(
                    text = "Unit Price: $${item.price}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${item.subtotal}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldPrimary
                        )
                    )
                    
                    QuantitySelector(
                        quantity = item.quantity,
                        onIncrement = onIncrement,
                        onDecrement = onDecrement
                    )
                }
            }
        }
    }
}

@Composable
fun CartBottomBar(
    totalPrice: Double,
    onCheckout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 24.dp,
        color = SurfaceWhite,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .navigationBarsPadding()
        ) {
            PriceSummaryRow(label = "Subtotal", value = "$$totalPrice")
            Spacer(modifier = Modifier.height(8.dp))
            PriceSummaryRow(label = "Delivery Fee", value = "$2.00")
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = GrocerySurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            PriceSummaryRow(label = "Total Amount", value = "$${totalPrice + 2.0}", isTotal = true)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            PrimaryButton(
                text = "Proceed to Checkout",
                onClick = onCheckout
            )
        }
    }
}
