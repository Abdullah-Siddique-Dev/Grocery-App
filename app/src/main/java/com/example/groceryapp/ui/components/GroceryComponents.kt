package com.example.groceryapp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groceryapp.domain.model.Category
import com.example.groceryapp.domain.model.Product
import com.example.groceryapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryTopBar(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent // Allow background to show through or scaffold to handle
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrocerySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search fresh groceries...",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceWhite
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxSize(),
            placeholder = { 
                Text(
                    placeholder, 
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                ) 
            },
            leadingIcon = { 
                Icon(
                    Icons.Default.Search, 
                    contentDescription = null,
                    tint = EmeraldPrimary
                ) 
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    onProductClick: (String) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    quantityInCart: Int = 0
) {
    var isPressed by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_press"
    )

    Card(
        modifier = modifier
            .width(ComponentSize.productCardWidth)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { 
                onProductClick(product.id)
            },
        shape = RoundedCornerShape(CornerRadius.md),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Elevation.xs,
            pressedElevation = Elevation.md
        )
    ) {
        Column {
            // Product Image with Badge Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                ProductImage(
                    imageUrl = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = CornerRadius.md, topEnd = CornerRadius.md))
                        .background(GrocerySurfaceVariant)
                )
                
                // Discount/Sale Badge - Premium styled
                Surface(
                    modifier = Modifier
                        .padding(Spacing.xs)
                        .align(Alignment.TopStart),
                    color = AccentAmber,
                    shape = RoundedCornerShape(CornerRadius.xs),
                    shadowElevation = Elevation.sm
                ) {
                    Text(
                        text = "15% OFF",
                        modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                }
                
                // Favorite/Wishlist Heart Icon Button
                Surface(
                    modifier = Modifier
                        .padding(Spacing.xs)
                        .size(32.dp)
                        .align(Alignment.TopEnd)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { isFavorite = !isFavorite },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f),
                    shadowElevation = Elevation.xs
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Add to favorites",
                            tint = if (isFavorite) StatusError else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(Spacing.sm)) {
                // Product Name
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = TextPrimary,
                    minLines = 2,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(Spacing.xxs))
                
                // Row for Unit/Quantity and Interactive Star Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.unit,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    
                    // Rating indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AccentAmber,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "4.5",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))

                // Low Stock / Available Indicator
                if (product.stockQuantity < 10) {
                    Text(
                        text = "Only ${product.stockQuantity} left!",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = StatusError
                    )
                } else {
                    Text(
                        text = "In Stock",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = EmeraldPrimary
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xs))
                
                // Price and Add Button Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Price Section
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "$${String.format("%.2f", product.price)}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            ),
                            color = EmeraldPrimary
                        )
                        
                        Text(
                            text = "$${String.format("%.2f", product.price * 1.15)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = TextDecoration.LineThrough
                              ),
                            color = TextMuted
                        )
                    }
                    
                    // Add to Cart Button with Animation
                    AnimatedContent(
                        targetState = quantityInCart > 0,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith 
                            fadeOut(animationSpec = tween(300))
                        },
                        label = "add_button_animation"
                    ) { hasQuantity ->
                        if (hasQuantity) {
                            // Quantity Selector (mini version)
                            Surface(
                                shape = RoundedCornerShape(CornerRadius.xs),
                                color = EmeraldLight,
                                modifier = Modifier.height(36.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "In cart",
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(IconSize.sm)
                                    )
                                    Text(
                                        text = quantityInCart.toString(),
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldPrimary
                                    )
                                }
                            }
                        } else {
                            // Add Button
                            Surface(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { 
                                        onAddClick()
                                    },
                                color = EmeraldPrimary,
                                shape = RoundedCornerShape(CornerRadius.xs),
                                shadowElevation = Elevation.sm
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add to cart",
                                        tint = Color.White,
                                        modifier = Modifier.size(IconSize.sm)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "category_scale"
    )
    
    Card(
        modifier = modifier
            .width(ComponentSize.categoryCardWidth)
            .scale(scale)
            .clickable { onCategoryClick(category.id) },
        shape = RoundedCornerShape(CornerRadius.md),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) EmeraldLight else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) Elevation.sm else Elevation.xs
        )
    ) {
        Column(
            modifier = Modifier
                .padding(Spacing.sm)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            // Category Icon/Image Container
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = if (isSelected) Color.White else EmeraldLight.copy(alpha = 0.5f),
                shadowElevation = if (isSelected) Elevation.sm else Elevation.none
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(Spacing.xs)
                ) {
                    ProductImage(
                        imageUrl = category.imageUrl,
                        contentDescription = category.name,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            // Category Name
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (isSelected) EmeraldPrimary else TextPrimary,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    onActionClick: (() -> Unit)? = null,
    actionText: String = "View All",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        if (onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionText,
                    color = EmeraldPrimary,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "button_press"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(ComponentSize.buttonHeight)
            .scale(scale),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(CornerRadius.sm),
        colors = ButtonDefaults.buttonColors(
            containerColor = EmeraldPrimary,
            contentColor = Color.White,
            disabledContainerColor = TextMuted,
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Elevation.sm,
            pressedElevation = Elevation.none,
            disabledElevation = Elevation.none
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.5.dp
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        } else {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(IconSize.md)
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(ComponentSize.buttonHeight),
        enabled = enabled,
        shape = RoundedCornerShape(CornerRadius.sm),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = EmeraldPrimary,
            disabledContentColor = TextMuted
        ),
        border = ButtonDefaults.outlinedButtonBorder(enabled).copy(
            brush = Brush.linearGradient(listOf(EmeraldPrimary, EmeraldPrimary))
        )
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(IconSize.md)
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
    minQuantity: Int = 1
) {
    val quantityAnimated by animateIntAsState(
        targetValue = quantity,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "quantity_change"
    )
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(CornerRadius.sm),
        color = EmeraldLight,
        shadowElevation = Elevation.xs
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
        ) {
            // Decrement Button
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clickable(
                        enabled = quantity > minQuantity,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onDecrement() },
                shape = RoundedCornerShape(CornerRadius.xs),
                color = if (quantity > minQuantity) Color.White else Color.White.copy(alpha = 0.5f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (quantity <= minQuantity) Icons.Default.Delete else Icons.Default.Remove,
                        contentDescription = if (quantity <= minQuantity) "Remove" else "Decrease",
                        tint = if (quantity <= minQuantity) StatusError else EmeraldPrimary,
                        modifier = Modifier.size(IconSize.sm)
                    )
                }
            }
            
            // Quantity Display
            AnimatedContent(
                targetState = quantityAnimated,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically { -it } + fadeIn() togetherWith
                        slideOutVertically { it } + fadeOut()
                    } else {
                        slideInVertically { it } + fadeIn() togetherWith
                        slideOutVertically { -it } + fadeOut()
                    }
                },
                label = "quantity_display"
            ) { animatedQuantity ->
                Text(
                    text = animatedQuantity.toString(),
                    modifier = Modifier.widthIn(min = 24.dp),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            }
            
            // Increment Button
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onIncrement() },
                shape = RoundedCornerShape(CornerRadius.xs),
                color = EmeraldPrimary,
                shadowElevation = Elevation.xs
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = Color.White,
                        modifier = Modifier.size(IconSize.sm)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    val (backgroundColor, textColor, icon) = when (status.lowercase()) {
        "completed", "confirmed", "delivered" -> Triple(
            EmeraldLight,
            EmeraldPrimary,
            Icons.Default.CheckCircle
        )
        "success" -> Triple(
            Color(0xFFD1FAE5),
            Color(0xFF065F46),
            Icons.Default.CheckCircle
        )
        "pending", "processing" -> Triple(
            Color(0xFFFEF3C7),
            Color(0xFF92400E),
            Icons.Default.Schedule
        )
        "cancelled", "error", "failed" -> Triple(
            Color(0xFFFEE2E2),
            StatusError,
            Icons.Default.Cancel
        )
        "shipped", "out for delivery" -> Triple(
            Color(0xFFDBEAFE),
            Color(0xFF1E40AF),
            Icons.Default.LocalShipping
        )
        else -> Triple(
            GrocerySurfaceVariant,
            TextSecondary,
            Icons.Default.Info
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(CornerRadius.full),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showIcon) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = status.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = textColor
            )
        }
    }
}

@Composable
fun PriceSummaryRow(
    label: String,
    value: String,
    isTotal: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold) 
                    else MaterialTheme.typography.bodyLarge,
            color = if (isTotal) TextPrimary else TextSecondary
        )
        Text(
            text = value,
            style = if (isTotal) MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = EmeraldPrimary
            ) else MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isTotal) EmeraldPrimary else TextPrimary
        )
    }
}

@Composable
fun EmptyState(
    title: String,
    description: String,
    icon: ImageVector = Icons.Default.ShoppingCart,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .padding(Spacing.xxxl)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon Container with Animation
        Box(
            modifier = Modifier
                .size(IconSize.huge)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            EmeraldLight,
                            EmeraldLight.copy(alpha = 0.3f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(IconSize.xxl),
                tint = EmeraldPrimary.copy(alpha = 0.5f)
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing.xl))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(Spacing.sm))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.xl)
        )
        
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(Spacing.xl))
            PrimaryButton(
                text = actionText,
                onClick = onActionClick,
                modifier = Modifier.widthIn(max = 200.dp)
            )
        }
    }
}

@Composable
fun ErrorState(
    title: String = "Something went wrong",
    description: String = "We couldn't load the data. Please try again.",
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        title = title,
        description = description,
        icon = Icons.Default.ErrorOutline,
        modifier = modifier,
        actionText = "Try Again",
        onActionClick = onRetry
    )
}

@Composable
fun LoadingStateShimmer(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    
    Box(
        modifier = modifier.background(
            color = GrocerySurfaceVariant.copy(alpha = shimmerAlpha)
        )
    )
}


// ============================================================
// PREMIUM ADDITIONAL COMPONENTS
// ============================================================

@Composable
fun CartItemCard(
    product: Product,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.md),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs)
    ) {
        Row(
            modifier = Modifier
                .padding(Spacing.sm)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // Product Image
            ProductImage(
                imageUrl = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(ComponentSize.cartItemImageSize)
                    .clip(RoundedCornerShape(CornerRadius.sm))
                    .background(GrocerySurfaceVariant)
            )
            
            // Product Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(ComponentSize.cartItemImageSize),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = TextPrimary
                    )
                    Text(
                        text = product.unit,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%.2f", product.price * quantity)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = EmeraldPrimary
                    )
                    
                    QuantitySelector(
                        quantity = quantity,
                        onIncrement = { onQuantityChange(quantity + 1) },
                        onDecrement = { 
                            if (quantity > 1) {
                                onQuantityChange(quantity - 1)
                            } else {
                                onRemove()
                            }
                        },
                        minQuantity = 1
                    )
                }
            }
            
            // Remove Button
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = TextMuted,
                    modifier = Modifier.size(IconSize.sm)
                )
            }
        }
    }
}

@Composable
fun OrderCard(
    orderId: String,
    date: String,
    itemCount: Int,
    totalAmount: Double,
    status: String,
    onOrderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOrderClick() },
        shape = RoundedCornerShape(CornerRadius.md),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${orderId.take(8)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(IconSize.xs)
                        )
                        Text(
                            text = date,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                
                StatusBadge(status = status)
            }
            
            HorizontalDivider(color = GrocerySurfaceVariant, thickness = 1.dp)
            
            // Order Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(IconSize.sm)
                    )
                    Text(
                        text = "$itemCount items",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                
                Text(
                    text = "$${String.format("%.2f", totalAmount)}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = EmeraldPrimary
                )
            }
            
            // View Details Button
            TextButton(
                onClick = onOrderClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = EmeraldPrimary)
            ) {
                Text(
                    text = "View Details",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(IconSize.sm)
                )
            }
        }
    }
}

@Composable
fun DiscountBadge(
    discountPercentage: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = AccentAmber,
        shape = RoundedCornerShape(CornerRadius.xs),
        shadowElevation = Elevation.sm
    ) {
        Text(
            text = "-$discountPercentage%",
            modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = Color.White
        )
    }
}

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    singleLine: Boolean = true
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            placeholder = { Text(placeholder, color = TextMuted) },
            leadingIcon = if (leadingIcon != null) {
                { Icon(leadingIcon, contentDescription = null, tint = if (isError) StatusError else EmeraldPrimary) }
            } else null,
            trailingIcon = trailingIcon,
            isError = isError,
            singleLine = singleLine,
            shape = RoundedCornerShape(CornerRadius.sm),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EmeraldPrimary,
                unfocusedBorderColor = TextMuted.copy(alpha = 0.3f),
                errorBorderColor = StatusError,
                focusedLabelColor = EmeraldPrimary,
                unfocusedLabelColor = TextSecondary,
                cursorColor = EmeraldPrimary
            ),
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation
        )
        
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(Spacing.xxs))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = StatusError,
                modifier = Modifier.padding(start = Spacing.md)
            )
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    content: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(CornerRadius.md),
        colors = CardDefaults.cardColors(containerColor = EmeraldLight),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)
    ) {
        Row(
            modifier = Modifier
                .padding(Spacing.md)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(IconSize.md)
                    )
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = TextSecondary
                )
                Text(
                    text = content,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }
            
            if (onClick != null) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Edit",
                    tint = TextSecondary
                )
            }
        }
    }
}
