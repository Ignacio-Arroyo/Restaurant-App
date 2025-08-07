@echo off
REM Restaurant App - Docker Launch Script for Windows

echo 🍽️  Starting Restaurant Management System with Docker...

REM Check if Docker is running
docker info >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ❌ Docker is not running. Please start Docker and try again.
    exit /b 1
)

REM Check if docker-compose is available
docker-compose --version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ❌ docker-compose is not installed. Please install docker-compose and try again.
    exit /b 1
)

echo 📦 Building and starting all services...

REM Build and start all services
docker-compose up --build -d

echo ⏳ Waiting for services to be healthy...
timeout /t 30 /nobreak >nul

echo ✅ All services should be running!
echo.
echo 🌐 Application URLs:
echo    Frontend:  http://localhost:3000
echo    Backend:   http://localhost:8080
echo    Database:  localhost:5432
echo.
echo 👤 Default Admin Account:
echo    Email:     admin@restaurant.com
echo    Password:  admin123
echo.
echo 📊 Optional: Start pgAdmin for database management:
echo    docker-compose --profile pgadmin up -d
echo    pgAdmin:   http://localhost:8081
echo.
echo 🛑 To stop all services:
echo    docker-compose down
echo.
echo 📝 To view logs:
echo    docker-compose logs -f [service-name]

pause
