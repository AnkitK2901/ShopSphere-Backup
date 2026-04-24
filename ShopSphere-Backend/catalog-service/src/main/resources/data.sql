
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (1, 'Color', 'Midnight Black', 0.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (2, 'Color', 'Crimson Red', 150.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (3, 'Color', 'Rose Gold', 450.0);


INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (4, 'Size', 'Small (S)', 0.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (5, 'Size', 'Large (L)', 250.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (6, 'Size', 'Extra Large (XL)', 400.0);


INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (7, 'Material', 'Standard Cotton', 0.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (8, 'Material', 'Genuine Leather', 2500.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (9, 'Material', 'Carbon Fiber', 5000.0);


INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (10, 'Service', 'Basic 1-Year Warranty', 0.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (11, 'Service', 'Extended 3-Year Warranty', 1500.0);


INSERT IGNORE INTO product (product_id, name, base_price, preview_image, is_active) VALUES (1, 'Premium Hoodie', 1500.0, 'hoodie_thumb.jpg', 1);
INSERT IGNORE INTO product (product_id, name, base_price, preview_image, is_active) VALUES (2, 'Luxury Wallet', 3000.0, 'wallet_thumb.jpg', 1 );


INSERT IGNORE INTO product_selected_options (product_id, option_id) VALUES (1, 2);
INSERT IGNORE INTO product_selected_options (product_id, option_id) VALUES (1, 5);


INSERT IGNORE INTO product_selected_options (product_id, option_id) VALUES (2, 1);
INSERT IGNORE INTO product_selected_options (product_id, option_id) VALUES (2, 8);