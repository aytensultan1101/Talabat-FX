-- =====================================================================
--  Talabat FX - database schema and sample data
--
--  Usage:  mysql -u root -p < database/schema.sql
--
--  Safe to re-run: tables are only created if missing, and sample rows
--  use fixed IDs with INSERT IGNORE so they are never duplicated.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS talabat_db CHARACTER SET utf8mb4;
USE talabat_db;

-- ---------------------------------------------------------------------
--  People
-- ---------------------------------------------------------------------

-- Every person in the system. A new customer row is created on each checkout.
CREATE TABLE IF NOT EXISTS users (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(100) NOT NULL,
    email  VARCHAR(150) NOT NULL,
    role   ENUM('customer', 'driver') NOT NULL
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS customers (
    user_id  INT PRIMARY KEY,
    address  VARCHAR(255) NOT NULL,
    CONSTRAINT fk_customers_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS drivers (
    user_id  INT PRIMARY KEY,
    CONSTRAINT fk_drivers_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  Restaurants and menus
-- ---------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS restaurants (
    id    INT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100) NOT NULL UNIQUE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS menu_items (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id  INT NOT NULL,
    name           VARCHAR(100) NOT NULL,
    price          DECIMAL(8, 2) NOT NULL,
    CONSTRAINT fk_menu_items_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  Orders
-- ---------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS orders (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    customer_id  INT NOT NULL,
    driver_id    INT NOT NULL,
    order_date   DATE NOT NULL,
    total        DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customers (user_id),
    CONSTRAINT fk_orders_driver   FOREIGN KEY (driver_id)   REFERENCES drivers (user_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS order_items (
    order_id      INT NOT NULL,
    menu_item_id  INT NOT NULL,
    quantity      INT NOT NULL DEFAULT 1,
    PRIMARY KEY (order_id, menu_item_id),
    CONSTRAINT fk_order_items_order     FOREIGN KEY (order_id)     REFERENCES orders (id),
    CONSTRAINT fk_order_items_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_items (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS payments (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    order_id  INT NOT NULL UNIQUE,
    method    ENUM('Cash', 'Card') NOT NULL,
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders (id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  Sample data
--  Restaurant and menu item names are matched to the images in
--  src/resources/ by keyword (see getRestaurantImage / getMenuImage in MainApp).
-- ---------------------------------------------------------------------

INSERT IGNORE INTO restaurants (id, name) VALUES
    (1, 'Toto Fine Dining'),
    (2, 'Menna Fol & Falafel'),
    (3, 'Aya Fresh');

INSERT IGNORE INTO menu_items (id, restaurant_id, name, price) VALUES
    (1, 1, 'Burger',           150.00),
    (2, 1, 'Pizza',            180.00),
    (3, 1, 'Cola',              25.00),
    (4, 2, 'Falafel Sandwich',  15.00),
    (5, 2, 'Fol Sandwich',      12.00),
    (6, 2, 'Tea',               10.00),
    (7, 3, 'Fruit Salad',       60.00),
    (8, 3, 'Orange Juice',      35.00);

INSERT IGNORE INTO users (id, name, email, role) VALUES
    (1, 'Ahmed Hassan', 'ahmed.driver@example.com',  'driver'),
    (2, 'Mohamed Ali',  'mohamed.driver@example.com', 'driver'),
    (3, 'Omar Khaled',  'omar.driver@example.com',   'driver');

INSERT IGNORE INTO drivers (user_id) VALUES (1), (2), (3);
