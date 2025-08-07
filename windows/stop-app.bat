@echo off
echo Stopping Restaurante Application...
echo.

:: Stop all containers
docker-compose down

echo.
echo All services stopped.
pause
