package com.example.groceryapp.data.dto

import com.example.groceryapp.domain.model.Cart
import com.example.groceryapp.domain.model.CartItem
import com.example.groceryapp.domain.model.Category
import com.example.groceryapp.domain.model.Order
import com.example.groceryapp.domain.model.OrderItem
import com.example.groceryapp.domain.model.Product
import com.example.groceryapp.domain.model.User

fun UserDto.toDomain() = User(
    id = id ?: "",
    name = name,
    email = email,
    phoneNumber = phoneNumber,
    address = address,
    createdAt = createdAt
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
    updatedAt = updatedAt
)

fun CartItemDto.toDomain() = CartItem(
    productId = productId,
    quantity = quantity,
    priceAtAdd = priceAtAdd
)

fun OrderDto.toDomain() = Order(
    id = id ?: "",
    userId = userId,
    items = items.map { it.toDomain() },
    totalAmount = totalAmount,
    deliveryAddress = deliveryAddress,
    status = status,
    placedAt = placedAt
)

fun OrderItemDto.toDomain() = OrderItem(
    productId = productId,
    quantity = quantity,
    price = price
)
