-- --- EXISTING OPTIONS (RE-VERIFIED) ---
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

-- --- EXPANDED PRODUCT LIST (8 PRODUCTS) ---
-- 1. Premium Hoodie (Fashion)
INSERT IGNORE INTO product (product_id, name, base_price, preview_image, is_active) VALUES (1, 'Premium Hoodie', 1500.0, 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=500', 1);
-- 2. Luxury Wallet (Accessories)
INSERT IGNORE INTO product (product_id, name, base_price, preview_image, is_active) VALUES (2, 'Luxury Wallet', 3000.0, 'https://images.unsplash.com/photo-1627123424574-724758594e93?w=500', 1);
-- 3. Organic Skincare Serum (Beauty)
INSERT IGNORE INTO product (product_id, name, base_price, preview_image, is_active) VALUES (3, 'Organic Face Serum', 1200.0, 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=500', 1);
-- 4. Minimalist Ceramic Vase (Home Decor)
INSERT IGNORE INTO product (product_id, name, base_price, preview_image, is_active) VALUES (4, 'Hand-Thrown Ceramic Vase', 2200.0, 'https://images.unsplash.com/photo-1581783898377-1c85bf937427?w=500', 1);
-- 5. Leather Satchel Bag (Bags)
INSERT IGNORE INTO product (product_id, name, base_price, preview_image, is_active) VALUES (5, 'Vintage Leather Satchel', 4500.0, 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=500', 1);
-- 6. Artisan Tea Set (Kitchen)
INSERT IGNORE INTO product (product_id, name, base_price, preview_image, is_active) VALUES (6, 'Stone Artisan Tea Set', 3500.0, 'https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=500', 1);
-- 7. Botanical Scented Candle (Wellness)
INSERT IGNORE INTO product (product_id, name, base_price, preview_image, is_active) VALUES (7, 'Lavender & Sage Candle', 800.0, 'https://images.unsplash.com/photo-1601493700631-2b1644ad4c15?w=500', 1);
-- 8. Solid Oak Coasters (Gift)
INSERT IGNORE INTO product (product_id, name, base_price, preview_image, is_active) VALUES (8, 'Hand-Carved Oak Coasters', 600.0, 'https://images.unsplash.com/photo-1610701596007-11502861dcfa?w=500', 1);

-- --- LINKING PRODUCTS TO OPTIONS ---
-- Product 1: Red + Large
INSERT IGNORE INTO product_selected_options (product_id, option_id) VALUES (1, 2), (1, 5);
-- Product 2: Black + Leather
INSERT IGNORE INTO product_selected_options (product_id, option_id) VALUES (2, 1), (2, 8);
-- Product 3: Warranty options
INSERT IGNORE INTO product_selected_options (product_id, option_id) VALUES (3, 10), (3, 11);
-- Product 5: Black + Leather + Warranty
INSERT IGNORE INTO product_selected_options (product_id, option_id) VALUES (5, 1), (5, 8), (5, 11);