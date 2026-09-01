package com.example.groceryapp.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceryapp.domain.model.Order
import com.example.groceryapp.domain.model.OrderItem
import com.example.groceryapp.domain.model.OrderStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: String,
    onBack: () -> Unit,
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Order Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is OrderDetailsState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is OrderDetailsState.Error -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadOrderDetails(orderId) }) { Text("Retry") }
                }
                is OrderDetailsState.Success -> {
                    val order = s.order
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            OrderStatusTimeline(currentStatus = order.status)
                        }

                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Payment Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Method:")
                                        Text(order.paymentMethod.name.replace("_", " "), fontWeight = FontWeight.Medium)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Status:")
                                        Text(order.paymentStatus.name, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Delivery Address", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(order.deliveryAddress.fullName, fontWeight = FontWeight.Bold)
                                    Text(order.deliveryAddress.addressLine)
                                    Text("${order.deliveryAddress.city}, ${order.deliveryAddress.postalCode}")
                                    Text(order.deliveryAddress.phoneNumber)
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Placed at: ${order.placedAt.take(16).replace("T", " ")}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        item {
                            HorizontalDivider()
                            Text("Items", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }

                        items(order.items) { item ->
                            OrderItemDetailRow(item)
                        }

                        item {
                            HorizontalDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Amount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("$${String.format("%.2f", order.totalAmount)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (order.status == OrderStatus.PENDING || order.status == OrderStatus.CONFIRMED) {
                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { viewModel.cancelOrder(order.id) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Cancel Order")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusTimeline(currentStatus: OrderStatus) {
    if (currentStatus == OrderStatus.CANCELLED) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Order Cancelled", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            val statuses = listOf(
                OrderStatus.PENDING to "Order Placed",
                OrderStatus.CONFIRMED to "Confirmed",
                OrderStatus.SHIPPED to "Shipped",
                OrderStatus.DELIVERED to "Delivered"
            )

            statuses.forEachIndexed { index, pair ->
                val (status, label) = pair
                val isCompleted = isStatusCompleted(currentStatus, status)
                val isCurrent = currentStatus == status

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isCompleted) MaterialTheme.colorScheme.primary 
                                    else if (isCurrent) MaterialTheme.colorScheme.secondary
                                    else Color.LightGray
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                        if (index < statuses.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(32.dp)
                                    .background(if (isCompleted) MaterialTheme.colorScheme.primary else Color.LightGray)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCurrent) MaterialTheme.colorScheme.primary else if (isCompleted) Color.Black else Color.Gray,
                        modifier = Modifier.padding(bottom = if (index < statuses.size - 1) 32.dp else 0.dp)
                    )
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
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Product ID: ${item.productId.takeLast(6)}")
            Text("Qty: ${item.quantity} x $${String.format("%.2f", item.price)}")
        }
        Text("$${String.format("%.2f", item.price * item.quantity)}", fontWeight = FontWeight.Medium)
    }
}
