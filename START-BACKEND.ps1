Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "  STARTING GROCERY APP BACKEND SERVER" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

# Set environment variables
$env:MONGODB_URI = "mongodb+srv://smartgrocery:grocery123@cluster0.b3gdydm.mongodb.net/?appName=Cluster0"
$env:JWT_SECRET = "your-secret-jwt-key-min-256-bits-for-HS256-algorithm-production-ready"

Write-Host "[1/3] Environment variables set" -ForegroundColor Green
Write-Host "[2/3] Starting backend server..." -ForegroundColor Yellow
Write-Host ""
Write-Host "IMPORTANT: Keep this window open!" -ForegroundColor Red
Write-Host "The backend will be available at: http://192.168.0.104:8080" -ForegroundColor Cyan
Write-Host ""
Write-Host "Waiting for server to start (this takes 1-2 minutes)..." -ForegroundColor Yellow
Write-Host ""

# Run backend
./gradlew :backend:run
