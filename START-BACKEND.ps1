Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "  STARTING GROCERY APP BACKEND SERVER" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

# Set environment variables
$env:MONGODB_URI = "mongodb+srv://smartgrocery:grocery123@cluster0.b3gdydm.mongodb.net/?appName=Cluster0"
$env:JWT_SECRET = "your-secret-jwt-key-min-256-bits-for-HS256-algorithm-production-ready"

Write-Host "[1/3] Environment variables set" -ForegroundColor Green

Write-Host "[2/3] Setting up USB Port Forwarding (ADB Reverse)..." -ForegroundColor Yellow
$adbPath = "C:\Users\bil_l\AppData\Local\Android\Sdk\platform-tools\adb.exe"
if (Test-Path $adbPath) {
    & $adbPath reverse tcp:8080 tcp:8080
    Write-Host "      SUCCESS: Port 8080 forwarded over USB." -ForegroundColor Green
} else {
    Write-Host "      WARNING: adb.exe not found at $adbPath" -ForegroundColor Red
}

Write-Host "[3/3] Starting backend server..." -ForegroundColor Yellow

$javaPath = "C:\Program Files\Android\Android Studio\jbr\bin\java.exe"
$jarPath = "D:\Grocerey App\backend\build\libs\backend-all.jar"

Write-Host ""
Write-Host "IMPORTANT: Keep this window open and your USB cable connected!" -ForegroundColor Red
Write-Host "The backend is now available on your phone via USB at: http://127.0.0.1:8080" -ForegroundColor Cyan
Write-Host ""

# Run using the correct Java version
& $javaPath -jar $jarPath
