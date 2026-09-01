package com.example.groceryapp.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceryapp.domain.model.Address

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val isLoggedOut by viewModel.isLoggedOut.collectAsState()
    var showAddressDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            onLogout()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile") },
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is ProfileState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ProfileState.Error -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadProfile() }) {
                        Text("Retry")
                    }
                }
                is ProfileState.Success -> {
                    val user = s.user
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ProfileInfoItem(Icons.Default.Person, "Name", user.name)
                        ProfileInfoItem(Icons.Default.Email, "Email", user.email)
                        ProfileInfoItem(Icons.Default.Phone, "Phone Number", user.phoneNumber)
                        
                        Divider()
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Delivery Address", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            TextButton(onClick = { showAddressDialog = true }) {
                                Text(if (user.address == null) "Add Address" else "Edit")
                            }
                        }
                        
                        user.address?.let { addr ->
                            Column(modifier = Modifier.padding(start = 40.dp)) {
                                Text(addr.fullName, fontWeight = FontWeight.Medium)
                                Text(addr.addressLine)
                                Text("${addr.city}, ${addr.postalCode}")
                                Text(addr.phoneNumber)
                            }
                        } ?: Text(
                            "No address saved", 
                            modifier = Modifier.padding(start = 40.dp),
                            color = MaterialTheme.colorScheme.outline
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Button(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Logout")
                        }
                    }

                    if (showAddressDialog) {
                        AddressEditDialog(
                            currentAddress = user.address,
                            onDismiss = { showAddressDialog = false },
                            onSave = {
                                viewModel.updateAddress(it)
                                showAddressDialog = false
                            }
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun ProfileInfoItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun AddressEditDialog(
    currentAddress: Address?,
    onDismiss: () -> Unit,
    onSave: (Address) -> Unit
) {
    var name by remember { mutableStateOf(currentAddress?.fullName ?: "") }
    var phone by remember { mutableStateOf(currentAddress?.phoneNumber ?: "") }
    var line by remember { mutableStateOf(currentAddress?.addressLine ?: "") }
    var city by remember { mutableStateOf(currentAddress?.city ?: "") }
    var zip by remember { mutableStateOf(currentAddress?.postalCode ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delivery Address") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") })
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") })
                OutlinedTextField(value = line, onValueChange = { line = it }, label = { Text("Address Line") })
                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") })
                OutlinedTextField(value = zip, onValueChange = { zip = it }, label = { Text("Postal Code") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(Address(name, phone, line, city, zip))
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
