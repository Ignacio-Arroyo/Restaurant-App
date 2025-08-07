#!/bin/bash

echo "Starting Restaurante Application..."
echo

# Start all containers
docker-compose up -d

echo
echo "Waiting for services to be ready..."
sleep 10

# Check status
echo
echo "Service Status:"
docker-compose ps

echo
echo "Application URLs:"
echo "Frontend: http://localhost:3000"
echo "Backend API: http://localhost:8080"
echo "Health Check: http://localhost:8080/actuator/health"
echo "PostgreSQL: localhost:5432"
echo "pgAdmin: http://localhost:8081"
echo "  Email: admin@restaurant.com"
echo "  Password: admin123"
echo

echo "To stop the application, run: ./stop-app.sh"
echo "Press any key to continue..."
read -n 1 -s
