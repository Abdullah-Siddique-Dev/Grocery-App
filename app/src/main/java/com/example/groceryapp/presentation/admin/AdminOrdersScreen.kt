package com.example.groceryapp.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceryapp.domain.model.Order
import com.example.groceryapp.domain.model.OrderStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    onBack: () -> Unit,
    viewModel: AdminOrdersViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Orders") },
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
                is AdminOrdersState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is AdminOrdersState.Error -> Column(Modifier.align(Alignment.Center)) {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadOrders() }) { Text("Retry") }
                }
                is AdminOrdersState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(s.orders) { order ->
                            AdminOrderCard(order, onUpdateStatus = { status ->
                                viewModel.updateStatus(order.id, status)
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrderCard(order: Order, onUpdateStatus: (OrderStatus) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Order #${order.id.takeLast(6)}", fontWeight = FontWeight.Bold)
                Text(order.status.name, color = MaterialTheme.colorScheme.primary)
            }
            Text("User: ${order.userId}")
            Text("Address: ${order.deliveryAddress.addressLine}, ${order.deliveryAddress.city}")
            Text("Total: $${String.format("%.2f", order.totalAmount)}")
            Text("Payment: ${order.paymentMethod.name} (${order.paymentStatus.name})", style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(onClick = { showDialog = true }, modifier = Modifier.align(Alignment.End)) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Update Status")
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Update Order Status") },
            text = {
                Column {
                    OrderStatus.entries.forEach { status ->
                        // Simple validation for transition
                        val isAllowed = isValidTransition(order.status, status)
                        TextButton(
                            onClick = { 
                                onUpdateStatus(status)
                                showDialog = false
                            },
                            enabled = isAllowed,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(status.name)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

private fun isValidTransition(current: OrderStatus, next: OrderStatus): Boolean {
    return when (current) {
        OrderStatus.PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED
        OrderStatus.CONFIRMED -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED
        OrderStatus.SHIPPED -> next == OrderStatus.DELIVERED
        OrderStatus.DELIVERED -> false
        OrderStatus.CANCELLED -> false
    }
}
