package com.example.groceryapp.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groceryapp.ui.components.GroceryTopBar
import com.example.groceryapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToOrders: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            GroceryTopBar(
                title = "Admin Console",
                showBackButton = true,
                onBackClick = onBack
            )
        },
        containerColor = FreshBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Metrics Highlight Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricWidget(
                    modifier = Modifier.weight(1f),
                    title = "Revenue",
                    value = "$24,850",
                    growth = "+18.2%",
                    icon = Icons.Default.TrendingUp,
                    color = EmeraldPrimary
                )
                MetricWidget(
                    modifier = Modifier.weight(1f),
                    title = "Active Users",
                    value = "1,420",
                    growth = "+12.4%",
                    icon = Icons.Default.People,
                    color = Color(0xFF0284C7)
                )
            }

            // Visual Mock Sales Analytics Chart Widget
            Text(text = "Weekly Sales Analytics", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, GrocerySurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        ChartBar("Mon", 45, EmeraldPrimary)
                        ChartBar("Tue", 75, EmeraldPrimary)
                        ChartBar("Wed", 60, EmeraldPrimary)
                        ChartBar("Thu", 90, AccentAmber) // Peak day highlight
                        ChartBar("Fri", 55, EmeraldPrimary)
                        ChartBar("Sat", 110, EmeraldPrimary)
                        ChartBar("Sun", 95, EmeraldPrimary)
                    }
                }
            }

            // Low Stock Urgent Indicators
            Text(text = "Inventory Stock Alerts", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFEF2F2),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFEE2E2))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = StatusError)
                    Column {
                        Text(text = "3 Items running low in stock", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF991B1B))
                        Text(text = "Organic Spinach and Cavendish Bananas count < 5 units.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7F1D1D))
                    }
                }
            }

            // Navigation Control Center Grid
            Text(text = "Operational Controls", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminCardPremium("Order Pipeline Management", "Process, accept, and dispatch shipments", Icons.Default.LocalShipping, EmeraldPrimary, onNavigateToOrders)
                AdminCardPremium("Product Inventory Catalog", "Update stocks, descriptions, and pictures", Icons.Default.Inventory, Color(0xFF0284C7), onNavigateToProducts)
                AdminCardPremium("Category Mapping Directory", "Organize taxonomy and layout priority", Icons.Default.Category, AccentAmber, onNavigateToCategories)
                AdminCardPremium("User Registries & Roles", "Inspect customer profiles and roles", Icons.Default.People, Color(0xFF7C3AED), onNavigateToUsers)
            }
        }
    }
}

@Composable
fun MetricWidget(
    modifier: Modifier,
    title: String,
    value: String,
    growth: String,
    icon: ImageVector,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, GrocerySurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = title, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold), color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = growth, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = EmeraldPrimary)
        }
    }
}

@Composable
fun ChartBar(label: String, percentage: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .width(24.dp)
                .fillMaxHeight(percentage / 120f)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(color)
        )
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}

@Composable
fun AdminCardPremium(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, GrocerySurfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = badgeColor.copy(alpha = 0.12f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(20.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
        }
    }
}
