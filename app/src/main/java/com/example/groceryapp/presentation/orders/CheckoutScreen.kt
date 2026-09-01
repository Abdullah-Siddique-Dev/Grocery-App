package com.example.groceryapp.presentation.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceryapp.domain.model.Address
import com.example.groceryapp.domain.model.PaymentMethod
import com.example.groceryapp.presentation.profile.AddressEditDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onOrderPlaced: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CheckoutViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val savedAddress by viewModel.address.collectAsState()
    
    var showAddressDialog by remember { mutableStateOf(false) }
    val selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CASH_ON_DELIVERY) }

    LaunchedEffect(state) {
        if (state is CheckoutState.Success) {
            onOrderPlaced((state as CheckoutState.Success).orderId)
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Checkout") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    Text("Order Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                cart?.let { c ->
                    items(c.items) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.productName} x ${item.quantity}")
                            Text("$${String.format("%.2f", item.subtotal)}")
                        }
                    }
                    
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Amount", fontWeight = FontWeight.Bold)
                            Text("$${String.format("%.2f", c.total)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Delivery Address", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { showAddressDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Address")
                        }
                    }
                    
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            savedAddress?.let { addr ->
                                Text(addr.fullName, fontWeight = FontWeight.Bold)
                                Text(addr.addressLine)
                                Text("${addr.city}, ${addr.postalCode}")
                                Text(addr.phoneNumber)
                            } ?: Text("Please set a delivery address", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text("Payment Method", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        border = if (selectedPaymentMethod == PaymentMethod.CASH_ON_DELIVERY) 
                            ButtonDefaults.outlinedButtonBorder else null
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedPaymentMethod == PaymentMethod.CASH_ON_DELIVERY,
                                onClick = { }
                            )
                            Text("Cash on Delivery")
                        }
                    }
                    
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = false, onClick = {}, enabled = false)
                        Text("Online Payment (Coming Soon)", color = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            if (state is CheckoutState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = { savedAddress?.let { viewModel.placeOrder(it, selectedPaymentMethod) } },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = savedAddress != null && cart != null
                ) {
                    Text("Place Order")
                }
            }

            if (state is CheckoutState.Error) {
                Text(
                    text = (state as CheckoutState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    if (showAddressDialog) {
        AddressEditDialog(
            currentAddress = savedAddress,
            onDismiss = { showAddressDialog = false },
            onSave = { updatedAddress ->
                viewModel.updateAddress(updatedAddress)
                showAddressDialog = false
            }
        )
    }
}
