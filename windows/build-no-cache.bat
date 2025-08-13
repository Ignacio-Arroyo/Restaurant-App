@echo off
:: Restaurant App - Build Without Cache Script (Windows)
:: This script will clean all caches and rebuild the entire project from scratch

setlocal enabledelayedexpansion

echo 🍽️  Restaurant App - Build Without Cache
echo ========================================
echo.

:: Check if Docker is running
echo 📋 Checking Docker status...
docker info >nul 2>&1
if !errorlevel! neq 0 (
    echo ❌ Docker is not running. Please start Docker and try again.
    pause
    exit /b 1
)
echo ✅ Docker is running
echo.

:: Stop all running containers
echo 📋 Stopping all running containers...
docker-compose down
echo ✅ Containers stopped
echo.

:: Clean Docker system
echo 📋 Cleaning Docker system (images, containers, networks, build cache)...
docker system prune -f
docker image prune -a -f
echo ✅ Docker system cleaned
echo.

:: Clean Maven cache
echo 📋 Cleaning Maven cache...
if exist "backend" (
    cd backend
    call mvnw.cmd clean
    cd ..
    echo ✅ Maven cache cleaned
) else (
    echo ⚠️  Backend directory not found, skipping Maven clean
)
echo.

:: Clean Node.js cache
echo 📋 Cleaning Node.js cache...
if exist "frontend" (
    cd frontend
    if exist "node_modules" rmdir /s /q node_modules
    if exist "build" rmdir /s /q build
    if exist "dist" rmdir /s /q dist
    if exist ".next" rmdir /s /q .next
    cd ..
    echo ✅ Node.js cache cleaned
) else (
    echo ⚠️  Frontend directory not found, skipping Node.js clean
)
echo.

:: Clean Docker volumes
echo 📋 Cleaning Docker volumes...
docker volume prune -f
echo ✅ Docker volumes cleaned
echo.

echo 📋 Building project without cache...
echo This may take several minutes as all dependencies will be downloaded fresh...
echo.

:: Build all services without cache
docker-compose build --no-cache

if !errorlevel! equ 0 (
    echo.
    echo ✅ Build completed successfully!
    echo.
    echo 📋 Starting all services...
    docker-compose up -d
    
    if !errorlevel! equ 0 (
        echo.
        echo 🎉 Restaurant App is now running!
        echo.
        echo 📱 Frontend: http://localhost:3000
        echo 🔧 Backend API: http://localhost:8080
        echo 🗃️  pgAdmin: http://localhost:8081
        echo.
        echo To view logs: docker-compose logs -f
        echo To stop: docker-compose down
        echo.
        pause
    ) else (
        echo ❌ Failed to start services
        pause
        exit /b 1
    )
) else (
    echo ❌ Build failed!
    pause
    exit /b 1
)

pause
