-- --- EXISTING OPTIONS ---
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (1, 'Color', 'Midnight Black', 0.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (2, 'Color', 'Crimson Red', 150.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (3, 'Color', 'Rose Gold', 450.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (4, 'Size', 'Small (S)', 0.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (5, 'Size', 'Large (L)', 250.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (6, 'Size', 'Extra Large (XL)', 400.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (7, 'Material', 'Standard Cotton', 0.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (8, 'Material', 'Genuine Leather', 2500.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (10, 'Service', 'Basic 1-Year Warranty', 0.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (11, 'Service', 'Extended 3-Year Warranty', 1500.0);

-- --- EXPANDED PRODUCT LIST WITH DESCRIPTIONS ---
INSERT IGNORE INTO product (product_id, name, description, base_price, preview_image, is_active) VALUES (1, 'Premium Hoodie', 'A warm, comfortable premium hoodie perfect for winter wear.', 1500.0, 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=500', 1);
INSERT IGNORE INTO product (product_id, name, description, base_price, preview_image, is_active) VALUES (2, 'Luxury Wallet', 'Genuine leather luxury wallet with RFID protection.', 3000.0, 'https://images.unsplash.com/photo-1627123424574-724758594e93?w=500', 1);
INSERT IGNORE INTO product (product_id, name, description, base_price, preview_image, is_active) VALUES (3, 'Organic Face Serum', 'Revitalizing organic skincare serum for daily glow.', 1200.0, 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=500', 1);
INSERT IGNORE INTO product (product_id, name, description, base_price, preview_image, is_active) VALUES (4, 'Hand-Thrown Ceramic Vase', 'Minimalist ceramic vase, perfect for modern home decor.', 2200.0, 'https://images.unsplash.com/photo-1581783898377-1c85bf937427?w=500', 1);
INSERT IGNORE INTO product (product_id, name, description, base_price, preview_image, is_active) VALUES (5, 'Vintage Leather Satchel', 'Durable leather satchel bag for daily office commute.', 4500.0, 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=500', 1);

-- --- LINKING PRODUCTS TO OPTIONS ---
INSERT IGNORE INTO product_selected_options (product_id, option_id) VALUES (1, 2), (1, 5);
INSERT IGNORE INTO product_selected_options (product_id, option_id) VALUES (2, 1), (2, 8);
INSERT IGNORE INTO product_selected_options (product_id, option_id) VALUES (3, 10), (3, 11);
INSERT IGNORE INTO product_selected_options (product_id, option_id) VALUES (5, 1), (5, 8), (5, 11);