# Restaurant App Launch Guide

## 🚨 Current Issues & Solutions

### Issue: Docker Network Timeout
You're experiencing Docker registry connection timeouts. Here are multiple solutions:

---

## 🔧 Solution 1: Fix Docker Network (Recommended)

### Step 1: Reset Docker Network Settings
```bash
# Stop any running containers
docker stop $(docker ps -aq) 2>/dev/null || true

# Remove all containers and networks
docker system prune -af --volumes

# Restart Docker Desktop (Windows)
# Close Docker Desktop completely and restart it
```

### Step 2: Configure DNS for Docker (if behind firewall/proxy)
Create or edit `~/.docker/daemon.json`:
```json
{
  "dns": ["8.8.8.8", "8.8.4.4", "1.1.1.1"],
  "registry-mirrors": ["https://mirror.gcr.io"]
}
```

Then restart Docker Desktop.

### Step 3: Try Starting Again
```bash
./start-app.sh
```

---

## 🔧 Solution 2: Development Mode (Quick Start)

### Prerequisites
1. **Install PostgreSQL** (if not installed):
   ```bash
   # Ubuntu/WSL
   sudo apt update
   sudo apt install postgresql postgresql-contrib
   
   # Start PostgreSQL service
   sudo service postgresql start
   
   # Set postgres user password
   sudo -u postgres psql -c "ALTER USER postgres PASSWORD 'password';"
   
   # Create database
   sudo -u postgres createdb restaurante_db
   ```

2. **Install Node.js** (if not installed):
   ```bash
   curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
   sudo apt-get install -y nodejs
   ```

3. **Install Java 17** (if not installed):
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk
   export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
   ```

### Start Backend (Terminal 1)
```bash
cd backend
./mvnw spring-boot:run
```

### Start Frontend (Terminal 2)
```bash
cd frontend
npm install
npm start
```

### Access Application
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health

---

## 🔧 Solution 3: Docker with Local Registry

If external registry access is blocked, pull images manually:

```bash
# Pull images when you have internet access
docker pull postgres:15-alpine
docker pull dpage/pgadmin4:latest

# Then try starting the app
./start-app.sh
```

---

## 🔧 Solution 4: Use Alternative Docker Images

Edit `docker-compose.yml` to use alternative registry:

```yaml
services:
  postgres:
    image: registry.cn-hangzhou.aliyuncs.com/library/postgres:15-alpine
    # ... rest of config
```

---

## 🐛 Troubleshooting

### Backend Won't Start
1. **Database Connection Error**:
   - Ensure PostgreSQL is running: `pg_isready -h localhost -p 5432`
   - Check credentials in `application-dev.properties`
   - Create database: `createdb -U postgres restaurante_db`

2. **Port Already in Use**:
   ```bash
   # Find what's using port 8080
   lsof -i :8080
   # Kill the process or change port in application.properties
   ```

3. **Java Version Issues**:
   ```bash
   java -version  # Should be Java 17+
   echo $JAVA_HOME
   ```

### Frontend Won't Start
1. **Dependencies Issues**:
   ```bash
   cd frontend
   rm -rf node_modules package-lock.json
   npm install
   ```

2. **Port 3000 in Use**:
   ```bash
   # Frontend will automatically use next available port
   # Or set custom port: PORT=3001 npm start
   ```

### Docker Issues
1. **Permission Denied**:
   ```bash
   sudo usermod -aG docker $USER
   # Logout and login again
   ```

2. **Docker Desktop Not Running**:
   - Ensure Docker Desktop is running on Windows
   - Check: `docker info`

---

## 🎯 Quick Start Commands

### Option A: Docker (Full Environment)
```bash
# Fix Docker networking first, then:
./start-app.sh
```

### Option B: Development Mode
```bash
# Terminal 1 - Backend
cd backend && ./mvnw spring-boot:run

# Terminal 2 - Frontend  
cd frontend && npm install && npm start
```

### Option C: Manual PostgreSQL + Development
```bash
# Setup Database
sudo service postgresql start
sudo -u postgres createdb restaurante_db

# Start Backend
cd backend && ./mvnw spring-boot:run

# Start Frontend
cd frontend && npm start
```

---

## 📱 Default Access

Once running, you can access:
- **Application**: http://localhost:3000
- **Default Admin**:
  - Email: admin@restaurant.com  
  - Password: admin123

---

## ❓ Still Having Issues?

1. Check logs in `backend/server.log`
2. Verify Java version: `java -version` (needs 17+)
3. Verify Node version: `node -v` (needs 16+)
4. Check if ports are free: `netstat -tlnp | grep ":8080\|:3000"`

Choose the solution that works best for your environment!
