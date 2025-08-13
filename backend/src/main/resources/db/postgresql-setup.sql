-- PostgreSQL Database Setup Script for Restaurant App

-- Create database (run this as postgres superuser)
-- CREATE DATABASE restaurante_db;

-- Create schema and tables will be handled by Hibernate
-- This file contains sample data and useful SQL commands

-- Sample data will be inserted by DataInitializer.java
-- You can also manually insert data using these SQL commands:

-- Insert sample admin user (password is BCrypt encoded for 'admin123')
-- INSERT INTO users (first_name, last_name, email, password, role, created_at) 
-- VALUES ('Admin', 'User', 'admin@restaurant.com', '$2a$10$YourBCryptHashHere', 'ADMIN', NOW());

-- Add image_url columns to existing tables (commented out - will be handled by Hibernate JPA)
-- ALTER TABLE meals 
-- ADD COLUMN IF NOT EXISTS image_url VARCHAR(500);

-- ALTER TABLE drinks 
-- ADD COLUMN IF NOT EXISTS image_url VARCHAR(500);

-- Insert sample meals if they don't exist
-- INSERT INTO meals (name, description, ingredients, allergens, cost, type) VALUES
-- ('Margherita Pizza', 'Classic Italian pizza with tomato sauce, mozzarella, and fresh basil', 'Tomato sauce, mozzarella cheese, fresh basil, olive oil', 'Gluten, Dairy', 12.99, 'VEGETARIAN'),
-- ('Grilled Chicken Breast', 'Tender grilled chicken breast with herbs and spices', 'Chicken breast, herbs, spices, olive oil', 'None', 16.99, 'MEAT'),
-- ('Vegan Buddha Bowl', 'Nutritious bowl with quinoa, vegetables, and tahini dressing', 'Quinoa, mixed vegetables, tahini, lemon, herbs', 'Sesame', 14.99, 'VEGAN'),
-- ('Organic Salmon', 'Fresh organic salmon with seasonal vegetables', 'Organic salmon, seasonal vegetables, herbs', 'Fish', 22.99, 'BIO');

-- Insert sample drinks
-- INSERT INTO drinks (name, ingredients, cost, type) VALUES
-- ('Coca Cola', 'Classic cola soft drink', 3.99, 'SODA'),
-- ('Fresh Orange Juice', 'Freshly squeezed orange juice', 5.99, 'NATURAL'),
-- ('House Wine', 'Red house wine selection', 8.99, 'ALCOHOL'),
-- ('Sparkling Water', 'Premium sparkling water', 2.99, 'NATURAL');

-- Insert sample products
-- INSERT INTO products (name, description, cost, quantity, unit) VALUES
-- ('Tomatoes', 'Fresh organic tomatoes', 3.50, 50, 'kg'),
-- ('Chicken Breast', 'Fresh chicken breast', 8.99, 25, 'kg'),
-- ('Mozzarella Cheese', 'Fresh mozzarella cheese', 12.99, 15, 'kg'),
-- ('Quinoa', 'Organic quinoa', 6.99, 20, 'kg');

-- Sales tables will be created by Hibernate JPA
-- Table: sales
-- - id (BIGSERIAL PRIMARY KEY)
-- - customer_name (VARCHAR NOT NULL)
-- - total_amount (DECIMAL NOT NULL)
-- - sale_date (TIMESTAMP NOT NULL)

-- Table: sale_items
-- - id (BIGSERIAL PRIMARY KEY)
-- - sale_id (BIGINT NOT NULL REFERENCES sales(id))
-- - product_name (VARCHAR NOT NULL)
-- - product_type (VARCHAR NOT NULL) -- 'MEAL' or 'DRINK'
-- - quantity (INTEGER NOT NULL)
-- - unit_price (DECIMAL NOT NULL)
-- - total_price (DECIMAL NOT NULL)

-- Useful queries for monitoring

-- Check all users
-- SELECT id, first_name, last_name, email, role, created_at FROM users;

-- Check all orders with user info
-- SELECT o.id as order_id, u.first_name, u.last_name, o.total_cost, o.order_type, o.table_number, o.status, o.order_date
-- FROM orders o JOIN users u ON o.user_id = u.id
-- ORDER BY o.order_date DESC;

-- Check order items
-- SELECT o.id as order_id, m.name as meal_name, om.quantity, m.cost
-- FROM orders o 
-- JOIN order_meals om ON o.id = om.order_id 
-- JOIN meals m ON om.meal_id = m.id;

-- Check inventory
-- SELECT * FROM products ORDER BY name;

-- Sales queries
-- SELECT * FROM sales ORDER BY sale_date DESC;
-- SELECT s.id, s.customer_name, s.total_amount, s.sale_date, 
--        si.product_name, si.product_type, si.quantity, si.unit_price, si.total_price
-- FROM sales s LEFT JOIN sale_items si ON s.id = si.sale_id
-- ORDER BY s.sale_date DESC;

-- Sales statistics
-- SELECT product_name, product_type, SUM(quantity) as total_quantity, SUM(total_price) as total_revenue
-- FROM sale_items si JOIN sales s ON si.sale_id = s.id
-- WHERE s.sale_date >= '2023-01-01'
-- GROUP BY product_name, product_type
-- ORDER BY total_revenue DESC;

-- Coupons table will be created by Hibernate JPA
-- Table: coupons
-- - id (BIGSERIAL PRIMARY KEY)
-- - name (VARCHAR NOT NULL) - Nombre descriptivo del cupón
-- - code (VARCHAR(50) NOT NULL UNIQUE) - Código único del cupón (mínimo 10 caracteres)
-- - active (BOOLEAN NOT NULL DEFAULT TRUE) - Si el cupón está activo
-- - discount_type (VARCHAR NOT NULL) - Tipo de descuento: 'PERCENTAGE' o 'FIXED'
-- - discount_value (DECIMAL(10,2) NOT NULL) - Valor del descuento (porcentaje o cantidad fija)
-- - minimum_purchase (DECIMAL(10,2)) - Compra mínima requerida (opcional)
-- - created_at (TIMESTAMP NOT NULL) - Fecha de creación
-- - updated_at (TIMESTAMP NOT NULL) - Fecha de última actualización

-- Insert sample coupons (examples - will be handled by DataInitializer.java)
-- INSERT INTO coupons (name, code, active, discount_type, discount_value, minimum_purchase, created_at, updated_at) VALUES
-- ('Descuento Bienvenida', 'WELCOME2024', true, 'PERCENTAGE', 10.00, 5000.00, NOW(), NOW()),
-- ('Oferta Especial', 'SPECIAL50OFF', true, 'FIXED', 2500.00, 10000.00, NOW(), NOW()),
-- ('Descuento VIP', 'VIP20PERCENT', true, 'PERCENTAGE', 20.00, 15000.00, NOW(), NOW()),
-- ('Prueba Inactivo', 'INACTIVE001', false, 'PERCENTAGE', 5.00, null, NOW(), NOW());

-- Coupon queries
-- Validar cupón
-- SELECT * FROM coupons WHERE code = 'WELCOME2024' AND active = true;

-- Ver todos los cupones activos
-- SELECT * FROM coupons WHERE active = true ORDER BY created_at DESC;

-- Estadísticas de cupones
-- SELECT 
--   COUNT(*) as total_coupons,
--   COUNT(CASE WHEN active = true THEN 1 END) as active_coupons,
--   COUNT(CASE WHEN active = false THEN 1 END) as inactive_coupons
-- FROM coupons;
