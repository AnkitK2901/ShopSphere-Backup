-- Use 'shipment' because Hibernate creates 'shipment'
INSERT IGNORE INTO shipment (shipment_id, order_id, status, tracking_number, carrier) 
VALUES ('1', '101', 'DELIVERED', 'TRACK123456', 'FedEx');

INSERT IGNORE INTO shipment (shipment_id, order_id, status, tracking_number, carrier) 
VALUES ('2', '102', 'IN_TRANSIT', 'TRACK789012', 'UPS');