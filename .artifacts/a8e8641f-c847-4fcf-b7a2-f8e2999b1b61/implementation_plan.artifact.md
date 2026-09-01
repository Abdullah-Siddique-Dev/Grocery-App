# Final Integration & Quality Review Fixes

Targeted fixes to address inconsistencies found during the integration review.

## User Review Required

> [!NOTE]
> These changes fix critical data type mismatches between the Android app and Backend, especially regarding Address handling and Order pricing security.

## Proposed Changes

### Backend Consistency & Security

#### [MODIFY] [AuthDto.kt](file:///D:/Grocerey%20App/backend/src/main/kotlin/com/example/groceryapp/models/AuthDto.kt)
* Update `UserDto` to use `Address?` instead of `String` for the `address` field to match the `User` model and Android DTO.
* Fix `toDto()` mapper to correctly pass the `Address` object.

#### [MODIFY] [AuthService.kt](file:///D:/Grocerey%20App/backend/src/main/kotlin/com/example/groceryapp/services/AuthService.kt)
* In `register`, convert the incoming address string into an `Address` object (initializing the first line) to satisfy the `User` model requirements.

#### [MODIFY] [ProductService.kt](file:///D:/Grocerey%20App/backend/src/main/kotlin/com/example/groceryapp/services/ProductService.kt)
* Add validation to `createProduct` and `updateProduct` to ensure `stockQuantity` is not negative.

#### [MODIFY] [OrderService.kt](file:///D:/Grocerey%20App/backend/src/main/kotlin/com/example/groceryapp/services/OrderService.kt)
* **Price Integrity**: Update `placeOrder` to fetch current product prices from the `ProductRepository` instead of trusting the price sent from the client's cart.

---

### Android Quality & UI

#### [MODIFY] [CheckoutViewModel.kt](file:///D:/Grocerey%20App/app/src/main/java/com/example/groceryapp/presentation/orders/CheckoutViewModel.kt)
* Add an `updateAddress` method to allow updating the delivery address directly from the checkout flow.

#### [MODIFY] [CheckoutScreen.kt](file:///D:/Grocerey%20App/app/src/main/java/com/example/groceryapp/presentation/orders/CheckoutScreen.kt)
* Implement the `onSave` callback for `AddressEditDialog` to actually persist address changes.

## Verification Plan

### Manual Verification
1.  **Registration**: Register a new user and verify their profile loads correctly with the address string mapped to an `Address` object.
2.  **Stock Integrity**: Attempt to set a negative stock for a product via Admin and verify it is rejected.
3.  **Price Integrity**: Manually modify a cart's `priceAtAdd` in the database and verify the placed order uses the current product price from the products collection instead.
4.  **Checkout Flow**: Update an address in the Checkout screen and verify it updates the UI and persists.
