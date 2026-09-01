package com.example.groceryapp.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoryEditScreen(
    categoryId: String,
    onBack: () -> Unit,
    viewModel: AdminCategoryEditViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val category by viewModel.category.collectAsState()

    var name by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var displayOrder by remember { mutableStateOf("") }

    LaunchedEffect(categoryId) {
        viewModel.loadCategory(categoryId)
    }

    LaunchedEffect(category) {
        category?.let {
            name = it.name
            icon = it.icon
            imageUrl = it.imageUrl
            displayOrder = it.displayOrder.toString()
        }
    }

    LaunchedEffect(state) {
        if (state is AdminCategoryEditState.Success) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (categoryId == "new") "Add Category" else "Edit Category") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state is AdminCategoryEditState.Loading && category == null && categoryId != "new") {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = icon, onValueChange = { icon = it }, label = { Text("Icon Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = displayOrder,
                    onValueChange = { displayOrder = it },
                    label = { Text("Display Order") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                if (state is AdminCategoryEditState.Error) {
                    Text((state as AdminCategoryEditState.Error).message, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        viewModel.saveCategory(
                            id = if (categoryId == "new") null else categoryId,
                            name = name,
                            icon = icon,
                            imageUrl = imageUrl,
                            displayOrder = displayOrder.toIntOrNull() ?: 0
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state !is AdminCategoryEditState.Loading && name.isNotBlank()
                ) {
                    if (state is AdminCategoryEditState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Save Category")
                    }
                }
            }
        }
    }
}
