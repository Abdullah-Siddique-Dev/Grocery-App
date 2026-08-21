package com.example.groceryapp.presentation.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceryapp.domain.model.OrderItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: String,
    onBack: () -> Unit,
    viewModel: OrderDetailsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.loadOrderDetails(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Order Details") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is OrderDetailsState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is OrderDetailsState.Error -> Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
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
                            Column {
                                Text("Order Status", style = MaterialTheme.typography.titleMedium)
                                Text(order.status, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Placed at: ${order.placedAt}")
                                Text("Delivery Address:", fontWeight = FontWeight.Bold)
                                Text(order.deliveryAddress)
                            }
                        }

                        item {
                            Divider()
                            Text("Items", style = MaterialTheme.typography.titleLarge)
                        }

                        items(order.items) { item ->
                            OrderItemDetailRow(item)
                        }

                        item {
                            Divider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Amount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("$${String.format("%.2f", order.totalAmount)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemDetailRow(item: OrderItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Product ID: ${item.productId}")
            Text("Qty: ${item.quantity} x $${String.format("%.2f", item.price)}")
        }
        Text("$${String.format("%.2f", item.price * item.quantity)}", fontWeight = FontWeight.Medium)
    }
}
