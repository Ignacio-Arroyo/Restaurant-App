-- Agregar campos para información del cliente y empleado en órdenes
ALTER TABLE orders 
ADD COLUMN customer_first_name VARCHAR(255),
ADD COLUMN customer_last_name VARCHAR(255),
ADD COLUMN customer_phone VARCHAR(20),
ADD COLUMN employee_id VARCHAR(255),
ADD COLUMN employee_name VARCHAR(255),
ADD COLUMN employee_role VARCHAR(50);
