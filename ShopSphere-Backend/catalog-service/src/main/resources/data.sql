-- MASTER CUSTOM OPTIONS (Dropdowns for Sellers)
INSERT IGNORE INTO custom_option (id, type, value, price_adjustment) VALUES 
(1, 'Color', 'Midnight Black', 0.0), (2, 'Color', 'Crimson Red', 150.0), (3, 'Color', 'Rose Gold', 450.0),
(4, 'Size', 'Small (S)', 0.0), (5, 'Size', 'Large (L)', 250.0), (6, 'Size', 'Extra Large (XL)', 400.0),
(7, 'Material', 'Organic Cotton', 0.0), (8, 'Material', 'Genuine Leather', 2500.0), (9, 'Material', 'Recycled Polyester', 0.0),
(10, 'Service', 'Basic 1-Year Warranty', 0.0), (11, 'Service', 'Extended 3-Year Warranty', 1500.0),
(12, 'Scent', 'Lavender & Vanilla', 50.0), (13, 'Scent', 'Sandalwood', 75.0), (14, 'Scent', 'Unscented', 0.0),
(15, 'Finish', 'Glossy', 0.0), (16, 'Finish', 'Matte Oak', 300.0), (17, 'Finish', 'Rustic Terracotta', 0.0);

-- 20 ARTISAN PRODUCTS (IMAGEKIT LINKS)
INSERT IGNORE INTO product (product_id, name, description, base_price, preview_image, is_active) VALUES 
(1, 'Premium Hoodie', 'Heavyweight organic cotton hoodie with a modern fit.', 1500.0, 'https://ik.imagekit.io/20wdsy6cl/images/photo-1556821840-3a63f95609a7_auto=format&fit=crop&w=600&q=80', 1),
(2, 'Luxury Wallet', 'Hand-stitched genuine leather wallet with RFID protection.', 3000.0, 'https://ik.imagekit.io/20wdsy6cl/images/photo-1627123424574-724758594e93_auto=format&fit=crop&w=600&q=80', 1),
(3, 'Organic Face Serum', 'Hydrating serum with Vitamin C and natural extracts.', 1200.0, 'https://ik.imagekit.io/20wdsy6cl/images/photo-1608571423902-eed4a5ad8108_auto=format&fit=crop&w=600&q=80', 1),
(4, 'Ceramic Vase', 'Hand-thrown minimalist vase for modern homes.', 2200.0, 'https://ik.imagekit.io/20wdsy6cl/images/photo-1578749556568-bc2c40e68b61_auto=format&fit=crop&w=600&q=80', 1),
(5, 'Leather Satchel', 'Briefcase made from vegetable-tanned leather.', 4500.0, 'https://ik.imagekit.io/20wdsy6cl/images/photo-1548036328-c9fa89d128fa_auto=format&fit=crop&w=600&q=80', 1),
(6, 'Smart Bamboo Watch', 'Eco-friendly wooden watch with Swiss movement.', 5500.0, 'https://ik.imagekit.io/20wdsy6cl/images/photo-1523275335684-37898b6baf30_auto=format&fit=crop&w=600&q=80', 1),
(7, 'Soy Wax Candle', 'Hand-poured candle in a reusable glass jar.', 850.0, 'https://ik.imagekit.io/20wdsy6cl/images/photo-1602874801007-bd458bb1b8b6_auto=format&fit=crop&w=600&q=80', 1),
(8, 'Linen Table Runner', 'Hand-woven linen runner for elegant dining.', 1100.0, 'https://ik.imagekit.io/20wdsy6cl/images/il_794xN.3035352488_es4u.jpg', 1),
(9, 'Copper Water Bottle', 'Hammered copper bottle for Ayurvedic health.', 1300.0, 'https://ik.imagekit.io/20wdsy6cl/images/81+hcH049WL._AC_UF350,350_QL80_.jpg', 1),
(10, 'Brass Incense Burner', 'Solid brass burner with ash catcher.', 1100.0, 'https://ik.imagekit.io/20wdsy6cl/images/IMG_4135.jpgcv_-scaled-1.jpg', 1),
(11, 'Silk Necktie', 'Hand-painted silk tie with unique patterns.', 2100.0, 'https://ik.imagekit.io/20wdsy6cl/images/TTH-COMSET-553-1-800x800(1).jpg', 1),
(12, 'Wooden Coasters', 'Set of 4 walnut wood coasters with resin inlay.', 750.0, 'https://ik.imagekit.io/20wdsy6cl/images/41mn1TwTM-L._SY300_SX300_QL70_FMwebp_.webp', 1),
(13, 'Beaded Necklace', 'Ethically sourced semi-precious stone necklace.', 3200.0, 'https://ik.imagekit.io/20wdsy6cl/images/photo-1515562141207-7a88fb7ce338_auto=format&fit=crop&w=600&q=80', 1),
(14, 'Denim Jacket', 'Custom distressed denim jacket with patches.', 2800.0, 'https://ik.imagekit.io/20wdsy6cl/images/photo-1551537482-f2075a1d41f2_auto=format&fit=crop&w=600&q=80', 1),
(15, 'Terracotta Planter', 'Breathable clay pot for indoor succulents.', 950.0, 'https://ik.imagekit.io/20wdsy6cl/images/photo-1485955900006-10f4d324d411_auto=format&fit=crop&w=600&q=80', 1),
(16, 'Sterling Silver Ring', 'Hand-forged textured silver band.', 2500.0, 'https://ik.imagekit.io/20wdsy6cl/images/photo-1485955900006-10f4d324d411_auto=format&fit=crop&w=600&q=80', 1),
(17, 'Glass Jewelry Box', 'Brass-framed geometric glass storage box.', 1750.0, 'https://ik.imagekit.io/20wdsy6cl/images/product-jpeg-1000x1000.png', 1),
(18, 'Bamboo Phone Stand', 'Adjustable desktop stand for all smartphones.', 450.0, 'https://ik.imagekit.io/20wdsy6cl/images/Phone-Stand(1).jpg', 1),
(19, 'Handmade Soap Bar', 'Cold-process soap with activated charcoal.', 350.0, 'https://ik.imagekit.io/20wdsy6cl/images/Red-Wine-Soap(1).png', 1),
(20, 'Ceramic Coffee Mug', 'Large 15oz mug with reactive glaze.', 650.0, 'https://ik.imagekit.io/20wdsy6cl/images/photo-1514228742587-6b1558fcca3d_auto=format&fit=crop&w=600&q=80', 1);
-- STITCHING PRODUCTS TO OPTIONS (Logically Corrected)
INSERT IGNORE INTO product_selected_options (product_id, option_id) VALUES 
-- Premium Hoodie (Colors, Sizes, Cotton)
(1, 1), (1, 2), (1, 4), (1, 5), (1, 6), (1, 7), 
-- Wallet & Satchel (Black, Leather)
(2, 1), (2, 8), (5, 1), (5, 8), 
-- Face Serum & Soap (Scents)
(3, 12), (3, 14), (19, 12), (19, 13), 
-- Ceramic Vase (Finishes)
(4, 15), (4, 17), 
-- Bamboo Watch (Warranties)
(6, 10), (6, 11), 
-- Candle (Scents)
(7, 12), (7, 13), (7, 14), 
-- Denim Jacket (Sizes, Recycled Poly)
(14, 4), (14, 5), (14, 6), (14, 9), 
-- Sterling Silver Ring (Rose Gold option)
(16, 3), 
-- Bamboo Phone Stand (Wood Finishes)
(18, 15), (18, 16);