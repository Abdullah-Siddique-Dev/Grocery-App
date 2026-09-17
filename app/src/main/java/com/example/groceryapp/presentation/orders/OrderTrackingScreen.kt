package com.example.groceryapp.presentation.orders

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groceryapp.ui.components.GroceryTopBar
import com.example.groceryapp.ui.components.PrimaryButton
import com.example.groceryapp.ui.theme.*

@Composable
fun OrderTrackingScreen(
    orderId: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            GroceryTopBar(
                title = "Track Order",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        containerColor = FreshBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Live Map Mockup Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(GrocerySurfaceVariant)
            ) {
                // Background map image (Unsplash city map mockup)
                com.example.groceryapp.ui.components.ProductImage(
                    imageUrl = "https://images.unsplash.com/photo-1524661135-423995f22d0b?w=800&q=80",
                    contentDescription = "Map view",
                    modifier = Modifier.fillMaxSize()
                )
                
                // Delivery Agent Indicator (Animated pulse)
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.4f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "pulse_scale"
                )
                
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                        .padding(bottom = 40.dp) // Offset to look like it's on a road
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                            .background(EmeraldPrimary.copy(alpha = 0.3f * (2f - pulseScale)), CircleShape)
                            .padding(pulseScale.dp * 10)
                    )
                    Surface(
                        modifier = Modifier.size(32.dp).align(Alignment.Center),
                        shape = CircleShape,
                        color = EmeraldPrimary,
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                
                // Delivery Status Overlay Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = EmeraldLight) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Timer, tint = EmeraldPrimary, contentDescription = null)
                            }
                        }
                        Column {
                            Text(text = "Arriving in 12 mins", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text(text = "Your delivery partner is on the way", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
            }
            
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Order Status",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Professional Vertical Timeline
                TrackingStep(
                    title = "Order Placed",
                    time = "10:30 AM",
                    isCompleted = true,
                    isCurrent = false,
                    isLast = false,
                    icon = Icons.Default.Receipt
                )
                TrackingStep(
                    title = "Order Packed",
                    time = "10:45 AM",
                    isCompleted = true,
                    isCurrent = false,
                    isLast = false,
                    icon = Icons.Default.Inventory2
                )
                TrackingStep(
                    title = "Out for Delivery",
                    time = "11:05 AM",
                    isCompleted = true,
                    isCurrent = true,
                    isLast = false,
                    icon = Icons.Default.LocalShipping
                )
                TrackingStep(
                    title = "Delivered",
                    time = "Expected by 11:20 AM",
                    isCompleted = false,
                    isCurrent = false,
                    isLast = true,
                    icon = Icons.Default.CheckCircle
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Delivery Partner Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GrocerySurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = GrocerySurfaceVariant) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TextMuted, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "John Smith", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text(text = "⭐ 4.9 (2,400+ deliveries)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        IconButton(
                            onClick = { /* Call action */ },
                            modifier = Modifier.background(EmeraldPrimary, CircleShape)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color.White)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                PrimaryButton(text = "Order Issues?", onClick = { /* Navigate to support */ }, icon = Icons.Default.Support)
            }
        }
    }
}

@Composable
fun TrackingStep(
    title: String,
    time: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean,
    icon: ImageVector
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = if (isCompleted) EmeraldPrimary else if (isCurrent) EmeraldLight else GrocerySurfaceVariant,
                border = if (isCurrent) androidx.compose.foundation.BorderStroke(2.dp, EmeraldPrimary) else null
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isCompleted) Color.White else if (isCurrent) EmeraldPrimary else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(48.dp)
                        .background(if (isCompleted) EmeraldPrimary else GrocerySurfaceVariant)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.padding(top = 4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold
                ),
                color = if (isCurrent) EmeraldPrimary else if (isCompleted) TextPrimary else TextMuted
            )
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
