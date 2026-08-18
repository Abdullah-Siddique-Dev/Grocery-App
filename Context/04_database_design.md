# Smart Grocery App — System Design
## 4. Database (MongoDB Atlas)

Five core collections make up the database: **Users, Categories, Products, Cart, Orders**.

### Collection Relationships

```mermaid
erDiagram
    USERS ||--o| CART : "userId"
    USERS ||--o{ ORDERS : "userId"
    CATEGORIES ||--o{ PRODUCTS : "categoryId"

    USERS {
        ObjectId _id
        string name
        string email
        string passwordHash
        string phoneNumber
        string address
        datetime createdAt
    }
    CATEGORIES {
        ObjectId _id
        string name
        string icon
        string imageUrl
        int displayOrder
    }
    PRODUCTS {
        ObjectId _id
        string name
        string description
        ObjectId categoryId
        double price
        string unit
        string imageUrl
        int stockQuantity
        boolean isAvailable
        datetime createdAt
    }
    CART {
        ObjectId _id
        ObjectId userId
        array items
        datetime updatedAt
    }
    ORDERS {
        ObjectId _id
        ObjectId userId
        array items
        double totalAmount
        string deliveryAddress
        string status
        datetime placedAt
    }
```

### Embedded Sub-Documents

**Cart.items[] → CartItem**

| Field | Type | Description |
|---|---|---|
| productId | ObjectId | Reference to a Products document |
| quantity | Int | Number of units selected |
| priceAtAdd | Double | Product price captured at time of add |

**Orders.items[] → OrderItem**

| Field | Type | Description |
|---|---|---|
| productId | ObjectId | Reference to a Products document |
| quantity | Int | Number of units ordered |
| price | Double | Price at time of order |

### Order Status Values

```mermaid
flowchart LR
    P["Pending"] --> C["Confirmed"] --> S["Shipped"] --> D["Delivered"]
    P --> X["Cancelled"]
    C --> X
```

### Key Relationships Summary

- `Users.userId` → `Cart` (one cart per user)
- `Users.userId` → `Orders` (many orders per user)
- `Categories.categoryId` → `Products` (many products per category)
