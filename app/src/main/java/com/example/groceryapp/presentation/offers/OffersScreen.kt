package com.example.groceryapp.presentation.offers

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groceryapp.ui.components.GroceryTopBar
import com.example.groceryapp.ui.theme.*

@Composable
fun OffersScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            GroceryTopBar(
                title = "Offers & Coupons",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Loyalty Reward Banner Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(EmeraldDeep, EmeraldPrimary)))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Gold Tier Member", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Earned 450 total loyalty points", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
                    }
                    Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.2f)) {
                        Icon(Icons.Default.Stars, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(44.dp).padding(8.dp))
                    }
                }
            }

            // Interactive Scratch Card section
            Text(text = "Your Unused Scratch Cards", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFCBD5E1)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color.White)
                        Text(text = "Tap to Scratch", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFCBD5E1)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color.White)
                        Text(text = "Tap to Scratch", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    }
                }
            }

            // Available Promo Codes List
            Text(text = "Best Promo Codes For You", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            
            CouponCard(code = "FRESH50", disc = "Save $50 flat on first 3 organic orders", minVal = "Minimum order value $200")
            CouponCard(code = "SUPERMEAT", disc = "Get 20% instant cashback on Protein category", minVal = "Minimum order value $150")
            CouponCard(code = "ECOMILK", disc = "Free organic whole dairy milk pack bundle", minVal = "Valid once per verified customer account")
        }
    }
}

@Composable
fun CouponCard(code: String, disc: String, minVal: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, GrocerySurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = EmeraldLight,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = code,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                        color = EmeraldPrimary
                    )
                }
                Text(
                    text = "APPLY",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = EmeraldPrimary,
                    modifier = androidx.compose.ui.Modifier.background(Color.Transparent)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = disc, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = minVal, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
    }
}
