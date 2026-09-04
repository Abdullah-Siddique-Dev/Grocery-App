$ErrorActionPreference = "Continue"
$ProgressPreference = "SilentlyContinue"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "BACKEND API SMOKE TEST" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Initialize test results
$results = @()

# Generate unique test data
$timestamp = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
$testEmail = "test_user_$timestamp@example.com"
$testPassword = "TestPassword123!"
$adminEmail = "admin@example.com"
$adminPassword = "admin123"

function Test-Api {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Uri,
        [hashtable]$Body = $null,
        [hashtable]$Headers = @{},
        [int]$ExpectedStatus = 200
    )
    
    try {
        $params = @{
            Uri = $Uri
            Method = $Method
            Headers = $Headers
            UseBasicParsing = $true
        }
        
        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json -Depth 10)
            $params.ContentType = "application/json"
        }
        
        $response = Invoke-WebRequest @params
        $status = $response.StatusCode
        $content = $response.Content | ConvertFrom-Json
        
        $pass = ($status -eq $ExpectedStatus)
        $script:results += [PSCustomObject]@{
            Test = $Name
            Status = $status
            Expected = $ExpectedStatus
            Result = if ($pass) { "PASS" } else { "FAIL" }
        }
        
        return @{ Success = $pass; Data = $content; Status = $status }
    }
    catch {
        $status = if ($_.Exception.Response) { $_.Exception.Response.StatusCode.value__ } else { 0 }
        $pass = ($status -eq $ExpectedStatus)
        $script:results += [PSCustomObject]@{
            Test = $Name
            Status = $status
            Expected = $ExpectedStatus
            Result = if ($pass) { "PASS" } else { "FAIL" }
        }
        
        return @{ Success = $pass; Data = $null; Status = $status }
    }
}

Write-Host "PHASE 1: ROOT ENDPOINT" -ForegroundColor Yellow
$root = Test-Api -Name "Root Endpoint" -Method GET -Uri "http://localhost:8080/"
Write-Host "  Root: $($root.Status)`n"

Write-Host "PHASE 2: AUTHENTICATION" -ForegroundColor Yellow

# Registration
$regResult = Test-Api -Name "User Registration" -Method POST -Uri "http://localhost:8080/auth/register" `
    -Body @{name="Test User"; email=$testEmail; password=$testPassword; phoneNumber="1234567890"; address="123 Test St"} `
    -ExpectedStatus 201

if ($regResult.Success) {
    $testUserId = $regResult.Data.user.id
    Write-Host "  ✓ Registration: $($regResult.Status) - User ID: $testUserId" -ForegroundColor Green
}

# Login
$loginResult = Test-Api -Name "User Login" -Method POST -Uri "http://localhost:8080/auth/login" `
    -Body @{email=$testEmail; password=$testPassword}

if ($loginResult.Success) {
    $token = $loginResult.Data.token
    Write-Host "  ✓ Login: $($loginResult.Status) - Token received" -ForegroundColor Green
} else {
    Write-Host "  ✗ Login failed" -ForegroundColor Red
    exit 1
}

# Profile
$profileResult = Test-Api -Name "Get Profile" -Method GET -Uri "http://localhost:8080/users/profile" `
    -Headers @{Authorization="Bearer $token"}
Write-Host "  Profile: $($profileResult.Status)`n"

Write-Host "PHASE 3: CATEGORIES & PRODUCTS" -ForegroundColor Yellow

$catResult = Test-Api -Name "Get Categories" -Method GET -Uri "http://localhost:8080/categories"
Write-Host "  Categories: $($catResult.Status)"

$prodResult = Test-Api -Name "Get Products" -Method GET -Uri "http://localhost:8080/products"
if ($prodResult.Success -and $prodResult.Data.Count -gt 0) {
    $testProductId = $prodResult.Data[0].id
    $testProductPrice = $prodResult.Data[0].price
    Write-Host "  Products: $($prodResult.Status) - Found $($prodResult.Data.Count) products"
    Write-Host "  Test Product ID: $testProductId"
}

$prodDetailResult = Test-Api -Name "Get Product by ID" -Method GET -Uri "http://localhost:8080/products/$testProductId"
Write-Host "  Product Detail: $($prodDetailResult.Status)`n"

Write-Host "PHASE 4: CART OPERATIONS" -ForegroundColor Yellow

$cartAddResult = Test-Api -Name "Add to Cart" -Method POST -Uri "http://localhost:8080/cart/items" `
    -Headers @{Authorization="Bearer $token"} `
    -Body @{productId=$testProductId; quantity=2}
Write-Host "  Add to Cart: $($cartAddResult.Status)"

$cartGetResult = Test-Api -Name "Get Cart" -Method GET -Uri "http://localhost:8080/cart" `
    -Headers @{Authorization="Bearer $token"}
Write-Host "  Get Cart: $($cartGetResult.Status)"

$cartUpdateResult = Test-Api -Name "Update Cart Quantity" -Method PUT -Uri "http://localhost:8080/cart/items/$testProductId" `
    -Headers @{Authorization="Bearer $token"} `
    -Body @{quantity=3}
Write-Host "  Update Quantity: $($cartUpdateResult.Status)"

$cartRemoveResult = Test-Api -Name "Remove from Cart" -Method DELETE -Uri "http://localhost:8080/cart/items/$testProductId" `
    -Headers @{Authorization="Bearer $token"}
Write-Host "  Remove from Cart: $($cartRemoveResult.Status)`n"

Write-Host "PHASE 5: FAVORITES" -ForegroundColor Yellow

$favAddResult = Test-Api -Name "Add Favorite" -Method POST -Uri "http://localhost:8080/favorites/$testProductId" `
    -Headers @{Authorization="Bearer $token"}
Write-Host "  Add Favorite: $($favAddResult.Status)"

$favGetResult = Test-Api -Name "Get Favorites" -Method GET -Uri "http://localhost:8080/favorites" `
    -Headers @{Authorization="Bearer $token"}
Write-Host "  Get Favorites: $($favGetResult.Status)"

$favRemoveResult = Test-Api -Name "Remove Favorite" -Method DELETE -Uri "http://localhost:8080/favorites/$testProductId" `
    -Headers @{Authorization="Bearer $token"}
Write-Host "  Remove Favorite: $($favRemoveResult.Status)`n"

Write-Host "PHASE 6: ADDRESS UPDATE" -ForegroundColor Yellow

$addressResult = Test-Api -Name "Update Address" -Method PUT -Uri "http://localhost:8080/users/address" `
    -Headers @{Authorization="Bearer $token"} `
    -Body @{fullName="Test User Updated"; phoneNumber="9876543210"; addressLine="123 Updated St"; city="Test City"; postalCode="12345"}
Write-Host "  Update Address: $($addressResult.Status)`n"

Write-Host "PHASE 7: ORDER CREATION" -ForegroundColor Yellow

# Re-add product to cart
Test-Api -Name "Re-add to Cart" -Method POST -Uri "http://localhost:8080/cart/items" `
    -Headers @{Authorization="Bearer $token"} `
    -Body @{productId=$testProductId; quantity=2} | Out-Null

$orderResult = Test-Api -Name "Create Order" -Method POST -Uri "http://localhost:8080/orders" `
    -Headers @{Authorization="Bearer $token"} `
    -Body @{
        deliveryAddress=@{fullName="Test User"; phoneNumber="1234567890"; addressLine="123 Test St"; city="Test City"; postalCode="12345"}
        paymentMethod="CASH_ON_DELIVERY"
    } -ExpectedStatus 201

if ($orderResult.Success) {
    $testOrderId = $orderResult.Data.id
    Write-Host "  ✓ Order Created: $($orderResult.Status) - Order ID: $testOrderId" -ForegroundColor Green
}

Write-Host "`nPHASE 8: ORDER OPERATIONS" -ForegroundColor Yellow

$ordersResult = Test-Api -Name "Get Orders" -Method GET -Uri "http://localhost:8080/orders" `
    -Headers @{Authorization="Bearer $token"}
Write-Host "  Get Orders: $($ordersResult.Status)"

$orderDetailResult = Test-Api -Name "Get Order Details" -Method GET -Uri "http://localhost:8080/orders/$testOrderId" `
    -Headers @{Authorization="Bearer $token"}
Write-Host "  Order Details: $($orderDetailResult.Status)"

$cancelResult = Test-Api -Name "Cancel Order" -Method PUT -Uri "http://localhost:8080/orders/$testOrderId/cancel" `
    -Headers @{Authorization="Bearer $token"}
Write-Host "  Cancel Order: $($cancelResult.Status)`n"

Write-Host "PHASE 9: ADMIN AUTHORIZATION" -ForegroundColor Yellow

$adminLoginResult = Test-Api -Name "Admin Login" -Method POST -Uri "http://localhost:8080/auth/login" `
    -Body @{email=$adminEmail; password=$adminPassword}

if ($adminLoginResult.Success) {
    $adminToken = $adminLoginResult.Data.token
    Write-Host "  ✓ Admin Login: $($adminLoginResult.Status)" -ForegroundColor Green
    
    $adminProdResult = Test-Api -Name "Admin Get Products" -Method GET -Uri "http://localhost:8080/admin/products" `
        -Headers @{Authorization="Bearer $adminToken"}
    Write-Host "  Admin Products Access: $($adminProdResult.Status)"
    
    $customerAdminResult = Test-Api -Name "Customer Try Admin Access" -Method GET -Uri "http://localhost:8080/admin/products" `
        -Headers @{Authorization="Bearer $token"} -ExpectedStatus 403
    Write-Host "  Customer Admin Access (should be 403): $($customerAdminResult.Status)"
}

Write-Host "`nPHASE 10: SECURITY TESTS" -ForegroundColor Yellow

$noAuthResult = Test-Api -Name "No Auth Access" -Method GET -Uri "http://localhost:8080/cart" -ExpectedStatus 401
Write-Host "  No Auth (should be 401): $($noAuthResult.Status)"

$invalidTokenResult = Test-Api -Name "Invalid Token" -Method GET -Uri "http://localhost:8080/cart" `
    -Headers @{Authorization="Bearer invalid_token"} -ExpectedStatus 401
Write-Host "  Invalid Token (should be 401): $($invalidTokenResult.Status)`n"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TEST RESULTS SUMMARY" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$results | Format-Table -AutoSize

$passCount = ($results | Where-Object { $_.Result -eq "PASS" }).Count
$failCount = ($results | Where-Object { $_.Result -eq "FAIL" }).Count
$total = $results.Count

Write-Host "Total: $total | Pass: $passCount | Fail: $failCount`n" -ForegroundColor $(if ($failCount -eq 0) { "Green" } else { "Yellow" })

# Save results
$results | Export-Csv -Path "api_test_results.csv" -NoTypeInformation
Write-Host "Results saved to: api_test_results.csv"

# Return summary
@{
    TestUserId = $testUserId
    TestOrderId = $testOrderId
    TestEmail = $testEmail
    PassCount = $passCount
    FailCount = $failCount
    Total = $total
}
