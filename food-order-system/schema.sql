-- GourmetFlow: Unified MySQL Schema Initialization Script
-- Run this script in your MySQL client to set up all microservice databases and tables.

-- ======================================================================
-- 1. ORDER SERVICE DATABASE
-- ======================================================================
CREATE DATABASE IF NOT EXISTS food_order_db;
USE food_order_db;

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_name VARCHAR(100) NOT NULL,
    item VARCHAR(100) NOT NULL,
    delivery_address VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Indexes for performance
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_customer ON orders(customer_name);

-- ======================================================================
-- 2. PAYMENT SERVICE DATABASE
-- ======================================================================
CREATE DATABASE IF NOT EXISTS food_payment_db;
USE food_payment_db;

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    transaction_id VARCHAR(50) NOT NULL UNIQUE,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Indexes for performance
CREATE INDEX idx_payments_order_id ON payments(order_id);

-- ======================================================================
-- 3. KITCHEN SERVICE DATABASE
-- ======================================================================
CREATE DATABASE IF NOT EXISTS food_kitchen_db;
USE food_kitchen_db;

CREATE TABLE IF NOT EXISTS kitchen_tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    ticket_number VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    items_detail VARCHAR(500) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Indexes for performance
CREATE INDEX idx_kitchen_tickets_order_id ON kitchen_tickets(order_id);

-- ======================================================================
-- 4. DELIVERY SERVICE DATABASE
-- ======================================================================
CREATE DATABASE IF NOT EXISTS food_delivery_db;
USE food_delivery_db;

CREATE TABLE IF NOT EXISTS deliveries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    courier_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    delivery_address VARCHAR(255) NOT NULL,
    delivered_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Indexes for performance
CREATE INDEX idx_deliveries_order_id ON deliveries(order_id);
