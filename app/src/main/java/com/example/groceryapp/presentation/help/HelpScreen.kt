package com.example.groceryapp.presentation.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groceryapp.ui.components.GroceryTopBar
import com.example.groceryapp.ui.components.PrimaryButton
import com.example.groceryapp.ui.theme.*

@Composable
fun HelpScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            GroceryTopBar(
                title = "Help & Support",
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
            // Direct contact tiles
            Text(text = "Connect with Support Agent", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SupportTile(
                    modifier = Modifier.weight(1f),
                    title = "Live Chat",
                    desc = "Average wait: 1 min",
                    icon = Icons.Default.Chat,
                    iconBg = EmeraldLight,
                    iconTint = EmeraldPrimary
                )
                SupportTile(
                    modifier = Modifier.weight(1f),
                    title = "Phone Call",
                    desc = "24/7 dedicated line",
                    icon = Icons.Default.Call,
                    iconBg = Color(0xFFE0F2FE),
                    iconTint = Color(0xFF0284C7)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            // Accordion FAQ section
            Text(text = "Frequently Asked Questions", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            
            FaqAccordion(q = "What is the expected delivery time?", a = "Our express delivery riders typically deliver all active fresh grocery bundles within 15 to 30 minutes depending on your real-time GPS location distance from our closest dark store hub.")
            FaqAccordion(q = "How do I request a cancellation or refund?", a = "You can easily click into your Order Details screen right up until packing is initiated to cancel instantly. Refund balance returns back to original accounts within 2-3 business bank days.")
            FaqAccordion(q = "Are all vegetables completely certified organic?", a = "Yes, absolutely! We source 100% of our leafy greens, organic roots, and exotic vegetables directly from verified eco-friendly agricultural farms that comply with local regulatory organic certifications.")
        }
    }
}

@Composable
fun SupportTile(
    modifier: Modifier,
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, GrocerySurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = iconBg) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint)
                }
            }
            Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
fun FaqAccordion(q: String, a: String) {
    var expanded by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, GrocerySurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = q, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary, modifier = Modifier.weight(1f))
                Icon(imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = TextSecondary)
            }
            
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = a, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        }
    }
}
