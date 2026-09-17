package com.example.groceryapp.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceryapp.domain.model.Product
import com.example.groceryapp.domain.model.User
import com.example.groceryapp.ui.components.*
import com.example.groceryapp.ui.theme.*
import java.util.*

@Composable
fun HomeScreen(
    onNavigateToCategories: () -> Unit,
    onNavigateToProducts: (String?) -> Unit,
    onNavigateToProductDetails: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToOffers: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState(initial = null)
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = FreshBackground,
        topBar = {
            HomeTopBar(
                user = currentUser,
                onNotificationClick = onNavigateToNotifications,
                onCartClick = onNavigateToCart,
                onProfileClick = onNavigateToProfile,
                onOffersClick = onNavigateToOffers
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Search Bar
            Box(modifier = Modifier.padding(16.dp)) {
                GrocerySearchBar(
                    query = "",
                    onQueryChange = { /* Navigate to search or filter */ },
                    placeholder = "Search fresh groceries...",
                    modifier = Modifier.clickable { onNavigateToProducts(null) }
                )
            }

            when (val homeState = state) {
                is HomeState.Loading -> {
                    HomeLoadingState()
                }
                is HomeState.Error -> {
                    EmptyState(
                        title = "Oops!",
                        description = homeState.message,
                        icon = Icons.Default.Error,
                        modifier = Modifier.fillMaxWidth().padding(top = 100.dp)
                    )
                }
                is HomeState.Success -> {
                    HomeContent(
                        homeState = homeState,
                        onCategoryClick = { onNavigateToProducts(it) },
                        onProductClick = onNavigateToProductDetails,
                        onAddProduct = { viewModel.addToCart(it) },
                        onViewAllCategories = onNavigateToCategories,
                        onViewAllProducts = { onNavigateToProducts(null) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun HomeTopBar(
    user: User?,
    onNotificationClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onOffersClick: () -> Unit
) {
    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$greeting 👋",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Text(
                text = user?.name ?: "Fresh Shopper",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { /* Change Location */ }
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = user?.address?.addressLine ?: "Set delivery location",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    Icons.Default.KeyboardArrowDown, 
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(44.dp).clickable { onNotificationClick() },
                shape = CircleShape,
                color = SurfaceWhite,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.Notifications, 
                        contentDescription = "Notifications",
                        tint = TextPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                modifier = Modifier.size(44.dp).clickable { onOffersClick() },
                shape = CircleShape,
                color = AccentAmber.copy(alpha = 0.15f),
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.LocalOffer, 
                        contentDescription = "Offers",
                        tint = AccentAmber
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                modifier = Modifier.size(44.dp).clickable { onProfileClick() },
                shape = CircleShape,
                color = EmeraldLight,
                shadowElevation = 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person, 
                        contentDescription = "Profile",
                        tint = EmeraldPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    homeState: HomeState.Success,
    onCategoryClick: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onAddProduct: (Product) -> Unit,
    onViewAllCategories: () -> Unit,
    onViewAllProducts: () -> Unit
) {
    Column {
        // Promo Banner
        PremiumPromoBanner(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(180.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Categories
        SectionHeader(
            title = "Categories",
            onActionClick = onViewAllCategories,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(homeState.categories) { category ->
                CategoryItem(category = category, onCategoryClick = onCategoryClick)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Featured Products
        SectionHeader(
            title = "Popular Choices",
            onActionClick = onViewAllProducts,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(homeState.featuredProducts) { product ->
                ProductCard(
                    product = product,
                    onProductClick = onProductClick,
                    onAddClick = { onAddProduct(product) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Fresh Picks
        SectionHeader(
            title = "Fresh Picks",
            onActionClick = onViewAllProducts,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(homeState.freshPicks) { product ->
                ProductCard(
                    product = product,
                    onProductClick = onProductClick,
                    onAddClick = { onAddProduct(product) }
                )
            }
        }
    }
}

@Composable
fun PremiumPromoBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        EmeraldDeep,
                        EmeraldPrimary,
                        AccentLime
                    )
                )
            )
    ) {
        // Background Pattern (Simulated)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.05f))
        )
        
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1.2f)) {
                Surface(
                    color = White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Limited Offer",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Freshness Delivered\nTo Your Door",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 28.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Get 25% OFF on your first order",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    modifier = Modifier
                        .height(36.dp)
                        .clickable { },
                    shape = RoundedCornerShape(10.dp),
                    color = White
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            "Shop Now", 
                            color = EmeraldDeep, 
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
                        )
                    }
                }
            }
            
            Box(
                modifier = Modifier.weight(0.8f),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.ShoppingBasket,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(120.dp)
                )
            }
        }
    }
}

@Composable
fun HomeLoadingState() {
    Column(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(GrocerySurfaceVariant)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(24.dp)
                .background(GrocerySurfaceVariant)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(GrocerySurfaceVariant)
                )
            }
        }
    }
}
