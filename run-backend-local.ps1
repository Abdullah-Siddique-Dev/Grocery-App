# Local Backend Runner
# Uses application-local.conf for MongoDB and JWT configuration

Write-Host "Starting backend with local configuration..." -ForegroundColor Cyan

Set-Location $PSScriptRoot
./gradlew :backend:run --args="-config=application-local.conf"
