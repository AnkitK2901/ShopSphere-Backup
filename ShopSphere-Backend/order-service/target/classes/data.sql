-- 1. MASTER ORDERS (Keep these IDs as they are referenced by details)
INSERT IGNORE INTO orders (order_id, customer_id, total_amount, status, created_at, updated_at) VALUES 
(101, 4, 4500.0, 'DELIVERED', '2026-05-01 10:00:00', '2026-05-03 14:00:00'),
(102, 4, 1200.0, 'SHIPPED', '2026-05-05 11:30:00', '2026-05-06 09:00:00'),
(103, 4, 850.0, 'CONFIRMED', NOW(), NOW());

-- 2. ORDER DETAILS (FIXED: order_item_id removed to allow auto-generation)
INSERT IGNORE INTO order_items (order_id, product_id, quantity, price) VALUES 
(101, '1', 1, 1500.0), 
(101, '2', 1, 3000.0), -- Links to Order 101
(102, '3', 1, 1200.0), -- Links to Order 102
(103, '7', 1, 850.0);  -- Links to Order 103

-- 3. CUSTOMIZATIONS (Keep these linked to the master order_id)
INSERT IGNORE INTO order_customizations (order_id, custom_detail) VALUES 
(101, 'Premium Hoodie [Size: Large (L)]'), 
(101, 'Luxury Wallet [Material: Genuine Leather]'),
(102, 'Organic Face Serum [Service: Basic 1-Year Warranty]');