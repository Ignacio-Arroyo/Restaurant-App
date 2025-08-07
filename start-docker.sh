#!/bin/bash

# Restaurant App - Docker Launch Script

echo "🍽️  Starting Restaurant Management System with Docker..."

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if docker-compose is available
if ! command -v docker-compose >/dev/null 2>&1; then
    echo "❌ docker-compose is not installed. Please install docker-compose and try again."
    exit 1
fi

echo "📦 Building and starting all services..."

# Build and start all services
docker-compose up --build -d

echo "⏳ Waiting for services to be healthy..."

# Wait for services to be ready
while [ "$(docker-compose ps -q postgres | xargs docker inspect -f '{{.State.Health.Status}}')" != "healthy" ]; do
    echo "   Waiting for PostgreSQL..."
    sleep 2
done

while [ "$(docker-compose ps -q backend | xargs docker inspect -f '{{.State.Health.Status}}')" != "healthy" ]; do
    echo "   Waiting for Backend..."
    sleep 2
done

echo "✅ All services are running!"
echo ""
echo "🌐 Application URLs:"
echo "   Frontend:  http://localhost:3000"
echo "   Backend:   http://localhost:8080"
echo "   Database:  localhost:5432"
echo ""
echo "👤 Default Admin Account:"
echo "   Email:     admin@restaurant.com"
echo "   Password:  admin123"
echo ""
echo "📊 Optional: Start pgAdmin for database management:"
echo "   docker-compose --profile pgadmin up -d"
echo "   pgAdmin:   http://localhost:8081"
echo ""
echo "🛑 To stop all services:"
echo "   docker-compose down"
echo ""
echo "📝 To view logs:"
echo "   docker-compose logs -f [service-name]"
