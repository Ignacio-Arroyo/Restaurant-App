#!/bin/bash

echo "Stopping Restaurante Application..."
echo

# Stop all containers
docker-compose down

echo
echo "All services stopped."
echo "Press any key to continue..."
read -n 1 -s
