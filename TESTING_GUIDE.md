# 🧪 TESTING GUIDE - Smart Grocery App

## 📋 HOW TO TEST ALL NEW FEATURES

After building and installing the app, follow these test scenarios:

---

## 🚀 STEP 1: BUILD & INSTALL

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install on device/emulator
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🧪 TEST SCENARIO 1: Notifications

### Steps:
1. Launch app and login
2. You should be on **Home Screen**
3. Look at top-right corner
4. You should see **3 icons**: 🔔 (bell), 🎁 (gift), 👤 (profile)
5. **Tap the bell icon** (notification)

### Expected Result:
- ✅ NotificationsScreen opens
- ✅ See 4 notification cards:
  1. 🚚 ORDER - "Your order has been delivered!" (Emerald green)
  2. 🎉 OFFER - "Weekend Flash Sale!" (Amber/yellow)
  3. 💰 PRICE_DROP - "Price drop on your favorites" (Blue)
  4. 📦 STOCK - "Items back in stock" (Purple)
- ✅ Each card shows icon, title, description, timestamp
- ✅ Tap back button → Returns to Home

### ❌ If It Fails:
- Check Screen.Notifications route is added
- Check NotificationsScreen composable is in AppNavigation
- Check import is present

---

## 🧪 TEST SCENARIO 2: Offers & Coupons

### Steps:
1. From **Home Screen**
2. Look at top-right corner
3. **Tap the yellow/amber gift icon** (between bell and profile)

### Expected Result:
- ✅ OffersScreen opens
- ✅ See **Gold Tier Loyalty Banner** with gradient
- ✅ See **2 Scratch Cards**:
  - "Mystery Discount" (Scratch to win!)
  - "Lucky Draw" (Scratch to win!)
- ✅ See **3 Promo Code Cards**:
  1. FRESH50 - "50% OFF Fresh Vegetables"
  2. SUPERMEAT - "30% OFF Meat & Protein"
  3. ECOMILK - "25% OFF Dairy Products"
- ✅ Each promo shows minimum order value
- ✅ Each promo has green "APPLY" button
- ✅ Tap back button → Returns to Home

### ❌ If It Fails:
- Check Screen.Offers route is added
- Check offers button is in HomeTopBar
- Check onOffersClick is wired correctly

---

## 🧪 TEST SCENARIO 3: Help & Support

### Steps:
1. Tap **Profile** tab (bottom navigation)
2. Scroll down past user info and address section
3. You should see **green "Help & Support" button** above red Logout
4. **Tap "Help & Support"**

### Expected Result:
- ✅ HelpScreen opens
- ✅ See **2 Quick Contact Tiles**:
  - 💬 "Start Live Chat" - Avg wait: 5 mins
  - 📞 "Call Support" - Avg wait: 2 mins
- ✅ See **FAQ Section** with 3 questions:
  1. "How long does delivery take?"
  2. "Can I cancel or modify my order?"
  3. "Are your products certified organic?"
- ✅ **Tap any FAQ question** → Expands to show answer
- ✅ Tap same question again → Collapses
- ✅ Tap back button → Returns to Profile

### ❌ If It Fails:
- Check Screen.Help route is added
- Check Help button is in ProfileScreen
- Check onNavigateToHelp is passed in AppNavigation

---

## 🧪 TEST SCENARIO 4: Order Tracking

### Steps:
1. Tap **Orders** tab (bottom navigation)
2. **Tap any order** in the list
3. OrderDetailsScreen opens
4. Look for **"Track Order Live"** button (blue/emerald)
   - NOTE: Button only shows for CONFIRMED or OUT_FOR_DELIVERY orders
5. **Tap "Track Order Live"**

### Expected Result:
- ✅ OrderTrackingScreen opens
- ✅ See **GPS Map Mockup** (City Street Map with blue gradient)
- ✅ See **ANIMATED DELIVERY AGENT PULSE** (breathing green circle)
- ✅ See **"Arriving in 12 mins"** at the top
- ✅ See **4-Stage Milestone Timeline**:
  1. ✅ Order Placed (9:30 AM) - COMPLETED
  2. ✅ Order Packed (9:45 AM) - COMPLETED
  3. ✅ Out for Delivery (10:15 AM) - CURRENT (emerald)
  4. ⏳ Delivered (10:42 AM) - PENDING (grey)
- ✅ See **Delivery Partner Card**:
  - Name, phone, rating (4.8⭐)
- ✅ See **"Call Delivery Partner"** button (green)
- ✅ See **"Order Issues?"** button (grey)
- ✅ Tap back button → Returns to Order Details

### ❌ If It Fails:
- Check Screen.OrderTracking route is added
- Check Track button is in OrderDetailsScreen
- Check onNavigateToTracking is passed correctly
- Verify order status is CONFIRMED or OUT_FOR_DELIVERY

---

## 🧪 TEST SCENARIO 5: Product Catalog

### Steps:
1. From **Home Screen**, scroll down
2. **Tap "Browse" tab** (bottom navigation, 2nd icon)
3. Browse categories

### Expected Result:
- ✅ See **8 Categories** with icons:
  - 🥬 Vegetables
  - 🍎 Fruits
  - 🥛 Dairy & Eggs
  - 🍞 Bakery & Bread
  - 🥩 Meat & Protein
  - 🥫 Pantry Essentials
  - 🍫 Snacks & Treats
  - 🥤 Beverages
- ✅ **Tap any category** → See 8+ products
- ✅ Each product shows:
  - ✅ Beautiful Unsplash image
  - ✅ ⭐ Star rating (4.5)
  - ✅ ❤️ Wishlist heart icon (top-right)
  - ✅ Product name
  - ✅ Price in emerald green
  - ✅ Stock indicator ("Only 5 left!" or "In Stock")
  - ✅ + button to add to cart

### ❌ If It Fails:
- Check backend is running
- Check SeedData.seedIfNeeded() was called
- Check MongoDB connection
- Check internet connection for images

---

## 🧪 TEST SCENARIO 6: Enhanced Product Card

### Steps:
1. Browse to any category
2. Look at **any product card**

### Expected Result:
- ✅ Top-left: Discount badge (if on sale)
- ✅ Top-right: **❤️ Wishlist heart** (outline)
- ✅ Center: Product image
- ✅ Below image: **⭐⭐⭐⭐⭐ 4.5** rating
- ✅ Product name
- ✅ Unit/quantity (e.g., "500g")
- ✅ Price (emerald green, bold)
- ✅ Bottom: Stock indicator
  - If stock < 10: **"Only X left!"** (amber)
  - If stock >= 10: **"In Stock"** (emerald)
- ✅ **Tap heart icon** → Fills with red (favorited)
- ✅ **Tap again** → Returns to outline (unfavorited)

---

## 🧪 TEST SCENARIO 7: Admin Dashboard

### Steps:
1. Login as **admin user**
   - Email: admin@example.com
   - Password: admin123
2. From Home, navigate to admin section
3. Tap **Admin Dashboard**

### Expected Result:
- ✅ See **2 Metric Cards** at top:
  - Revenue: $24,850 (+18.2%)
  - Active Users: 1,420 (+12.4%)
- ✅ See **"Weekly Sales Analytics"** section
- ✅ See **BAR CHART** with 7 days (Mon-Sun)
  - Bars have different heights
  - Thursday is highlighted in amber (peak day)
- ✅ See **"Inventory Stock Alerts"** section
  - Red warning card
  - "3 Items running low in stock"
  - Mentions Spinach and Bananas
- ✅ See **4 Control Cards**:
  1. Order Pipeline Management (emerald)
  2. Product Inventory Catalog (blue)
  3. Category Mapping Directory (amber)
  4. User Registries & Roles (purple)
- ✅ Each card has icon, title, subtitle, chevron

---

## 🧪 TEST SCENARIO 8: Back Navigation

Test that all back buttons work:

- [ ] Notifications → Back → Home ✓
- [ ] Offers → Back → Home ✓
- [ ] Help → Back → Profile ✓
- [ ] Order Tracking → Back → Order Details ✓
- [ ] Order Details → Back → Orders ✓
- [ ] Product Details → Back → Products ✓

---

## 🧪 TEST SCENARIO 9: Bottom Navigation

Test all 5 tabs work:

- [ ] Home tab → HomeScreen ✓
- [ ] Browse tab → CategoriesScreen ✓
- [ ] Cart tab → CartScreen ✓
- [ ] Orders tab → OrdersScreen ✓
- [ ] Profile tab → ProfileScreen ✓

---

## 🧪 TEST SCENARIO 10: Complete User Flow

Full end-to-end test:

1. **Launch app** → Login
2. **Home** → Tap notification bell → See notifications → Back
3. **Home** → Tap offers icon → See coupons → Back
4. **Home** → Tap category → Tap product → See details
5. **Product Details** → Tap heart (favorite) → Tap "Add to Cart"
6. **Navigate to Cart** → See item → Tap checkout
7. **Checkout** → Enter address → Place order
8. **Navigate to Orders** → Tap order → See details
9. **Order Details** → Tap "Track Order Live" → See map
10. **Navigate to Profile** → Tap "Help & Support" → See FAQ
11. **Profile** → Tap Logout → Returns to Login

---

## ✅ CHECKLIST

After testing, verify:

### Navigation:
- [ ] All 4 new screens are accessible
- [ ] All back buttons work
- [ ] No navigation crashes
- [ ] Deep linking works (if applicable)

### UI:
- [ ] All buttons are visible
- [ ] All icons are correct colors
- [ ] All animations work smoothly
- [ ] No UI overlaps or clipping

### Data:
- [ ] Products load with images
- [ ] Categories show correct counts
- [ ] Cart operations work
- [ ] Orders display correctly

### Features:
- [ ] Notifications display properly
- [ ] Offers can be applied
- [ ] Help FAQs expand/collapse
- [ ] Order tracking shows animation
- [ ] Wishlist heart toggles
- [ ] Stock indicators accurate

---

## ❌ COMMON ISSUES & FIXES

### Issue: "Screens not opening"
**Fix**: Check Screen.kt has all 4 routes added

### Issue: "Buttons not visible"
**Fix**: Check HomeScreen.kt and ProfileScreen.kt have buttons added

### Issue: "No products showing"
**Fix**: 
1. Check backend is running
2. Check MongoDB connection
3. Check SeedData was called
4. Check internet for images

### Issue: "Images not loading"
**Fix**:
1. Check internet connection
2. Verify Unsplash URLs are valid
3. Check Coil dependency is working

### Issue: "Build errors"
**Fix**:
1. Run `./gradlew clean`
2. Sync Gradle files
3. Check all imports are present
4. Verify function signatures match

---

## 🎯 SUCCESS CRITERIA

**TEST PASSES IF:**
- ✅ All 4 new screens open and work
- ✅ All navigation flows work correctly
- ✅ All UI elements are visible and functional
- ✅ 64+ products load with images
- ✅ No crashes or errors
- ✅ Back navigation works everywhere

**APP IS PRODUCTION-READY IF:**
- ✅ All 10 test scenarios pass
- ✅ All items in checklist are checked
- ✅ No critical bugs found
- ✅ Performance is smooth (no lag)

---

## 📊 TEST REPORT TEMPLATE

```
TEST DATE: __________
TESTER: __________
DEVICE: __________
ANDROID VERSION: __________

RESULTS:
[ ] Scenario 1: Notifications - PASS / FAIL
[ ] Scenario 2: Offers - PASS / FAIL
[ ] Scenario 3: Help - PASS / FAIL
[ ] Scenario 4: Tracking - PASS / FAIL
[ ] Scenario 5: Products - PASS / FAIL
[ ] Scenario 6: Product Card - PASS / FAIL
[ ] Scenario 7: Admin - PASS / FAIL
[ ] Scenario 8: Back Nav - PASS / FAIL
[ ] Scenario 9: Bottom Nav - PASS / FAIL
[ ] Scenario 10: Full Flow - PASS / FAIL

OVERALL: PASS / FAIL

NOTES:
__________________________________________
__________________________________________
```

---

**Happy Testing! 🧪🚀**

*If all tests pass, your app is ready for production deployment!*
