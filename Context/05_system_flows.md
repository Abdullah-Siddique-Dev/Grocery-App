# Smart Grocery App — System Design
## 5. System Flows

Each flow shows request direction (solid) and response direction (dashed) across the
three layers: **Android Frontend → Ktor Backend → MongoDB Atlas**.

---

### Flow 1 — User Registration / Login

```mermaid
sequenceDiagram
    participant U as User
    participant A as Android<br/>Login/Register Screen
    participant K as Authentication API<br/>(Ktor)
    participant DB as Users Collection<br/>(MongoDB)

    U->>A: Enter credentials
    A->>K: POST /auth/register or /auth/login
    K->>DB: Validate / create user record
    DB-->>K: User document
    K-->>A: Authentication result (session/token)
    A-->>U: Logged-in UI
```

---

### Flow 2 — Browse Categories

```mermaid
sequenceDiagram
    participant U as User
    participant A as Android<br/>Categories Screen
    participant K as Category API
    participant DB as Categories Collection

    U->>A: Open Categories screen
    A->>K: GET /categories
    K->>DB: Fetch categories
    DB-->>K: Category list
    K-->>A: Category response (JSON)
    A-->>U: Render categories in UI
```

---

### Flow 3 — Browse / Search Products

```mermaid
sequenceDiagram
    participant U as User
    participant A as Android<br/>Product Screen
    participant K as Product API
    participant DB as Products Collection

    U->>A: Select category / enter search
    A->>K: GET /products?categoryId=... or ?q=...
    K->>DB: Query products (filter / search)
    DB-->>K: Matching products
    K-->>A: Product response (JSON)
    A-->>U: Render product list
```

- **Category filtering:** Category Selection → Product API → Products filtered by `categoryId` → Android UI
- **Search:** Search Query → Product API → Product Search → Products Collection → Search Results → Android UI

---

### Flow 4 — Add to Cart

```mermaid
sequenceDiagram
    participant U as User
    participant A as Android<br/>Product Screen
    participant K as Cart API
    participant DB as Cart Collection

    U->>A: Tap "Add to Cart"
    A->>K: POST /cart/items
    K->>DB: Insert CartItem {productId, quantity, priceAtAdd}
    DB-->>K: Updated cart
    K-->>A: Response
    A-->>U: Cart UI updated
```

---

### Flow 5 — Update / Remove Cart Item

```mermaid
sequenceDiagram
    participant U as User
    participant A as Android<br/>Cart Screen
    participant K as Cart API
    participant DB as Cart Collection

    U->>A: Change quantity / remove item
    A->>K: PUT /cart/items or DELETE /cart/items/{id}
    K->>DB: Update Cart document
    DB-->>K: Updated cart (total recalculated)
    K-->>A: Response
    A-->>U: Cart UI reflects change
```

Supports: update quantity, remove item, automatic total price calculation.

---

### Flow 6 — Place Order

```mermaid
sequenceDiagram
    participant U as User
    participant A as Android<br/>Cart Screen
    participant K as Order API
    participant DB as Orders Collection

    U->>A: Tap "Place Order"
    A->>K: POST /orders
    K->>DB: Persist order {userId, items, totalAmount,<br/>deliveryAddress, status: "Pending", placedAt}
    DB-->>K: Order confirmation
    K-->>A: Order confirmation response
    A-->>U: Order success UI
```

Initial order status is always **Pending**.

---

### Flow 7 — Order History / Status

```mermaid
sequenceDiagram
    participant U as User
    participant A as Android<br/>Orders Screen
    participant K as Order API
    participant DB as Orders Collection

    U->>A: Open Orders screen
    A->>K: GET /orders
    K->>DB: Fetch orders for userId
    DB-->>K: Previous orders + status
    K-->>A: Order list response
    A-->>U: Render order history
```

Possible statuses: `Pending → Confirmed → Shipped → Delivered` (or `Cancelled`).

---

### Flow 8 — User Profile

```mermaid
sequenceDiagram
    participant U as User
    participant A as Android<br/>Profile Screen
    participant K as User API
    participant DB as Users Collection

    U->>A: Open Profile screen
    A->>K: GET /user/profile
    K->>DB: Fetch user document
    DB-->>K: Profile data
    K-->>A: Profile response
    A-->>U: Render profile (name, email, phone, address)
```

---

### Error Handling Flow

```mermaid
sequenceDiagram
    participant A as Android<br/>Request
    participant K as Ktor Backend
    participant VM as Android ViewModel

    A->>K: Any request
    K->>K: Error occurs
    K->>K: Ktor Error Handling
    K-->>A: JSON Error Response
    A->>VM: Propagate error
    VM-->>A: UI Error State
```

No specific error codes are defined beyond the general error-handling path, per the
documented scope.
