package com.example.groceryapp.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceryapp.domain.model.*
import com.example.groceryapp.ui.components.*
import com.example.groceryapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: String,
    onBack: () -> Unit,
    onNavigateToTracking: (String) -> Unit = {},
    viewModel: OrderDetailsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val cancelState by viewModel.cancelState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(orderId) {
        viewModel.loadOrderDetails(orderId)
    }

    LaunchedEffect(cancelState) {
        cancelState?.let { result ->
            if (result.isSuccess) {
                snackbarHostState.showSnackbar("Order cancelled successfully")
            } else {
                snackbarHostState.showSnackbar(result.exceptionOrNull()?.message ?: "Failed to cancel order")
            }
            viewModel.resetCancelState()
        }
    }

    Scaffold(
        containerColor = FreshBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GroceryTopBar(
                title = "Order Details",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val s = state) {
                is OrderDetailsState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = EmeraldPrimary)
                    }
                }
                is OrderDetailsState.Error -> {
                    EmptyState(
                        title = "Error",
                        description = s.message,
                        icon = Icons.Default.Error
                    )
                }
                is OrderDetailsState.Success -> {
                    val order = s.order
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Order Status Card
                        item {
                            OrderStatusBanner(order = order)
                        }

                        // Order Timeline
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                color = SurfaceWhite
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "Tracking Status",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(bottom = 20.dp),
                                        color = TextPrimary
                                    )
                                    
                                    // Track Order Button (show only for active deliveries)
                                    if (order.status == OrderStatus.OUT_FOR_DELIVERY || order.status == OrderStatus.CONFIRMED) {
                                        PrimaryButton(
                                            text = "Track Order Live",
                                            onClick = { onNavigateToTracking(orderId) },
                                            icon = Icons.Default.LocalShipping,
                                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                                        )
                                    }
                                    
                                    OrderStatusTimeline(currentStatus = order.status)
                                }
                            }
                        }

                        // Delivery Info
                        item {
                            InfoSection(title = "Delivery Address", icon = Icons.Default.LocationOn) {
                                Column {
                                    Text(order.deliveryAddress.fullName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(order.deliveryAddress.addressLine, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                    Text("${order.deliveryAddress.city}, ${order.deliveryAddress.postalCode}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(order.deliveryAddress.phoneNumber, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                            }
                        }

                        // Items
                        item {
                            InfoSection(title = "Ordered Items", icon = Icons.Outlined.ShoppingBag) {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    order.items.forEach { item ->
                                        OrderItemDetailRow(item)
                                    }
                                }
                            }
                        }

                        // Price Summary
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                color = EmeraldLight.copy(alpha = 0.5f)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Paid Amount", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                                        Text(
                                            text = "$${order.totalAmount}",
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = EmeraldPrimary
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "via ${order.paymentMethod.name.replace("_", " ")}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldOnLight
                                    )
                                }
                            }
                        }

                        // Cancel Button
                        if (order.status == OrderStatus.PENDING || order.status == OrderStatus.CONFIRMED) {
                            item {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .clickable { viewModel.cancelOrder(order.id) },
                                    shape = RoundedCornerShape(16.dp),
                                    color = StatusError.copy(alpha = 0.1f)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = StatusError)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Cancel this Order", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = StatusError)
                                    }
                                }
                            }
                        }
                        
                        item { Spacer(modifier = Modifier.height(32.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusBanner(order: Order) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = EmeraldDeep
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Current Status",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = order.status.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ID: #${order.id.takeLast(10).uppercase()}",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun InfoSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
            Icon(icon, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = SurfaceWhite
        ) {
            Box(modifier = Modifier.padding(20.dp)) {
                content()
            }
        }
    }
}

@Composable
fun OrderStatusTimeline(currentStatus: OrderStatus) {
    if (currentStatus == OrderStatus.CANCELLED) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(StatusError.copy(alpha = 0.1f))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Cancel, contentDescription = null, tint = StatusError)
            Spacer(modifier = Modifier.width(12.dp))
            Text("This order was cancelled", color = StatusError, fontWeight = FontWeight.Bold)
        }
    } else {
        val steps = listOf(
            OrderStatus.PENDING to "Order Placed",
            OrderStatus.CONFIRMED to "Confirmed",
            OrderStatus.SHIPPED to "Shipped",
            OrderStatus.DELIVERED to "Delivered"
        )

        Column {
            steps.forEachIndexed { index, (status, label) ->
                val isCompleted = isStatusCompleted(currentStatus, status)
                val isCurrent = currentStatus == status
                
                val textColor = if (isCurrent) TextPrimary else if (isCompleted) TextPrimary else TextMuted

                Row(verticalAlignment = Alignment.Top) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier.size(24.dp),
                            shape = CircleShape,
                            color = if (isCurrent) EmeraldPrimary else if (isCompleted) EmeraldLight else GrocerySurfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (isCompleted) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                                } else if (isCurrent) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                                }
                            }
                        }
                        if (index < steps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(40.dp)
                                    .background(if (isCompleted) EmeraldPrimary else GrocerySurfaceVariant)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.padding(top = 2.dp)) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Bold,
                            color = textColor
                        )
                        if (isCurrent) {
                            Text(
                                text = "Current Status",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun isStatusCompleted(current: OrderStatus, target: OrderStatus): Boolean {
    val order = listOf(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.SHIPPED, OrderStatus.DELIVERED)
    val currentIndex = order.indexOf(current)
    val targetIndex = order.indexOf(target)
    return currentIndex > targetIndex
}

@Composable
fun OrderItemDetailRow(item: OrderItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Item #${item.productId.takeLast(8).uppercase()}", 
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Text(
                text = "${item.quantity} x $${item.price}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Text(
            text = "$${item.price * item.quantity}",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
        )
    }
}
