-- 删除数据库
DROP DATABASE IF EXISTS super_mall;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS super_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE super_mall;

-- 认证相关表
CREATE TABLE `auth_users` (
    `auth_id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `email` VARCHAR(100),
    `password_hash` VARCHAR(100) NOT NULL,
    `role_id` BIGINT,
    `status` ENUM('ACTIVE', 'INACTIVE', 'LOCKED') NOT NULL DEFAULT 'ACTIVE',
    `last_login` DATETIME,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `roles` (
    `role_id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(200),
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `permissions` (
    `permission_id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(200),
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `role_permissions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_id` BIGINT NOT NULL,
    `permission_id` BIGINT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户相关表
CREATE TABLE `user_profiles` (
    `user_id` BIGINT NOT NULL AUTO_INCREMENT,
    `auth_id` BIGINT NOT NULL,
    `full_name` VARCHAR(50),
    `phone_number` VARCHAR(20),
    `avatar_url` VARCHAR(255),
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_addresses` (
    `address_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `receiver_name` VARCHAR(50) NOT NULL,
    `receiver_phone` VARCHAR(20) NOT NULL,
    `province` VARCHAR(50) NOT NULL,
    `city` VARCHAR(50) NOT NULL,
    `district` VARCHAR(50) NOT NULL,
    `street` VARCHAR(200) NOT NULL,
    `postal_code` VARCHAR(10),
    `is_default` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商家相关表
CREATE TABLE `merchant_profiles` (
    `merchant_id` BIGINT NOT NULL AUTO_INCREMENT,
    `auth_id` BIGINT NOT NULL,
    `shop_name` VARCHAR(100) NOT NULL,
    `shop_description` TEXT,
    `business_license` VARCHAR(255),
    `contact_phone` VARCHAR(20),
    `contact_email` VARCHAR(100),
    `status` ENUM('PENDING', 'APPROVED', 'REJECTED', 'SUSPENDED') NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品相关表
CREATE TABLE `categories` (
    `category_id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `parent_id` BIGINT,
    `level` INT NOT NULL,
    `sort` INT DEFAULT 0,
    `status` ENUM('ENABLED', 'DISABLED') NOT NULL DEFAULT 'ENABLED',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `products` (
    `product_id` BIGINT NOT NULL AUTO_INCREMENT,
    `merchant_id` BIGINT NOT NULL,
    `category_id` BIGINT NOT NULL,
    `name` VARCHAR(200) NOT NULL,
    `description` TEXT,
    `price` DECIMAL(10,2) NOT NULL,
    `original_price` DECIMAL(10,2),
    `stock` INT NOT NULL DEFAULT 0,
    `sales` INT NOT NULL DEFAULT 0,
    `main_image` VARCHAR(255),
    `status` TINYINT NOT NULL COMMENT '0:草稿,1:待审核,2:已拒绝,3:已通过,4:在售,5:下架',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `product_images` (
    `image_id` BIGINT NOT NULL AUTO_INCREMENT,
    `product_id` BIGINT NOT NULL,
    `image_url` VARCHAR(255) NOT NULL,
    `sort_order` INT DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`image_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `product_specs` (
    `spec_id` BIGINT NOT NULL AUTO_INCREMENT,
    `product_id` BIGINT NOT NULL,
    `spec_name` VARCHAR(50) NOT NULL,
    `spec_value` VARCHAR(50) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`spec_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `stock_movements` (
    `movement_id` BIGINT NOT NULL AUTO_INCREMENT,
    `product_id` BIGINT NOT NULL,
    `type` ENUM('IN_STOCK', 'OUT_STOCK', 'RETURN', 'ADJUSTMENT') NOT NULL,
    `quantity` INT NOT NULL,
    `reason` VARCHAR(200),
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`movement_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 购物车
CREATE TABLE `shopping_cart_items` (
    `item_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL,
    `selected` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单相关表
CREATE TABLE `orders` (
    `order_id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_no` VARCHAR(32) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `total_amount` DECIMAL(10,2) NOT NULL,
    `payment_amount` DECIMAL(10,2) NOT NULL,
    `shipping_fee` DECIMAL(10,2),
    `status` ENUM('PENDING_PAYMENT', 'PAID', 'SHIPPED', 'DELIVERED', 'COMPLETED', 'CANCELLED', 'REFUNDED') NOT NULL,
    `payment_time` DATETIME,
    `shipping_time` DATETIME,
    `completion_time` DATETIME,
    `address_snapshot` TEXT,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `order_items` (
    `item_id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `merchant_id` BIGINT NOT NULL,
    `product_snapshot` TEXT NOT NULL,
    `quantity` INT NOT NULL,
    `price` DECIMAL(10,2) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `return_orders` (
    `return_id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `order_item_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `merchant_id` BIGINT NOT NULL,
    `return_amount` DECIMAL(10,2) NOT NULL,
    `status` ENUM('PENDING', 'APPROVED', 'REJECTED', 'RETURNED', 'REFUNDED') NOT NULL,
    `reason_type` ENUM('QUALITY_ISSUE', 'WRONG_ITEM', 'NOT_SATISFIED', 'OTHER') NOT NULL,
    `reason_detail` VARCHAR(500),
    `handling_notes` VARCHAR(500),
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`return_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 支付表
CREATE TABLE `payments` (
    `payment_id` BIGINT NOT NULL AUTO_INCREMENT,
    `payment_no` VARCHAR(64) NOT NULL,
    `order_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `amount` DECIMAL(10,2) NOT NULL,
    `payment_method` ENUM('ALIPAY', 'WECHAT', 'BANK_CARD', 'BALANCE') NOT NULL,
    `transaction_id` VARCHAR(64),
    `status` ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') NOT NULL,
    `failure_reason` VARCHAR(200),
    `callback_content` TEXT,
    `paid_at` DATETIME,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`payment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品评价表
CREATE TABLE `product_reviews` (
    `review_id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `order_item_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `merchant_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `content` TEXT NOT NULL,
    `rating` INT NOT NULL,
    `images` TEXT,
    `status` ENUM('PENDING', 'PUBLISHED', 'HIDDEN') NOT NULL DEFAULT 'PENDING',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 收藏表
CREATE TABLE `user_favorites` (
    `favorite_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`favorite_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 通知表
CREATE TABLE `notifications` (
    `notification_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `content` TEXT NOT NULL,
    `type` ENUM('ORDER_STATUS', 'RETURN_STATUS', 'SYSTEM') NOT NULL,
    `reference_id` BIGINT,
    `is_read` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`notification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 系统日志表
CREATE TABLE `system_logs` (
    `log_id` BIGINT NOT NULL AUTO_INCREMENT,
    `auth_id` BIGINT,
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
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `type` INT NOT NULL,
    `title_type` INT NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `tax_number` VARCHAR(20),
    `amount` DECIMAL(10,2) NOT NULL,
    `content` VARCHAR(200) NOT NULL,
    `status` INT NOT NULL,
    `company_address` VARCHAR(200),
    `company_phone` VARCHAR(20),
    `bank_name` VARCHAR(100),
    `bank_account` VARCHAR(30),
    `invoice_number` VARCHAR(50),
    `issued_time` DATETIME,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `remark` VARCHAR(500),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始化角色数据
INSERT INTO `roles` (`name`, `description`) VALUES
('ROLE_ADMIN', '系统管理员'),
('ROLE_MERCHANT', '商家'),
('ROLE_USER', '普通用户');

-- 初始化权限数据
INSERT INTO `permissions` (`name`, `description`) VALUES
-- 用户权限
('USER_INFO_READ', '查看用户信息'),
('USER_INFO_WRITE', '修改用户信息'),
-- 商品权限
('PRODUCT_READ', '查看商品'),
('PRODUCT_WRITE', '管理商品'),
-- 订单权限
('ORDER_READ', '查看订单'),
('ORDER_WRITE', '管理订单'),
-- 系统权限
('SYSTEM_MANAGE', '系统管理');

-- 初始化角色-权限关联
INSERT INTO `role_permissions` (`role_id`, `permission_id`) 
SELECT r.role_id, p.permission_id
FROM roles r, permissions p
WHERE r.name = 'ROLE_ADMIN';

INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT r.role_id, p.permission_id
FROM roles r, permissions p
WHERE r.name = 'ROLE_MERCHANT' 
AND p.name IN ('PRODUCT_READ', 'PRODUCT_WRITE', 'ORDER_READ', 'ORDER_WRITE');

INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT r.role_id, p.permission_id
FROM roles r, permissions p
WHERE r.name = 'ROLE_USER'
AND p.name IN ('USER_INFO_READ', 'USER_INFO_WRITE', 'PRODUCT_READ', 'ORDER_READ');

-- 初始化管理员账号
INSERT INTO `auth_users` (`username`, `email`, `password_hash`, `role_id`, `status`)
SELECT 'admin', 'admin@supermall.com', 
'$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8', -- 密码为 'admin123'
r.role_id, 'ACTIVE'
FROM roles r
WHERE r.name = 'ROLE_ADMIN';

INSERT INTO `user_profiles` (`auth_id`, `full_name`, `phone_number`)
SELECT au.auth_id, '系统管理员', '13800000000'
FROM auth_users au
WHERE au.username = 'admin';