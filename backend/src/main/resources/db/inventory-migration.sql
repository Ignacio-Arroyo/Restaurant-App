-- Script de migración para sistema de inventario
-- Fecha: 2025-08-25

-- Crear tabla de items de inventario
CREATE TABLE IF NOT EXISTS inventory_items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    current_stock NUMERIC(10,2) NOT NULL DEFAULT 0,
    minimum_stock NUMERIC(10,2) NOT NULL DEFAULT 0,
    unit VARCHAR(20) NOT NULL,
    cost_per_unit NUMERIC(10,2),
    category VARCHAR(50) NOT NULL,
    supplier VARCHAR(100),
    expiration_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE
);

-- Crear tabla de relación entre platillos e inventario
CREATE TABLE IF NOT EXISTS meal_inventory (
    id BIGSERIAL PRIMARY KEY,
    meal_id BIGINT NOT NULL,
    inventory_item_id BIGINT NOT NULL,
    quantity_needed NUMERIC(10,2) NOT NULL,
    notes TEXT,
    FOREIGN KEY (meal_id) REFERENCES meals(id) ON DELETE CASCADE,
    FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id) ON DELETE CASCADE,
    UNIQUE(meal_id, inventory_item_id)
);

-- Crear tabla de relación entre bebidas e inventario
CREATE TABLE IF NOT EXISTS drink_inventory (
    id BIGSERIAL PRIMARY KEY,
    drink_id BIGINT NOT NULL,
    inventory_item_id BIGINT NOT NULL,
    quantity_needed NUMERIC(10,2) NOT NULL,
    notes TEXT,
    FOREIGN KEY (drink_id) REFERENCES drinks(id) ON DELETE CASCADE,
    FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id) ON DELETE CASCADE,
    UNIQUE(drink_id, inventory_item_id)
);

-- Crear tabla de promociones en órdenes
CREATE TABLE IF NOT EXISTS order_promotions (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    promotion_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price NUMERIC(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE CASCADE
);

-- Crear índices para mejorar performance
CREATE INDEX IF NOT EXISTS idx_inventory_items_category ON inventory_items(category);
CREATE INDEX IF NOT EXISTS idx_inventory_items_active ON inventory_items(active);
CREATE INDEX IF NOT EXISTS idx_inventory_items_low_stock ON inventory_items(current_stock, minimum_stock);
CREATE INDEX IF NOT EXISTS idx_meal_inventory_meal_id ON meal_inventory(meal_id);
CREATE INDEX IF NOT EXISTS idx_meal_inventory_inventory_item_id ON meal_inventory(inventory_item_id);
CREATE INDEX IF NOT EXISTS idx_drink_inventory_drink_id ON drink_inventory(drink_id);
CREATE INDEX IF NOT EXISTS idx_drink_inventory_inventory_item_id ON drink_inventory(inventory_item_id);
CREATE INDEX IF NOT EXISTS idx_order_promotions_order_id ON order_promotions(order_id);
CREATE INDEX IF NOT EXISTS idx_order_promotions_promotion_id ON order_promotions(promotion_id);

-- Insertar datos de ejemplo para inventario
INSERT INTO inventory_items (name, description, current_stock, minimum_stock, unit, cost_per_unit, category, supplier) VALUES
-- Verduras
('Tomates', 'Tomates frescos rojos', 50.00, 10.00, 'kg', 2.50, 'VEGETABLES', 'Mercado Central'),
('Lechugas', 'Lechugas frescas verdes', 25.00, 5.00, 'unidades', 1.20, 'VEGETABLES', 'Mercado Central'),
('Cebollas', 'Cebollas blancas', 30.00, 8.00, 'kg', 1.80, 'VEGETABLES', 'Mercado Central'),
('Zanahorias', 'Zanahorias frescas', 20.00, 5.00, 'kg', 2.00, 'VEGETABLES', 'Mercado Central'),
('Pimientos', 'Pimientos rojos y verdes', 15.00, 3.00, 'kg', 3.50, 'VEGETABLES', 'Mercado Central'),

-- Carnes
('Pollo', 'Pechugas de pollo frescas', 40.00, 10.00, 'kg', 8.50, 'MEAT', 'Carnicería López'),
('Carne de Res', 'Carne de res para guiso', 25.00, 5.00, 'kg', 12.00, 'MEAT', 'Carnicería López'),
('Cerdo', 'Lomo de cerdo', 20.00, 5.00, 'kg', 9.50, 'MEAT', 'Carnicería López'),

-- Lácteos
('Queso Mozzarella', 'Queso mozzarella para pizza', 10.00, 2.00, 'kg', 6.50, 'DAIRY', 'Lácteos San José'),
('Leche', 'Leche entera fresca', 30.00, 8.00, 'litros', 1.20, 'DAIRY', 'Lácteos San José'),
('Mantequilla', 'Mantequilla sin sal', 5.00, 1.00, 'kg', 4.50, 'DAIRY', 'Lácteos San José'),

-- Granos y cereales
('Arroz', 'Arroz blanco grano largo', 50.00, 10.00, 'kg', 1.50, 'GRAINS', 'Distribuidora Alba'),
('Pasta', 'Pasta italiana variada', 25.00, 5.00, 'kg', 2.20, 'GRAINS', 'Distribuidora Alba'),
('Harina', 'Harina de trigo', 20.00, 5.00, 'kg', 1.80, 'GRAINS', 'Distribuidora Alba'),

-- Especias y condimentos
('Sal', 'Sal marina', 10.00, 2.00, 'kg', 1.00, 'SPICES', 'Especias del Mundo'),
('Pimienta Negra', 'Pimienta negra molida', 2.00, 0.50, 'kg', 15.00, 'SPICES', 'Especias del Mundo'),
('Ajo en Polvo', 'Ajo deshidratado en polvo', 1.50, 0.30, 'kg', 8.50, 'SPICES', 'Especias del Mundo'),
('Orégano', 'Orégano seco', 1.00, 0.20, 'kg', 12.00, 'SPICES', 'Especias del Mundo'),

-- Bebidas
('Agua Mineral', 'Agua mineral natural', 100.00, 20.00, 'litros', 0.80, 'BEVERAGES', 'Bebidas Naturales'),
('Zumo de Naranja', 'Zumo de naranja natural', 50.00, 10.00, 'litros', 2.50, 'BEVERAGES', 'Bebidas Naturales'),
('Café en Grano', 'Café arábica premium', 10.00, 2.00, 'kg', 18.00, 'BEVERAGES', 'Café Premium'),
('Té Negro', 'Té negro Earl Grey', 2.00, 0.50, 'kg', 25.00, 'BEVERAGES', 'Té & Infusiones'),

-- Aceites
('Aceite de Oliva', 'Aceite de oliva extra virgen', 15.00, 3.00, 'litros', 8.50, 'OILS', 'Aceites Mediterráneos'),
('Aceite de Girasol', 'Aceite de girasol refinado', 20.00, 5.00, 'litros', 3.50, 'OILS', 'Aceites Mediterráneos')

ON CONFLICT (name) DO NOTHING;

-- Ejemplos de relaciones meal_inventory (recetas)
-- Nota: Estos se insertarían después de crear las relaciones en la aplicación
-- Los administradores pueden configurar qué ingredientes usa cada platillo
