package com.example.groceryapp.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val isLoggedOut by viewModel.isLoggedOut.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile("current_user_id")
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
                    Button(onClick = { viewModel.loadProfile("current_user_id") }) {
                        Text("Retry")
                    }
                }
                is ProfileState.Success -> {
                    val user = s.user
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ProfileInfoItem(Icons.Default.Person, "Name", user.name)
                        ProfileInfoItem(Icons.Default.Email, "Email", user.email)
                        ProfileInfoItem(Icons.Default.Phone, "Phone Number", user.phoneNumber)
                        ProfileInfoItem(Icons.Default.Home, "Delivery Address", user.address)
                        
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
