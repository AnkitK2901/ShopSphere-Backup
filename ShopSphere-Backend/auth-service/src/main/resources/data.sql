-- Passwords are set to 'password123', hashed with BCrypt for Spring Security
INSERT IGNORE INTO users (user_id, user_full_name, user_email, user_password, user_role, user_name, user_address, user_gender) 
VALUES (1, 'Admin User', 'admin@shopsphere.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'ROLE_ADMIN', 'admin', 'Admin HQ', 'Other');

INSERT IGNORE INTO users (user_id, user_full_name, user_email, user_password, user_role, user_name, user_address, user_gender) 
VALUES (2, 'Artisan Seller', 'seller@shopsphere.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'ROLE_ARTISAN', 'artisan_demo', 'Artisan Street', 'Female');

INSERT IGNORE INTO users (user_id, user_full_name, user_email, user_password, user_role, user_name, user_address, user_gender) 
VALUES (3, 'Logistics Staff', 'warehouse@shopsphere.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'ROLE_LOGISTICS', 'warehouse_staff', 'Warehouse Dock 4', 'Male');

INSERT IGNORE INTO users (user_id, user_full_name, user_email, user_password, user_role, user_name, user_address, user_gender) 
VALUES (4, 'John Shopper', 'customer@shopsphere.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'ROLE_CUSTOMER', 'john_doe', '123 Customer Ave', 'Male');