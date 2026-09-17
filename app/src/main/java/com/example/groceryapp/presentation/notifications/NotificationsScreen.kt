package com.example.groceryapp.presentation.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groceryapp.ui.components.GroceryTopBar
import com.example.groceryapp.ui.theme.*

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean
)

enum class NotificationType {
    ORDER, OFFER, STOCK, PRICE_DROP
}

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit
) {
    val mockNotifications = remember {
        listOf(
            NotificationItem(
                id = "1",
                title = "⚡ 15 Minute Super Fast Delivery",
                body = "Your order #817294 has been assigned to our nearby delivery partner John. Get ready to receive fresh groceries!",
                time = "Just now",
                type = NotificationType.ORDER,
                isRead = false
            ),
            NotificationItem(
                id = "2",
                title = "🎁 Weekend Organic Super Sale is Live!",
                body = "Enjoy up to 40% absolute cashback on organic avocados, farm fresh berries, and premium dairy milk bundles.",
                time = "2 hours ago",
                type = NotificationType.OFFER,
                isRead = false
            ),
            NotificationItem(
                id = "3",
                title = "🔥 Price Drop Alert on Your Favorites",
                body = "Great news! Gala Red Apples price dropped down by 15% from $3.49 to $2.99/kg. Add to your basket now!",
                time = "1 day ago",
                type = NotificationType.PRICE_DROP,
                isRead = true
            ),
            NotificationItem(
                id = "4",
                title = "⚠️ Back in Stock Notification",
                body = "Premium Organic Cavendish Bananas are now fully back in stock with fresh farm batches. Hurry up before stock finishes!",
                time = "2 days ago",
                type = NotificationType.STOCK,
                isRead = true
            )
        )
    }

    Scaffold(
        topBar = {
            GroceryTopBar(
                title = "Notifications",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        containerColor = FreshBackground
    ) { padding ->
        if (mockNotifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.NotificationsOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = TextMuted)
                    Text(text = "All caught up!", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    Text(text = "We will notify you about your order updates here.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mockNotifications) { notification ->
                    NotificationCard(notification = notification)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    val (bgColor, iconColor, icon) = when (notification.type) {
        NotificationType.ORDER -> Triple(EmeraldLight, EmeraldPrimary, Icons.Default.LocalShipping)
        NotificationType.OFFER -> Triple(Color(0xFFFEF3C7), AccentAmber, Icons.Default.CardGiftcard)
        NotificationType.PRICE_DROP -> Triple(Color(0xFFE0F2FE), Color(0xFF0284C7), Icons.Default.TrendingDown)
        NotificationType.STOCK -> Triple(Color(0xFFFEE2E2), StatusError, Icons.Default.Inventory)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (notification.isRead) Color.White else EmeraldLight.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (notification.isRead) GrocerySurfaceVariant else EmeraldPrimary.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = bgColor) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (!notification.isRead) {
                        Box(modifier = Modifier.size(8.dp).background(EmeraldPrimary, CircleShape))
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = notification.body, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = notification.time, style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
        }
    }
}
