-- 删除数据库
DROP DATABASE IF EXISTS super_mall;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS super_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE super_mall;

-- 认证相关表
CREATE TABLE `auth_users` (
    `auth_id` INT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `email` VARCHAR(100),
    `password_hash` VARCHAR(100) NOT NULL,
    `role_id` INT,
    `status` VARCHAR(20),
    `last_login` DATETIME,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `roles` (
    `role_id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `code` VARCHAR(50) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `permissions` (
    `permission_id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `code` VARCHAR(50) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `role_permissions` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `role_id` INT NOT NULL,
    `permission_id` INT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户相关表
CREATE TABLE `user_profiles` (
    `user_id` INT NOT NULL AUTO_INCREMENT,
    `auth_id` INT NOT NULL,
    `full_name` VARCHAR(50),
    `phone_number` VARCHAR(20),
    `avatar_url` VARCHAR(255),
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_addresses` (
    `address_id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `receiver_name` VARCHAR(50) NOT NULL,
    `receiver_phone` VARCHAR(20) NOT NULL,
    `province` VARCHAR(50) NOT NULL,
    `city` VARCHAR(50) NOT NULL,
    `district` VARCHAR(50) NOT NULL,
    `street` VARCHAR(200) NOT NULL,
    `postal_code` VARCHAR(10),
    `is_default` TINYINT(1) NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商家相关表
CREATE TABLE `merchant_profiles` (
    `merchant_id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `logo_url` VARCHAR(255),
    `description` TEXT,
    `status` VARCHAR(20) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品相关表
CREATE TABLE `categories` (
    `category_id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `parent_id` INT,
    `level` INT NOT NULL,
    `sort` INT DEFAULT 0,
    `status` VARCHAR(20) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `products` (
    `product_id` INT NOT NULL AUTO_INCREMENT,
    `merchant_id` INT NOT NULL,
    `category_id` INT NOT NULL,
    `name` VARCHAR(200) NOT NULL,
    `description` TEXT,
    `price` DECIMAL(10,2) NOT NULL,
    `original_price` DECIMAL(10,2),
    `stock` INT NOT NULL DEFAULT 0,
    `sales` INT NOT NULL DEFAULT 0,
    `main_image` VARCHAR(255),
    `status` VARCHAR(20) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `product_images` (
    `image_id` INT NOT NULL AUTO_INCREMENT,
    `product_id` INT NOT NULL,
    `url` VARCHAR(255) NOT NULL,
    `sort` INT DEFAULT 0,
    `is_main` TINYINT(1) NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`image_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `product_specs` (
    `spec_id` INT NOT NULL AUTO_INCREMENT,
    `product_id` INT NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `value` VARCHAR(50) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`spec_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `stock_movements` (
    `movement_id` INT NOT NULL AUTO_INCREMENT,
    `product_id` INT NOT NULL,
    `type` VARCHAR(20) NOT NULL,
    `quantity` INT NOT NULL,
    `reason` VARCHAR(200),
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`movement_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 购物车
CREATE TABLE `shopping_cart_items` (
    `item_id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `product_id` INT NOT NULL,
    `quantity` INT NOT NULL,
    `selected` TINYINT(1) NOT NULL DEFAULT 1,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单相关表
CREATE TABLE `orders` (
    `order_id` INT NOT NULL AUTO_INCREMENT,
    `order_no` VARCHAR(32) NOT NULL,
    `user_id` INT NOT NULL,
    `total_amount` DECIMAL(10,2) NOT NULL,
    `payment_amount` DECIMAL(10,2) NOT NULL,
    `shipping_fee` DECIMAL(10,2),
    `status` VARCHAR(20) NOT NULL,
    `payment_time` DATETIME,
    `shipping_time` DATETIME,
    `completion_time` DATETIME,
    `address_snapshot` TEXT,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `order_items` (
    `item_id` INT NOT NULL AUTO_INCREMENT,
    `order_id` INT NOT NULL,
    `product_id` INT NOT NULL,
    `product_snapshot` TEXT NOT NULL,
    `quantity` INT NOT NULL,
    `price` DECIMAL(10,2) NOT NULL,
    `total_amount` DECIMAL(10,2) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `return_orders` (
    `return_id` INT NOT NULL AUTO_INCREMENT,
    `order_id` INT NOT NULL,
    `user_id` INT NOT NULL,
    `reason` VARCHAR(500) NOT NULL,
    `status` VARCHAR(20) NOT NULL,
    `amount` DECIMAL(10,2) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`return_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 支付表
CREATE TABLE `payments` (
    `payment_id` INT NOT NULL AUTO_INCREMENT,
    `order_id` INT NOT NULL,
    `payment_no` VARCHAR(64) NOT NULL,
    `user_id` INT NOT NULL,
    `amount` DECIMAL(10,2) NOT NULL,
    `payment_method` VARCHAR(20) NOT NULL,
    `status` VARCHAR(20) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`payment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品评价表
CREATE TABLE `product_reviews` (
    `review_id` INT NOT NULL AUTO_INCREMENT,
    `order_id` INT NOT NULL,
    `product_id` INT NOT NULL,
    `user_id` INT NOT NULL,
    `content` TEXT NOT NULL,
    `rating` INT NOT NULL,
    `images` TEXT,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 收藏表
CREATE TABLE `user_favorites` (
    `favorite_id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `product_id` INT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`favorite_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 通知表
CREATE TABLE `notifications` (
    `notification_id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `content` TEXT NOT NULL,
    `type` VARCHAR(20) NOT NULL,
    `is_read` TINYINT(1) NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`notification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 系统日志表
CREATE TABLE `system_logs` (
    `log_id` INT NOT NULL AUTO_INCREMENT,
    `module` VARCHAR(100) NOT NULL,
    `action` VARCHAR(100) NOT NULL,
    `detail` TEXT,
    `ip_address` VARCHAR(50),
    `user_agent` VARCHAR(255),
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 发票表
CREATE TABLE `invoice` (
    `invoice_id` INT NOT NULL AUTO_INCREMENT,
    `order_id` INT NOT NULL,
    `user_id` INT NOT NULL,
    `type` VARCHAR(20) NOT NULL,
    `title_type` VARCHAR(20) NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `tax_number` VARCHAR(20),
    `amount` DECIMAL(10,2) NOT NULL,
    `content` VARCHAR(200) NOT NULL,
    `status` VARCHAR(20) NOT NULL,
    `company_address` VARCHAR(200),
    `company_phone` VARCHAR(20),
    `bank_name` VARCHAR(100),
    `bank_account` VARCHAR(30),
    `invoice_number` VARCHAR(50),
    `issued_time` DATETIME,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `remark` VARCHAR(500),
    PRIMARY KEY (`invoice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;