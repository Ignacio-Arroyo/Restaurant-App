-- Insertar items básicos de inventario para el restaurante
INSERT INTO inventory_items (name, description, current_stock, minimum_stock, unit, cost_per_unit, category, supplier, active, created_at, updated_at) VALUES
-- Vegetales
('Tomate', 'Tomates frescos para pizzas y ensaladas', 50.00, 10.00, 'kg', 2.50, 'VEGETABLES', 'Proveedor Verduras SA', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Cebolla', 'Cebollas frescas para cocinar', 30.00, 5.00, 'kg', 1.80, 'VEGETABLES', 'Proveedor Verduras SA', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Lechuga', 'Lechuga fresca para ensaladas', 20.00, 3.00, 'kg', 2.20, 'VEGETABLES', 'Proveedor Verduras SA', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Champiñones', 'Champiñones frescos para pizzas', 15.00, 2.00, 'kg', 4.50, 'VEGETABLES', 'Proveedor Verduras SA', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Carnes
('Carne de Res', 'Carne de res premium para hamburguesas', 25.00, 5.00, 'kg', 12.00, 'MEAT', 'Carnicería El Buen Corte', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Pollo', 'Pechugas de pollo frescas', 30.00, 5.00, 'kg', 8.50, 'MEAT', 'Carnicería El Buen Corte', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jamón', 'Jamón de pavo para pizzas', 10.00, 2.00, 'kg', 15.00, 'MEAT', 'Embutidos Deliciosos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Pepperoni', 'Pepperoni para pizzas', 8.00, 1.00, 'kg', 18.00, 'MEAT', 'Embutidos Deliciosos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Lácteos
('Queso Mozzarella', 'Queso mozzarella para pizzas', 20.00, 3.00, 'kg', 6.80, 'DAIRY', 'Lácteos San José', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Queso Cheddar', 'Queso cheddar para hamburguesas', 15.00, 2.00, 'kg', 7.50, 'DAIRY', 'Lácteos San José', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Crema', 'Crema para salsas', 10.00, 2.00, 'litros', 3.20, 'DAIRY', 'Lácteos San José', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Granos y harinas
('Harina de Trigo', 'Harina para masa de pizza', 100.00, 20.00, 'kg', 1.50, 'GRAINS', 'Molinos del Valle', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Arroz', 'Arroz blanco premium', 50.00, 10.00, 'kg', 1.80, 'GRAINS', 'Granos del Campo', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Condimentos y especias
('Sal', 'Sal refinada para cocinar', 10.00, 2.00, 'kg', 0.80, 'SPICES', 'Especias del Mundo', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Pimienta Negra', 'Pimienta negra molida', 2.00, 0.50, 'kg', 8.00, 'SPICES', 'Especias del Mundo', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Orégano', 'Orégano seco para pizzas', 1.00, 0.20, 'kg', 15.00, 'SPICES', 'Especias del Mundo', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Ajo en Polvo', 'Ajo en polvo para condimentar', 1.50, 0.30, 'kg', 12.00, 'SPICES', 'Especias del Mundo', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Aceites
('Aceite de Oliva', 'Aceite de oliva extra virgen', 20.00, 3.00, 'litros', 8.50, 'OILS', 'Aceites Premium', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Aceite Vegetal', 'Aceite vegetal para freír', 30.00, 5.00, 'litros', 3.20, 'OILS', 'Aceites Premium', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Bebidas (ingredientes)
('Agua Mineral', 'Agua mineral embotellada', 100.00, 20.00, 'litros', 0.80, 'BEVERAGES', 'Bebidas Naturales', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Concentrado de Naranja', 'Concentrado para jugos naturales', 15.00, 3.00, 'litros', 4.50, 'BEVERAGES', 'Bebidas Naturales', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Panadería
('Pan para Hamburguesa', 'Pan de hamburguesa artesanal', 50.00, 10.00, 'unidades', 1.20, 'BAKERY', 'Panadería La Esquina', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Pan Baguette', 'Pan baguette fresco', 20.00, 5.00, 'unidades', 2.50, 'BAKERY', 'Panadería La Esquina', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
