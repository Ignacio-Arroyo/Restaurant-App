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

-- Insert sample meals
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
