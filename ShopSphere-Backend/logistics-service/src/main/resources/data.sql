-- SHIPMENT TRACKING (Linked via order_id)
INSERT IGNORE INTO shipment (shipment_id, order_id, status, tracking_number, carrier) VALUES 
('1', '101', 'DELIVERED', 'SHOP-TRK-990112', 'FedEx'),
('2', '102', 'IN_TRANSIT', 'SHOP-TRK-445200', 'Delhivery'),
('3', '103', 'CREATED', NULL, 'Pending');