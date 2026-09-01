package com.example.groceryapp.data.dto

import com.example.groceryapp.domain.model.*

fun UserDto.toDomain() = User(
    id = id ?: "",
    name = name,
    email = email,
    phoneNumber = phoneNumber,
    address = address?.toDomain(),
    role = role,
    createdAt = createdAt
)

fun AddressDto.toDomain() = Address(
    fullName = fullName,
    phoneNumber = phoneNumber,
    addressLine = addressLine,
    city = city,
    postalCode = postalCode
)

fun Address.toDto() = AddressDto(
    fullName = fullName,
    phoneNumber = phoneNumber,
    addressLine = addressLine,
    city = city,
    postalCode = postalCode
)

fun CategoryDto.toDomain() = Category(
    id = id ?: "",
    name = name,
    icon = icon,
    imageUrl = imageUrl,
    displayOrder = displayOrder
)

fun ProductDto.toDomain() = Product(
    id = id ?: "",
    name = name,
    description = description,
    categoryId = categoryId,
    price = price,
    unit = unit,
    imageUrl = imageUrl,
    stockQuantity = stockQuantity,
    isAvailable = isAvailable,
    createdAt = createdAt
)

fun CartDto.toDomain() = Cart(
    id = id ?: "",
    userId = userId,
    items = items.map { it.toDomain() },
    total = total,
    updatedAt = updatedAt
)

fun CartItemDto.toDomain() = CartItem(
    productId = productId,
    productName = productName,
    productImageUrl = productImageUrl,
    quantity = quantity,
    price = price,
    subtotal = subtotal,
    stockQuantity = stockQuantity
)

fun OrderDto.toDomain() = Order(
    id = id ?: "",
    userId = userId,
    items = items.map { it.toDomain() },
    totalAmount = totalAmount,
    deliveryAddress = deliveryAddress.toDomain(),
    status = status,
    paymentMethod = paymentMethod,
    paymentStatus = paymentStatus,
    placedAt = placedAt
)

fun OrderItemDto.toDomain() = OrderItem(
    productId = productId,
    quantity = quantity,
    price = price
)

fun ReviewDto.toDomain() = Review(
    id = id ?: "",
    productId = productId,
    userId = userId,
    userName = userName,
    rating = rating,
    comment = comment,
    createdAt = createdAt
)

fun ReviewSummaryDto.toDomain() = ReviewSummary(
    averageRating = averageRating,
    totalReviews = totalReviews
)

fun ProductReviewsResponseDto.toDomain() = ProductReviews(
    summary = summary.toDomain(),
    reviews = reviews.map { it.toDomain() }
)

fun Product.toDto() = ProductDto(
    id = id,
    name = name,
    description = description,
    categoryId = categoryId,
    price = price,
    unit = unit,
    imageUrl = imageUrl,
    stockQuantity = stockQuantity,
    isAvailable = isAvailable,
    createdAt = createdAt
)

fun Category.toDto() = CategoryDto(
    id = id,
    name = name,
    icon = icon,
    imageUrl = imageUrl,
    displayOrder = displayOrder
)
