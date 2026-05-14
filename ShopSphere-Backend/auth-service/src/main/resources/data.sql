-- FIX: Changed user_name to match the email address exactly. Password is still 'password123'
INSERT IGNORE INTO users (user_id, user_full_name, user_email, user_password, user_role, user_name, user_address, user_gender) VALUES 
(1, 'Admin User', 'admin@shopsphere.com', '$2a$10$f2e9J7OlrT/euiYnYMg2OOpc4zqBWDmDZklyditvA5L3bFDFh.NEG', 'ROLE_ADMIN', 'admin', 'Headquarters', 'Other'),
(2, 'Artisan Seller', 'seller@shopsphere.com', '$2a$10$f2e9J7OlrT/euiYnYMg2OOpc4zqBWDmDZklyditvA5L3bFDFh.NEG', 'ROLE_ARTISAN', 'seller', 'Artisan Village', 'Female'),
(3, 'Logistics Lead', 'warehouse@shopsphere.com', '$2a$10$f2e9J7OlrT/euiYnYMg2OOpc4zqBWDmDZklyditvA5L3bFDFh.NEG', 'ROLE_LOGISTICS', 'wareh', 'Warehouse Dock 1', 'Male'),
(4, 'Ankit Kumar', 'customer@shopsphere.com', '$2a$10$f2e9J7OlrT/euiYnYMg2OOpc4zqBWDmDZklyditvA5L3bFDFh.NEG', 'ROLE_CUSTOMER', 'buy', 'Mumbai, India', 'Male');