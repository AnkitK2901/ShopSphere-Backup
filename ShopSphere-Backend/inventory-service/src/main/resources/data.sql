-- Use 'inventory' because Hibernate creates 'inventory'
INSERT IGNORE INTO inventory (product_id, stock_level, reorder_threshold, supplier_id, supplier_lead_time_days) 
VALUES ('1', 50, 10, 'SUPP-001', 3);

INSERT IGNORE INTO inventory (product_id, stock_level, reorder_threshold, supplier_id, supplier_lead_time_days) 
VALUES ('2', 30, 5, 'SUPP-002', 5);