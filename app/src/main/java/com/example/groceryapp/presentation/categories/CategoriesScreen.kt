package com.example.groceryapp.presentation.categories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceryapp.domain.model.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onCategoryClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CategoriesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is CategoriesState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is CategoriesState.Empty -> Text("No categories found", Modifier.align(Alignment.Center))
                is CategoriesState.Error -> Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadCategories() }) { Text("Retry") }
                }
                is CategoriesState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(s.categories) { category ->
                            CategoryItem(category, onClick = { onCategoryClick(category.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(category.name) },
        modifier = Modifier.clickable { onClick() }
    )
}
