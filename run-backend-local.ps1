# Local Backend Runner
# Uses application-local.conf for MongoDB and JWT configuration

Write-Host "Starting backend with local configuration..." -ForegroundColor Cyan

./gradlew :backend:run --args="-config=application-local.conf"
