# Restaurant Management System

Una aplicación web completa y modular para la gestión de restaurantes con seguimiento de inventario y funcionalidad de pedidos de clientes.

## 🎯 **Aplicación Modular**

**Esta aplicación está diseñada para ser fácilmente adaptable a diferentes restaurantes.** Toda la información específica del restaurante se puede personalizar desde archivos de configuración centralizados.

### 🚀 Configuración Rápida

```bash
# Usar el configurador automático
./configure-restaurant.sh    # Linux/Mac
configure-restaurant.bat     # Windows

# O configurar manualmente
cp .env.example .env
# Editar .env con tu información
```

### 📁 Archivos de Configuración

- `.env` - Configuración del backend (email, información del restaurante)
- `frontend/src/config/restaurant.config.ts` - Configuración del frontend
- Ver `RESTAURANT-CONFIG.md` para guía completa

## Features

### Customer Features
- **User Registration & Authentication**: Customers can create accounts with name, last name, email, and password
- **Menu Browsing**: View all meals and drinks with filtering by type (vegetarian, vegan, bio, meat for meals; soda, alcohol, natural for drinks)
- **Order Placement**: Add items to cart and place orders for either dine-in or takeaway
- **Order History**: View past orders and their status
- **Order Types**: 
  - **Dine-in**: Select table number
  - **Takeaway**: Order for pickup

### Admin Features
- **Inventory Management**: Add, edit, and delete products used in the kitchen
- **Product Search**: Find products by name
- **Stock Tracking**: Monitor quantity and costs of inventory items
- **Restaurant Configuration**: Panel de administración para personalizar información del restaurante

### Technical Features
- **JWT Authentication**: Secure user sessions
- **Role-based Access**: Customer and Admin roles
- **RESTful API**: Well-structured backend API
- **Responsive Design**: Works on desktop and mobile devices
- **Modular Configuration**: Easy adaptation for different restaurants

## Tech Stack

### Backend
- **Framework**: Spring Boot 3.5.4
- **Database**: PostgreSQL 15+
- **Security**: Spring Security with JWT
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven
- **Java Version**: 17

### Frontend
- **Framework**: React 18 with TypeScript
- **UI Library**: React Bootstrap
- **Routing**: React Router DOM
- **HTTP Client**: Axios
- **State Management**: React Context API

## Database Schema

### Entities

#### User
- ID, First Name, Last Name, Email, Password, Role, Created Date

#### Meal
- ID, Name, Description, Ingredients, Allergens, Cost, Type (Vegetarian/Vegan/Bio/Meat)

#### Drink  
- ID, Name, Ingredients, Cost, Type (Soda/Alcohol/Natural)

#### Product (Inventory)
- ID, Name, Description, Cost, Quantity, Unit

#### Order
- ID, User, Order Date, Total Cost, Order Type, Table Number, Status

#### Order Items
- Order-Meal and Order-Drink junction tables with quantities

## Getting Started

### Prerequisites
- Docker and Docker Compose (recommended)
- OR: Java 17+, Node.js 16+, Maven 3.6+, PostgreSQL 12+

## 🐳 **Quick Start with Docker (Recommended)**

### Option 1: Using Helper Scripts
```bash
# For Linux/Mac
chmod +x start-docker.sh
./start-docker.sh

# For Windows
start-docker.bat
```

### Option 2: Manual Docker Commands
```bash
# Start the entire application stack
docker-compose up --build -d

# Optional: Start pgAdmin for database management
docker-compose --profile pgadmin up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

**What this starts:**
- PostgreSQL database on port `5432`
- Spring Boot backend on port `8080`
- React frontend on port `3000`
- Optional pgAdmin on port `8081`

**Access the application:**
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **pgAdmin** (optional): http://localhost:8081

## 💻 **Manual Development Setup**

### Database Setup

#### Option 1: Using Docker (For Database Only)
```bash
# Start only PostgreSQL
docker-compose up postgres -d

# Optional: Start pgAdmin
docker-compose --profile pgadmin up -d
```

#### Option 2: Local PostgreSQL Installation
1. Install PostgreSQL 12 or higher
2. Create a database named `restaurante_db`:
```sql
CREATE DATABASE restaurante_db;
```
3. Update the database credentials in `backend/src/main/resources/application.properties` if needed

### Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Run the Spring Boot application:
```bash
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`

**Database Access:**
- PostgreSQL: `localhost:5432`
- Database: `restaurante_db`
- Username: `postgres`
- Password: `password`
- pgAdmin (if using Docker): `http://localhost:8081`

### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The frontend will start on `http://localhost:3000`

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Menu (Public)
- `GET /api/menu/meals` - Get all meals
- `GET /api/menu/meals/type/{type}` - Get meals by type
- `GET /api/menu/drinks` - Get all drinks
- `GET /api/menu/drinks/type/{type}` - Get drinks by type

### Orders (Authenticated)
- `POST /api/orders` - Create new order
- `GET /api/orders` - Get user's orders
- `GET /api/orders/{id}` - Get specific order

### Admin - Products (Admin Only)
- `GET /api/admin/products` - Get all products
- `POST /api/admin/products` - Create product
- `PUT /api/admin/products/{id}` - Update product
- `DELETE /api/admin/products/{id}` - Delete product
- `GET /api/admin/products/search?name={name}` - Search products

## Default Accounts

### Admin Account
- Email: `admin@restaurant.com`
- Password: `admin123`

### Customer Accounts
Create new accounts through the registration page.

## Sample Data

The application comes with sample data:
- **Meals**: Margherita Pizza, Grilled Chicken Breast, Vegan Buddha Bowl, Organic Salmon
- **Drinks**: Coca Cola, Fresh Orange Juice, House Wine, Sparkling Water
- **Products**: Tomatoes, Chicken Breast, Mozzarella Cheese, Quinoa

## Database Configuration

### Environment-Specific Configuration
The application supports multiple environments:

- **Development (`dev`)**: Uses `create-drop` schema generation for testing
- **Production (`prod`)**: Uses `validate` for schema validation only

### Configuration Files
- `application.properties` - Main configuration
- `application-dev.properties` - Development overrides
- `application-prod.properties` - Production overrides

### Environment Variables (Production)
```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/restaurante_db
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key
CORS_ORIGINS=https://yourdomain.com
```

## 🔧 **Docker Management Commands**

```bash
# Start all services
docker-compose up -d

# Start with rebuild
docker-compose up --build -d

# Start specific service
docker-compose up -d postgres

# View logs
docker-compose logs -f
docker-compose logs -f backend
docker-compose logs -f frontend

# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ will delete database data)
docker-compose down -v

# View running services
docker-compose ps

# Access backend container
docker-compose exec backend bash

# Access database
docker-compose exec postgres psql -U postgres -d restaurante_db
```

## 📱 **Application Usage**

### Application Flow
1. **Home Page**: Welcome page with restaurant information
2. **Registration/Login**: Users create accounts or sign in
3. **Menu**: Browse meals and drinks, add to cart
4. **Order**: Choose dine-in or takeaway, place order
5. **Order History**: View past orders and status
6. **Admin Panel**: Manage inventory (admin users only)

### Service Architecture
- **Frontend (React)**: User interface served by Nginx
- **Backend (Spring Boot)**: REST API with business logic
- **Database (PostgreSQL)**: Data persistence
- **pgAdmin**: Database management interface (optional)

## 🚀 **Production Deployment**

### Environment Variables
Create a `.env` file for production:
```bash
# Database
DATABASE_URL=jdbc:postgresql://your-db-host:5432/restaurante_db
DATABASE_USERNAME=your-username
DATABASE_PASSWORD=your-password

# Security
JWT_SECRET=your-super-secret-jwt-key-here
CORS_ORIGINS=https://yourdomain.com

# Ports
BACKEND_PORT=8080
FRONTEND_PORT=80
```

### Production Docker Compose
```bash
# Use production profile
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

## Development Notes

- The application uses PostgreSQL database
- CORS is configured to allow frontend-backend communication
- JWT tokens expire after 24 hours
- All passwords are encrypted using BCrypt
- The frontend includes responsive design for mobile devices
- Sample data is automatically inserted on first run

## Future Enhancements

- Payment integration
- Real-time order status updates
- Kitchen dashboard for order management
- Email notifications
- Advanced reporting and analytics
- Multi-restaurant support

## Important Commands

Connect to the database from the terminal 
- docker-compose exec postgres psql -U postgres -d restaurante_db

## TODO

- Add coupons functionnalities (CHECKED)
- Add the API functions to deactivate a worker and deleted
- Modify the currency used