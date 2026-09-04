# Backend Configuration Guide

## Required Configuration

The backend requires the following configuration to run:

1. **MongoDB Connection URI**
2. **JWT Secret Key**

## Configuration Methods

### Option 1: Local Configuration File (Recommended for Development)

1. Create `backend/src/main/resources/application-local.conf` with:

```hocon
include "application.conf"

mongodb {
    uri = "mongodb+srv://smartgrocery:grocery123@cluster0.b3gdydm.mongodb.net/?appName=Cluster0"
    database = "grocery_db"
}

jwt {
    secret = "your-secret-jwt-key-min-256-bits-for-HS256-algorithm-production-ready"
    issuer = "http://localhost:8080"
    audience = "http://localhost:8080/grocery"
}
```

2. Run the backend using:

```bash
./gradlew :backend:run --args="-config=application-local.conf"
```

Or use the convenience script:

```bash
./run-backend-local.ps1
```

**Note:** `application-local.conf` is gitignored and should NOT be committed.

### Option 2: Environment Variables (Recommended for Production)

Set the following environment variables:

**Windows PowerShell:**
```powershell
$env:MONGODB_URI = "mongodb+srv://smartgrocery:grocery123@cluster0.b3gdydm.mongodb.net/?appName=Cluster0"
$env:JWT_SECRET = "your-secret-jwt-key-min-256-bits-for-HS256-algorithm-production-ready"
./gradlew :backend:run
```

**Linux/Mac:**
```bash
export MONGODB_URI="mongodb+srv://smartgrocery:grocery123@cluster0.b3gdydm.mongodb.net/?appName=Cluster0"
export JWT_SECRET="your-secret-jwt-key-min-256-bits-for-HS256-algorithm-production-ready"
./gradlew :backend:run
```

## MongoDB Connection String Format

```
mongodb+srv://<username>:<password>@<cluster-url>/?appName=<app-name>
```

Your MongoDB Atlas connection:
- **Cluster:** cluster0.b3gdydm.mongodb.net
- **Database:** grocery_db
- **Username:** smartgrocery
- **Password:** <REDACTED>

## JWT Configuration

The JWT secret must be:
- At least 32 characters (256 bits) for HS256 algorithm
- Randomly generated for production
- Never committed to version control

## Security Notes

⚠️ **NEVER commit credentials to git:**
- `application-local.conf` is in `.gitignore`
- Use environment variables in production
- Rotate credentials regularly
- Use different credentials for dev/staging/production

## Building

To build without running:

```bash
./gradlew :backend:build -x test
```

## Testing Connection

Start the backend and check the logs for:
- ✅ "Configuration validation passed"
- ✅ "MongoDB connection successful"
- ✅ "Application started successfully"

The API will be available at: `http://localhost:8080`
