-- Вставляем категории
INSERT INTO categories (name, description) VALUES ('Электроника', 'Ноутбуки, планшеты, смартфоны и наушники');
INSERT INTO categories (name, description) VALUES ('Бытовая техника', 'Техника для дома');

-- Вставляем продукты в категорию "Электроника" (categoryId = 1)
INSERT INTO products (name_en, name_ru, characteristics, weight, size, expiry_date, stock_quantity, category_id) VALUES ('Laptop', 'Ноутбук', '15.6" IPS, 16GB RAM, 512GB SSD', 1.8, '36x25x2 cm', null, 50, 1);
INSERT INTO products (name_en, name_ru, characteristics, weight, size, expiry_date, stock_quantity, category_id) VALUES ('Tablet', 'Планшет', '10.1" IPS, 4GB RAM, 64GB storage', 0.5, '24x17x0.7 cm', null, 150, 1);
INSERT INTO products (name_en, name_ru, characteristics, weight, size, expiry_date, stock_quantity, category_id) VALUES ('Smartphone', 'Смартфон', '6.7" OLED, 8GB RAM, 128GB storage', 0.2, '16x7.5x0.8 cm', null, 300, 1);
INSERT INTO products (name_en, name_ru, characteristics, weight, size, expiry_date, stock_quantity, category_id) VALUES ('Headphones', 'Наушники', 'Wireless, noise-cancelling', 0.25, '20x18x8 cm', null, 200, 1);

-- Вставляем продукты в категорию "Бытовая техника" (categoryId = 2)
INSERT INTO products (name_en, name_ru, characteristics, weight, size, expiry_date, stock_quantity, category_id) VALUES ('Fan', 'Вентилятор', '3-speed, oscillating', 3.5, '45x45x120 cm', null, 70, 2);
INSERT INTO products (name_en, name_ru, characteristics, weight, size, expiry_date, stock_quantity, category_id) VALUES ('Vacuum Cleaner', 'Пылесос', 'Bagless, 1800W', 5.0, '40x30x30 cm', null, 40, 2);
INSERT INTO products (name_en, name_ru, characteristics, weight, size, expiry_date, stock_quantity, category_id) VALUES ('Washing Machine', 'Стиральная машина', '7kg capacity, 1200 RPM', 65.0, '60x60x85 cm', null, 20, 2);
INSERT INTO products (name_en, name_ru, characteristics, weight, size, expiry_date, stock_quantity, category_id) VALUES ('Refrigerator', 'Холодильник', '250L capacity, No-Frost', 70.0, '60x65x180 cm', null, 1, 2);