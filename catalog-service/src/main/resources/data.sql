-- Master Options: Colors
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (1, 'Color', 'Midnight Black', 0.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (2, 'Color', 'Crimson Red', 150.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (3, 'Color', 'Rose Gold', 450.0);

-- Master Options: Sizes
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (4, 'Size', 'Small (S)', 0.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (5, 'Size', 'Large (L)', 250.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (6, 'Size', 'Extra Large (XL)', 400.0);

-- Master Options: Materials
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (7, 'Material', 'Standard Cotton', 0.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (8, 'Material', 'Genuine Leather', 2500.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (9, 'Material', 'Carbon Fiber', 5000.0);

-- Master Options: Services/Warranty
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (10, 'Service', 'Basic 1-Year Warranty', 0.0);
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES (11, 'Service', 'Extended 3-Year Warranty', 1500.0);