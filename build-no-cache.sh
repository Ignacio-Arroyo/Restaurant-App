#!/bin/bash

# Restaurant App - Build Without Cache Script
# This script will clean all caches and rebuild the entire project from scratch

set -e  # Exit on any error

echo "🍽️  Restaurant App - Build Without Cache"
echo "========================================"
echo ""

# Function to print colored output
print_step() {
    echo -e "\033[1;34m📋 $1\033[0m"
}

print_success() {
    echo -e "\033[1;32m✅ $1\033[0m"
}

print_warning() {
    echo -e "\033[1;33m⚠️  $1\033[0m"
}

print_error() {
    echo -e "\033[1;31m❌ $1\033[0m"
}

# Check if Docker is running
print_step "Checking Docker status..."
if ! docker info >/dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi
print_success "Docker is running"

# Stop all running containers
print_step "Stopping all running containers..."
docker-compose down || true
print_success "Containers stopped"

# Clean Docker system
print_step "Cleaning Docker system (images, containers, networks, build cache)..."
docker system prune -f
docker image prune -a -f
print_success "Docker system cleaned"

# Clean Maven cache
print_step "Cleaning Maven cache..."
if [ -d "backend" ]; then
    cd backend
    ./mvnw clean || print_warning "Maven clean failed, continuing..."
    cd ..
    print_success "Maven cache cleaned"
else
    print_warning "Backend directory not found, skipping Maven clean"
fi

# Clean Node.js cache
print_step "Cleaning Node.js cache..."
if [ -d "frontend" ]; then
    cd frontend
    rm -rf node_modules build dist .next || true
    cd ..
    print_success "Node.js cache cleaned"
else
    print_warning "Frontend directory not found, skipping Node.js clean"
fi

# Remove Docker volumes (optional - uncomment if you want to clean database data too)
print_step "Cleaning Docker volumes..."
docker volume prune -f
print_success "Docker volumes cleaned"

echo ""
print_step "Building project without cache..."
echo "This may take several minutes as all dependencies will be downloaded fresh..."

# Build all services without cache
docker-compose build --no-cache

if [ $? -eq 0 ]; then
    print_success "Build completed successfully!"
    echo ""
    print_step "Starting all services..."
    docker-compose up -d
    
    if [ $? -eq 0 ]; then
        echo ""
        print_success "🎉 Restaurant App is now running!"
        echo ""
        echo "📱 Frontend: http://localhost:3000"
        echo "🔧 Backend API: http://localhost:8080"
        echo "🗃️  pgAdmin: http://localhost:8081"
        echo ""
        echo "To view logs: docker-compose logs -f"
        echo "To stop: docker-compose down"
    else
        print_error "Failed to start services"
        exit 1
    fi
else
    print_error "Build failed!"
    exit 1
fi
