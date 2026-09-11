package com.example.groceryapp.presentation.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceryapp.domain.model.Address
import com.example.groceryapp.domain.model.PaymentMethod
import com.example.groceryapp.presentation.profile.AddressEditDialog
import com.example.groceryapp.ui.components.*
import com.example.groceryapp.ui.theme.*

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
        containerColor = FreshBackground,
        topBar = {
            GroceryTopBar(
                title = "Checkout",
                showBackButton = true,
                onBackClick = onBack
            )
        },
        bottomBar = {
            cart?.let { c ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 24.dp,
                    color = SurfaceWhite,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(24.dp)
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Grand Total", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Text(
                                text = "$${c.total}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldPrimary
                                )
                            )
                        }
                        
                        PrimaryButton(
                            text = "Place Order",
                            onClick = { savedAddress?.let { viewModel.placeOrder(it, selectedPaymentMethod) } },
                            modifier = Modifier.width(180.dp),
                            isLoading = state is CheckoutState.Loading,
                            enabled = savedAddress != null
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Delivery Address Section
                item {
                    CheckoutSection(
                        title = "Delivery Address",
                        icon = Icons.Default.LocationOn,
                        onActionClick = { showAddressDialog = true }
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = SurfaceWhite
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                savedAddress?.let { addr ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(addr.fullName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(addr.addressLine, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                    Text("${addr.city}, ${addr.postalCode}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Phone, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(addr.phoneNumber, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    }
                                } ?: Text(
                                    "Please set a delivery address",
                                    color = StatusError,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Order Summary Section
                item {
                    CheckoutSection(
                        title = "Order Summary",
                        icon = Icons.Default.ShoppingBag
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = SurfaceWhite
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                cart?.items?.forEach { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${item.productName} x ${item.quantity}", 
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = "$${item.subtotal}", 
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = GrocerySurfaceVariant)
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                PriceSummaryRow(label = "Subtotal", value = "$${cart?.total ?: 0.0}")
                                Spacer(modifier = Modifier.height(8.dp))
                                PriceSummaryRow(label = "Delivery", value = "FREE")
                            }
                        }
                    }
                }

                // Payment Method Section
                item {
                    CheckoutSection(
                        title = "Payment Method",
                        icon = Icons.Default.Payment
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = EmeraldLight.copy(alpha = 0.3f),
                            border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedPaymentMethod == PaymentMethod.CASH_ON_DELIVERY,
                                    onClick = { },
                                    colors = RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Cash on Delivery", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                                    Text("Pay when you receive your fresh items", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
                
                if (state is CheckoutState.Error) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = StatusError.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = (state as CheckoutState.Error).message,
                                color = StatusError,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
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

@Composable
fun CheckoutSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onActionClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            }
            if (onActionClick != null) {
                TextButton(onClick = onActionClick) {
                    Text("Change", color = EmeraldPrimary, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
        content()
    }
}
