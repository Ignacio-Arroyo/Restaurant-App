# Build Scripts

This directory contains scripts to build the Restaurant App project without using any cache.

## 🐧 Linux/macOS/WSL

Use the bash script:

```bash
./build-no-cache.sh
```

## 🪟 Windows

Use the batch script:

```cmd
build-no-cache.bat
```

## What these scripts do:

1. **Check Docker status** - Ensures Docker is running
2. **Stop containers** - Stops any running Restaurant App containers
3. **Clean Docker system** - Removes all images, containers, networks, and build cache
4. **Clean Maven cache** - Cleans backend Java build cache
5. **Clean Node.js cache** - Removes frontend node_modules and build directories
6. **Clean Docker volumes** - Removes any persistent data volumes
7. **Build without cache** - Rebuilds all Docker images from scratch
8. **Start services** - Starts all services after successful build

## After running:

The application will be available at:
- 📱 **Frontend**: http://localhost:3000
- 🔧 **Backend API**: http://localhost:8080  
- 🗃️ **pgAdmin**: http://localhost:8081

## Useful commands after build:

```bash
# View real-time logs
docker-compose logs -f

# Stop all services
docker-compose down

# Restart services
docker-compose up -d

# View running containers
docker-compose ps
```

## Troubleshooting:

- **Docker not running**: Start Docker Desktop and wait for it to be ready
- **Permission denied (Linux/macOS)**: Run `chmod +x build-no-cache.sh`
- **Build fails**: Check that you have enough disk space and good internet connection
- **Services don't start**: Check logs with `docker-compose logs`

## Build time:

- First run: 5-10 minutes (downloads all dependencies)
- Subsequent runs: 3-5 minutes (some layers may be cached)

The scripts will show colored output to indicate progress and any issues.
