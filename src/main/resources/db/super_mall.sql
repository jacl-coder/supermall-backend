-- 删除数据库
DROP DATABASE IF EXISTS super_mall;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS super_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE super_mall;

-- 1. 权限模块
-- 1.1 角色表
CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 1.2 权限表
CREATE TABLE permissions (
    permission_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 1.3 角色权限关联表
CREATE TABLE role_permissions (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE
);

-- 初始化角色数据
INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', '系统管理员'),
('ROLE_MERCHANT', '商家用户'),
('ROLE_USER', '普通用户');

-- 初始化权限数据
INSERT INTO permissions (name, description) VALUES
-- 系统权限
('system:manage', '系统管理权限'),
('user:manage', '用户管理权限'),
('merchant:manage', '商家管理权限'),
('category:manage', '分类管理权限'),

-- 商家权限
('product:manage', '商品管理权限'),
('order:manage', '订单管理权限'),
('shop:manage', '店铺管理权限'),

-- 用户权限
('profile:edit', '个人资料编辑'),
('order:create', '创建订单'),
('review:create', '创建评价');

-- 初始化角色权限关联
INSERT INTO role_permissions (role_id, permission_id) VALUES
-- 管理员权限
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7),
-- 商家权限
(2, 5), (2, 6), (2, 7),
-- 用户权限
(3, 8), (3, 9), (3, 10);

-- 1. 认证模块
-- 1.1 统一认证表
CREATE TABLE auth_users (
    auth_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    status ENUM('active', 'inactive', 'locked') DEFAULT 'active',
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- 1.2 用户信息表
CREATE TABLE user_profiles (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    auth_id INT UNIQUE,
    full_name VARCHAR(255),
    phone_number VARCHAR(20),
    avatar_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (auth_id) REFERENCES auth_users(auth_id) ON DELETE CASCADE,
    INDEX idx_auth_id (auth_id)
);

-- 1.3 商家信息表
CREATE TABLE merchant_profiles (
    merchant_id INT AUTO_INCREMENT PRIMARY KEY,
    auth_id INT UNIQUE,
    shop_name VARCHAR(255) NOT NULL,
    shop_description TEXT,
    business_license VARCHAR(255),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(255),
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (auth_id) REFERENCES auth_users(auth_id) ON DELETE CASCADE,
    INDEX idx_auth_id (auth_id),
    INDEX idx_shop_name (shop_name)
);

-- 1.4 用户地址表
CREATE TABLE user_addresses (
    address_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    receiver_name VARCHAR(255) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    province VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    district VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    postal_code VARCHAR(20),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
);

-- 2. 商品模块
-- 2.1 商品分类表
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    parent_id INT DEFAULT NULL,
    level INT NOT NULL DEFAULT 1,
    sort_order INT DEFAULT 0,
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES categories(category_id) ON DELETE CASCADE,
    INDEX idx_parent_id (parent_id),
    INDEX idx_level (level)
);

-- 2.2 商品表
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    merchant_id INT NOT NULL,
    category_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    original_price DECIMAL(10, 2),
    stock INT NOT NULL DEFAULT 0,
    sales INT NOT NULL DEFAULT 0,
    main_image VARCHAR(255),
    status ENUM('draft', 'pending', 'approved', 'rejected', 'on_sale', 'off_sale') DEFAULT 'draft',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchant_profiles(merchant_id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    FULLTEXT INDEX idx_product_search (name, description)
);

-- 2.3 商品图片表
CREATE TABLE product_images (
    image_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_product_id (product_id)
);

-- 2.4 商品规格表
CREATE TABLE product_specs (
    spec_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    spec_name VARCHAR(255) NOT NULL,
    spec_value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_product_id (product_id)
);

-- 3. 订单模块
-- 3.1 订单主表
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(32) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_amount DECIMAL(10, 2) NOT NULL,
    shipping_fee DECIMAL(10, 2) NOT NULL DEFAULT 0,
    status ENUM('pending_payment', 'paid', 'shipped', 'delivered', 'completed', 'canceled') DEFAULT 'pending_payment',
    payment_time TIMESTAMP NULL,
    shipping_time TIMESTAMP NULL,
    completion_time TIMESTAMP NULL,
    address_snapshot TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_profiles(user_id),
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status)
);

-- 3.2 订单商品表
CREATE TABLE order_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    merchant_id INT NOT NULL,
    product_snapshot TEXT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (merchant_id) REFERENCES merchant_profiles(merchant_id),
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id),
    INDEX idx_merchant_id (merchant_id)
);

-- 4. 购物车模块
CREATE TABLE shopping_cart_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    selected BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_profiles(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    UNIQUE KEY uk_user_product (user_id, product_id),
    INDEX idx_user_id (user_id)
);

-- 5. 支付模块
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    payment_no VARCHAR(32) NOT NULL UNIQUE,
    order_id INT NOT NULL,
    user_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status ENUM('pending', 'processing', 'success', 'failed', 'refunded') DEFAULT 'pending',
    payment_time TIMESTAMP NULL,
    callback_content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (user_id) REFERENCES user_profiles(user_id),
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_payment_no (payment_no)
);

-- 6. 评价模块
CREATE TABLE product_reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    order_item_id INT NOT NULL UNIQUE,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    merchant_id INT NOT NULL,
    rating TINYINT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    content TEXT,
    images TEXT,
    status ENUM('pending', 'published', 'hidden') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_item_id) REFERENCES order_items(item_id),
    FOREIGN KEY (user_id) REFERENCES user_profiles(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (merchant_id) REFERENCES merchant_profiles(merchant_id),
    INDEX idx_product_id (product_id),
    INDEX idx_merchant_id (merchant_id)
);

-- 7. 系统日志表
CREATE TABLE system_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    auth_id INT,
    module VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    detail TEXT,
    ip_address VARCHAR(50),
    user_agent VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (auth_id) REFERENCES auth_users(auth_id),
    INDEX idx_auth_id (auth_id),
    INDEX idx_module (module),
    INDEX idx_created_at (created_at)
);

-- 8. 库存变动记录表
CREATE TABLE stock_movements (
    movement_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    quantity INT NOT NULL COMMENT '正数表示入库，负数表示出库',
    type ENUM('order_create', 'order_cancel', 'return', 'manual_adjustment') NOT NULL,
    reference_id INT COMMENT '关联的订单ID或退货单ID',
    operator_id INT COMMENT '操作人ID',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (operator_id) REFERENCES auth_users(auth_id),
    INDEX idx_product_id (product_id),
    INDEX idx_reference_id (reference_id)
);

-- 9. 退货订单表
CREATE TABLE return_orders (
    return_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    order_item_id INT NOT NULL,
    user_id INT NOT NULL,
    merchant_id INT NOT NULL,
    return_amount DECIMAL(10,2) NOT NULL,
    status ENUM('pending', 'approved', 'rejected', 'returned', 'refunded') NOT NULL,
    reason_type ENUM('quality_issue', 'wrong_item', 'not_satisfied', 'other') NOT NULL,
    reason_detail TEXT,
    handling_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (order_item_id) REFERENCES order_items(item_id),
    FOREIGN KEY (user_id) REFERENCES user_profiles(user_id),
    FOREIGN KEY (merchant_id) REFERENCES merchant_profiles(merchant_id),
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_merchant_id (merchant_id)
);

-- 10. 收藏夹表
CREATE TABLE user_favorites (
    favorite_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_profiles(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    UNIQUE KEY uk_user_product (user_id, product_id),
    INDEX idx_user_id (user_id)
);

-- 11. 消息通知表
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    type ENUM('order_status', 'return_status', 'system') NOT NULL,
    reference_id INT COMMENT '相关的订单ID或退货单ID',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_profiles(user_id),
    INDEX idx_user_id (user_id),
    INDEX idx_reference_id (reference_id)
);
