package com.example.groceryapp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.groceryapp.domain.model.Address
import com.example.groceryapp.ui.components.*
import com.example.groceryapp.ui.theme.*

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
        containerColor = FreshBackground,
        topBar = {
            GroceryTopBar(
                title = "My Profile",
                showBackButton = false // Part of bottom nav
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val s = state) {
                is ProfileState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = EmeraldPrimary)
                    }
                }
                is ProfileState.Error -> {
                    EmptyState(
                        title = "Error",
                        description = s.message,
                        icon = Icons.Default.Error
                    )
                }
                is ProfileState.Success -> {
                    val user = s.user
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Profile Header
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.name.take(1).uppercase(),
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        color = EmeraldPrimary,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = user.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }

                        // Account Settings Section
                        ProfileSection(title = "Account Information") {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                color = SurfaceWhite
                            ) {
                                Column {
                                    ProfileInfoRow(icon = Icons.Outlined.Person, label = "Full Name", value = user.name)
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = GrocerySurfaceVariant)
                                    ProfileInfoRow(icon = Icons.Outlined.Email, label = "Email", value = user.email)
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = GrocerySurfaceVariant)
                                    ProfileInfoRow(icon = Icons.Outlined.Phone, label = "Phone Number", value = user.phoneNumber)
                                }
                            }
                        }

                        // Address Section
                        ProfileSection(
                            title = "Delivery Address",
                            actionText = if (user.address == null) "Add New" else "Edit",
                            onActionClick = { showAddressDialog = true }
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                color = SurfaceWhite
                            ) {
                                Box(modifier = Modifier.padding(20.dp)) {
                                    user.address?.let { addr ->
                                        Column {
                                            Text(addr.fullName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(addr.addressLine, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                            Text("${addr.city}, ${addr.postalCode}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                        }
                                    } ?: Text(
                                        "No address saved yet",
                                        color = TextMuted,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Logout Button
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable { viewModel.logout() },
                            shape = RoundedCornerShape(16.dp),
                            color = StatusError.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = null, tint = StatusError)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Logout from App", 
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = StatusError
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(40.dp))
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
fun ProfileSection(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            if (actionText != null && onActionClick != null) {
                TextButton(onClick = onActionClick) {
                    Text(actionText, color = EmeraldPrimary, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
        content()
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = FreshBackground
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), color = TextPrimary)
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
        title = { Text("Delivery Address", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name, 
                    onValueChange = { name = it }, 
                    label = { Text("Full Name") }, 
                    shape = RoundedCornerShape(12.dp), 
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary)
                )
                OutlinedTextField(
                    value = phone, 
                    onValueChange = { phone = it }, 
                    label = { Text("Phone Number") }, 
                    shape = RoundedCornerShape(12.dp), 
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary)
                )
                OutlinedTextField(
                    value = line, 
                    onValueChange = { line = it }, 
                    label = { Text("Address Line") }, 
                    shape = RoundedCornerShape(12.dp), 
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary)
                )
                OutlinedTextField(
                    value = city, 
                    onValueChange = { city = it }, 
                    label = { Text("City") }, 
                    shape = RoundedCornerShape(12.dp), 
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary)
                )
                OutlinedTextField(
                    value = zip, 
                    onValueChange = { zip = it }, 
                    label = { Text("Postal Code") }, 
                    shape = RoundedCornerShape(12.dp), 
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary)
                )
            }
        },
        confirmButton = {
            PrimaryButton(
                text = "Save Address", 
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank() && line.isNotBlank()) {
                        onSave(Address(name, phone, line, city, zip))
                    }
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", color = TextSecondary, textAlign = TextAlign.Center)
            }
        }
    )
}
