INSERT IGNORE INTO product (item, normal_price) VALUES ('A', 40);
INSERT IGNORE INTO product (item, normal_price) VALUES ('B',10);
INSERT IGNORE INTO product (item, normal_price) VALUES ('C', 30);
INSERT IGNORE INTO product (item, normal_price) VALUES ('D', 25);
INSERT IGNORE INTO special_price (product_item, required_quantity, special_price) VALUES ('A', 3, 30);
INSERT IGNORE INTO special_price (product_item, required_quantity, special_price) VALUES ('B', 2, 7.5);
INSERT IGNORE INTO special_price (product_item, required_quantity, special_price) VALUES ('C', 4, 20);
INSERT IGNORE INTO special_price (product_item, required_quantity, special_price) VALUES ('D', 2, 23.5);