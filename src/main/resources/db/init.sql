-- 删除数据库
DROP DATABASE IF EXISTS `super_mall`;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `super_mall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `super_mall`;

-- 创建角色表
CREATE TABLE IF NOT EXISTS `roles` (
    `role_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(200) COMMENT '角色描述',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 创建权限表
CREATE TABLE IF NOT EXISTS `permissions` (
    `permission_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    `description` VARCHAR(200) COMMENT '权限描述',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 创建角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permissions` (
    `role_id` INT NOT NULL COMMENT '角色ID',
    `permission_id` INT NOT NULL COMMENT '权限ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`role_id`, `permission_id`),
    CONSTRAINT `fk_role_permissions_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`),
    CONSTRAINT `fk_role_permissions_permission` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 创建用户认证表
CREATE TABLE IF NOT EXISTS `auth_users` (
    `auth_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password_hash` VARCHAR(100) NOT NULL COMMENT '密码哈希',
    `email` VARCHAR(100) COMMENT '邮箱',
    `role_id` INT COMMENT '角色ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-活跃, INACTIVE-未激活, LOCKED-锁定',
    `last_login` DATETIME COMMENT '最后登录时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`auth_id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_role_id` (`role_id`),
    CONSTRAINT `fk_auth_users_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户认证表';

-- 创建用户配置表
CREATE TABLE IF NOT EXISTS `user_profiles` (
    `user_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `auth_id` INT NOT NULL COMMENT '认证用户ID',
    `full_name` VARCHAR(100) COMMENT '用户全名',
    `phone_number` VARCHAR(20) COMMENT '手机号码',
    `avatar_url` VARCHAR(255) COMMENT '头像URL',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`),
    KEY `idx_auth_id` (`auth_id`),
    CONSTRAINT `fk_user_profiles_auth_user` FOREIGN KEY (`auth_id`) REFERENCES `auth_users` (`auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户配置表';

-- 创建用户地址表
CREATE TABLE IF NOT EXISTS `user_addresses` (
    `address_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `province` VARCHAR(50) NOT NULL COMMENT '省份',
    `city` VARCHAR(50) NOT NULL COMMENT '城市',
    `district` VARCHAR(50) NOT NULL COMMENT '区县',
    `street` VARCHAR(200) NOT NULL COMMENT '街道地址',
    `postal_code` VARCHAR(10) COMMENT '邮政编码',
    `is_default` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址：0-否，1-是',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`address_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_user_addresses_user_profiles` FOREIGN KEY (`user_id`) REFERENCES `user_profiles` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS `user_roles` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `role_id` INT NOT NULL COMMENT '角色ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`),
    CONSTRAINT `fk_user_roles_user` FOREIGN KEY (`user_id`) REFERENCES `user_profiles` (`user_id`),
    CONSTRAINT `fk_user_roles_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 创建商品分类表
CREATE TABLE IF NOT EXISTS `categories` (
    `category_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `parent_id` INT COMMENT '父分类ID',
    `level` INT NOT NULL COMMENT '分类层级',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用，INACTIVE-禁用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`category_id`),
    KEY `idx_parent_id` (`parent_id`),
    CONSTRAINT `fk_categories_parent` FOREIGN KEY (`parent_id`) REFERENCES `categories` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 创建商家表
CREATE TABLE IF NOT EXISTS `merchant_profiles` (
    `merchant_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `auth_id` INT NOT NULL COMMENT '认证用户ID',
    `shop_name` VARCHAR(100) NOT NULL COMMENT '店铺名称',
    `shop_description` TEXT COMMENT '店铺描述',
    `business_license` VARCHAR(255) NOT NULL COMMENT '营业执照号',
    `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `contact_email` VARCHAR(100) NOT NULL COMMENT '联系邮箱',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核，APPROVED-已审核，REJECTED-已拒绝，SUSPENDED-已暂停，TERMINATED-已终止',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`merchant_id`),
    UNIQUE KEY `uk_shop_name` (`shop_name`),
    UNIQUE KEY `uk_business_license` (`business_license`),
    KEY `idx_auth_id` (`auth_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_merchant_profiles_auth_user` FOREIGN KEY (`auth_id`) REFERENCES `auth_users` (`auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家表';

-- 创建商品表
CREATE TABLE IF NOT EXISTS `products` (
    `product_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `merchant_id` INT NOT NULL COMMENT '商家ID',
    `category_id` INT NOT NULL COMMENT '分类ID',
    `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `description` TEXT COMMENT '商品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '售价',
    `original_price` DECIMAL(10,2) NOT NULL COMMENT '原价',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    `sales` INT NOT NULL DEFAULT 0 COMMENT '销量',
    `main_image` VARCHAR(255) NOT NULL COMMENT '主图URL',
    `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，PENDING-待审核，REJECTED-已拒绝，APPROVED-已通过，ON_SALE-在售，OFF_SALE-下架',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`product_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_products_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`),
    CONSTRAINT `fk_products_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant_profiles` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 创建用户收藏表
CREATE TABLE IF NOT EXISTS `user_favorites` (
    `favorite_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `product_id` INT NOT NULL COMMENT '商品ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`favorite_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    CONSTRAINT `fk_user_favorites_user` FOREIGN KEY (`user_id`) REFERENCES `user_profiles` (`user_id`),
    CONSTRAINT `fk_user_favorites_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';


-- 创建商品图片表
CREATE TABLE IF NOT EXISTS `product_images` (
    `image_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `product_id` INT NOT NULL COMMENT '商品ID',
    `image_url` VARCHAR(255) NOT NULL COMMENT '图片URL',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`image_id`),
    KEY `idx_product_id` (`product_id`),
    CONSTRAINT `fk_product_images_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';

-- 创建商品规格表
CREATE TABLE IF NOT EXISTS `product_specs` (
    `spec_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `product_id` INT NOT NULL COMMENT '商品ID',
    `spec_name` VARCHAR(50) NOT NULL COMMENT '规格名称',
    `spec_value` VARCHAR(100) NOT NULL COMMENT '规格值',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`spec_id`),
    KEY `idx_product_id` (`product_id`),
    CONSTRAINT `fk_product_specs_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格表';

-- 创建库存变动记录表
CREATE TABLE IF NOT EXISTS `stock_movements` (
    `movement_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `product_id` INT NOT NULL COMMENT '商品ID',
    `quantity` INT NOT NULL COMMENT '变动数量',
    `type` VARCHAR(20) NOT NULL COMMENT '变动类型：ORDER_CREATE-订单创建，ORDER_CANCEL-订单取消，RETURN-退货，MANUAL_ADJUSTMENT-手动调整',
    `reference_id` INT COMMENT '关联ID',
    `operator_id` INT COMMENT '操作人ID',
    `notes` VARCHAR(200) COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`movement_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_type` (`type`),
    KEY `idx_reference_id` (`reference_id`),
    CONSTRAINT `fk_stock_movements_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
    CONSTRAINT `fk_stock_movements_operator` FOREIGN KEY (`operator_id`) REFERENCES `user_profiles` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存变动记录表';

-- 创建购物车表
CREATE TABLE IF NOT EXISTS `shopping_cart_items` (
    `item_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `product_id` INT NOT NULL COMMENT '商品ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '商品数量',
    `selected` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否选中：0-未选中，1-已选中',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`item_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    CONSTRAINT `fk_shopping_cart_items_user` FOREIGN KEY (`user_id`) REFERENCES `user_profiles` (`user_id`),
    CONSTRAINT `fk_shopping_cart_items_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 创建通知表
CREATE TABLE IF NOT EXISTS `notifications` (
    `notification_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT NOT NULL COMMENT '通知内容',
    `type` VARCHAR(50) NOT NULL COMMENT '通知类型：ORDER_CREATED-订单创建,ORDER_PAID-订单支付成功,ORDER_SHIPPED-订单已发货,ORDER_COMPLETED-订单已完成,ORDER_CANCELLED-订单已取消,RETURN_CREATED-退货申请已提交,RETURN_APPROVED-退货申请已通过,RETURN_REJECTED-退货申请已拒绝,RETURN_COMPLETED-退货已完成,PAYMENT_SUCCESS-支付成功,PAYMENT_FAILED-支付失败,REFUND_SUCCESS-退款成功,REFUND_FAILED-退款失败,SYSTEM_MAINTENANCE-系统维护,SYSTEM_ANNOUNCEMENT-系统公告',
    `reference_id` INT COMMENT '关联ID',
    `is_read` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    `priority` VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级：HIGH-高，MEDIUM-中，LOW-低',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待发送，SENT-已发送，READ-已读，EXPIRED-已过期，FAILED-发送失败',
    `expire_time` DATETIME COMMENT '过期时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`notification_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_reference_id` (`reference_id`),
    KEY `idx_expire_time` (`expire_time`),
    CONSTRAINT `fk_notifications_user` FOREIGN KEY (`user_id`) REFERENCES `user_profiles` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 创建系统日志表
CREATE TABLE IF NOT EXISTS `system_logs` (
    `log_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` INT COMMENT '用户ID',
    `module` VARCHAR(50) NOT NULL COMMENT '模块名称',
    `action` VARCHAR(50) NOT NULL COMMENT '操作类型',
    `description` TEXT NOT NULL COMMENT '操作描述',
    `ip_address` VARCHAR(50) COMMENT 'IP地址',
    `user_agent` VARCHAR(255) COMMENT '用户代理',
    `request_url` VARCHAR(255) COMMENT '请求URL',
    `request_method` VARCHAR(10) COMMENT '请求方法',
    `request_params` TEXT COMMENT '请求参数',
    `response_code` INT COMMENT '响应状态码',
    `response_data` TEXT COMMENT '响应数据',
    `error_message` TEXT COMMENT '错误信息',
    `execution_time` INT COMMENT '执行时间(ms)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`log_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_module` (`module`),
    KEY `idx_action` (`action`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';

-- 初始化角色数据
INSERT INTO roles (name, description) VALUES 
('ADMIN', '系统管理员'),
('USER', '普通用户'),
('MERCHANT', '商城商家用户');

-- 初始化权限数据
INSERT INTO permissions (name, description) VALUES 
('product:create', '创建商品'),
('product:update', '更新商品'),
('product:delete', '删除商品'),
('product:manage', '管理商品状态'),
('product:view', '查看商品'),
('product:list', '商品列表'),
('merchant:approve', '审核商家'),
('merchant:manage', '管理商家'),
('user:manage', '管理用户'),
('order:manage', '管理订单'),
('system:manage', '系统管理');

-- 为角色分配权限
-- 管理员拥有所有权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, permission_id FROM permissions;

-- 商家拥有商品相关权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 3, permission_id 
FROM permissions 
WHERE name IN (
    'product:create',
    'product:update',
    'product:delete',
    'product:manage',
    'product:view',
    'product:list'
);

-- 初始化管理员账户
INSERT INTO auth_users (username, password_hash, email, role_id, status) VALUES 
('admin', '$2a$10$fXaBEAS5nYX/DIyg81buge861IG//56/2E2KVFZIu1QA2rgK8Qwsy', 'admin@supermall.com', 1, 'ACTIVE');

-- 初始化管理员用户资料
INSERT INTO user_profiles (auth_id, full_name, phone_number)
SELECT auth_id, '系统管理员', '13800000000'
FROM auth_users
WHERE username = 'admin';

-- 插入一些基础的商品分类数据
INSERT INTO categories (name, parent_id, level, sort_order, status) VALUES
('电子产品', NULL, 1, 1, 'ACTIVE'),
('服装', NULL, 1, 2, 'ACTIVE'),
('食品', NULL, 1, 3, 'ACTIVE'),
('手机', 1, 2, 1, 'ACTIVE'),
('电脑', 1, 2, 2, 'ACTIVE'),
('男装', 2, 2, 1, 'ACTIVE'),
('女装', 2, 2, 2, 'ACTIVE'),
('零食', 3, 2, 1, 'ACTIVE'),
('饮料', 3, 2, 2, 'ACTIVE');

-- 创建订单表
CREATE TABLE IF NOT EXISTS `orders` (
    `order_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `merchant_id` INT NOT NULL COMMENT '商家ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `payment_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `shipping_fee` DECIMAL(10,2) NOT NULL COMMENT '运费',
    `status` VARCHAR(20) NOT NULL COMMENT '订单状态',
    `payment_time` DATETIME COMMENT '支付时间',
    `shipping_time` DATETIME COMMENT '发货时间',
    `completion_time` DATETIME COMMENT '完成时间',
    `address_snapshot` TEXT NOT NULL COMMENT '收货地址快照',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`order_id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `user_profiles` (`user_id`),
    CONSTRAINT `fk_orders_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant_profiles` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 创建订单项表
CREATE TABLE IF NOT EXISTS `order_items` (
    `item_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_id` INT NOT NULL COMMENT '订单ID',
    `product_id` INT NOT NULL COMMENT '商品ID',
    `merchant_id` INT NOT NULL COMMENT '商家ID',
    `product_snapshot` TEXT NOT NULL COMMENT '商品快照',
    `quantity` INT NOT NULL COMMENT '购买数量',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    `payment_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `product_spec` VARCHAR(255) COMMENT '商品规格',
    `is_refunded` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已退款',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`item_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    CONSTRAINT `fk_order_items_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
    CONSTRAINT `fk_order_items_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
    CONSTRAINT `fk_order_items_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant_profiles` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- 创建订单退款表
CREATE TABLE IF NOT EXISTS `order_refunds` (
    `refund_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_id` INT NOT NULL COMMENT '订单ID',
    `order_item_id` INT NOT NULL COMMENT '订单项ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `merchant_id` INT NOT NULL COMMENT '商家ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    `reason` VARCHAR(255) NOT NULL COMMENT '退款原因',
    `description` TEXT COMMENT '详细说明',
    `images` TEXT COMMENT '图片凭证',
    `merchant_reply` TEXT COMMENT '商家回复',
    `status` VARCHAR(20) NOT NULL COMMENT '退款状态',
    `completion_time` DATETIME COMMENT '完成时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`refund_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_item_id` (`order_item_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_order_refunds_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
    CONSTRAINT `fk_order_refunds_order_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`item_id`),
    CONSTRAINT `fk_order_refunds_user` FOREIGN KEY (`user_id`) REFERENCES `user_profiles` (`user_id`),
    CONSTRAINT `fk_order_refunds_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant_profiles` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单退款表';

-- 创建退货订单表
CREATE TABLE IF NOT EXISTS `return_orders` (
    `return_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_id` INT NOT NULL COMMENT '订单ID',
    `order_item_id` INT NOT NULL COMMENT '订单项ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `merchant_id` INT NOT NULL COMMENT '商家ID',
    `return_amount` DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    `status` VARCHAR(20) NOT NULL COMMENT '退货状态',
    `reason_type` VARCHAR(20) NOT NULL COMMENT '退货原因类型',
    `reason_detail` TEXT COMMENT '退货原因详情',
    `handling_notes` TEXT COMMENT '处理备注',
    `logistics_info` TEXT COMMENT '物流信息',
    `logistics_status` VARCHAR(20) COMMENT '物流状态',
    `logistics_remark` TEXT COMMENT '物流备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`return_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_item_id` (`order_item_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_return_orders_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
    CONSTRAINT `fk_return_orders_order_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`item_id`),
    CONSTRAINT `fk_return_orders_user` FOREIGN KEY (`user_id`) REFERENCES `user_profiles` (`user_id`),
    CONSTRAINT `fk_return_orders_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant_profiles` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退货订单表';

-- 创建支付记录表
CREATE TABLE IF NOT EXISTS `payments` (
    `payment_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `payment_no` VARCHAR(50) NOT NULL COMMENT '支付单号',
    `order_id` INT NOT NULL COMMENT '订单ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `payment_method` VARCHAR(20) NOT NULL COMMENT '支付方式：ALIPAY-支付宝，WECHAT-微信支付，BANK_CARD-银行卡',
    `transaction_id` VARCHAR(100) COMMENT '第三方交易号',
    `status` VARCHAR(20) NOT NULL COMMENT '支付状态：PENDING-待支付，PROCESSING-处理中，SUCCESS-支付成功，FAILED-支付失败，REFUND_PENDING-退款中，REFUNDED-已退款，CLOSED-已关闭',
    `failure_reason` VARCHAR(255) COMMENT '失败原因',
    `callback_content` TEXT COMMENT '回调内容',
    `refund_for_payment_id` INT COMMENT '原支付记录ID（仅退款时使用）',
    `is_refund` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否为退款记录：0-否，1-是',
    `expire_time` DATETIME COMMENT '支付超时时间',
    `channel_config` TEXT COMMENT '支付渠道配置（JSON格式）',
    `retry_count` INT NOT NULL DEFAULT 0 COMMENT '重试��数',
    `notify_url` VARCHAR(255) COMMENT '支付回调通知地址',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `paid_at` DATETIME COMMENT '支付完成时间',
    PRIMARY KEY (`payment_id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    UNIQUE KEY `uk_transaction_id` (`transaction_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_refund_for_payment_id` (`refund_for_payment_id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_expire_time` (`expire_time`),
    CONSTRAINT `fk_payments_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
    CONSTRAINT `fk_payments_user` FOREIGN KEY (`user_id`) REFERENCES `user_profiles` (`user_id`),
    CONSTRAINT `fk_payments_refund` FOREIGN KEY (`refund_for_payment_id`) REFERENCES `payments` (`payment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- 创建商品评论表
CREATE TABLE IF NOT EXISTS `product_reviews` (
    `review_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_id` INT NOT NULL COMMENT '订单ID',
    `order_item_id` INT NOT NULL COMMENT '订单项ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `product_id` INT NOT NULL COMMENT '商品ID',
    `merchant_id` INT NOT NULL COMMENT '商家ID',
    `rating` INT NOT NULL COMMENT '评分',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `images` TEXT COMMENT '图片凭证',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核，PUBLISHED-已发布，REJECTED-已拒绝，DELETED-已删除',
    `reject_reason` VARCHAR(255) COMMENT '拒绝原因',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`review_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_item_id` (`order_item_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_product_reviews_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
    CONSTRAINT `fk_product_reviews_order_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`item_id`),
    CONSTRAINT `fk_product_reviews_user` FOREIGN KEY (`user_id`) REFERENCES `user_profiles` (`user_id`),
    CONSTRAINT `fk_product_reviews_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
    CONSTRAINT `fk_product_reviews_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant_profiles` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评论表';

-- 创建评论回复表
CREATE TABLE IF NOT EXISTS `review_replies` (
    `reply_id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `review_id` INT NOT NULL COMMENT '评论ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `merchant_id` INT NOT NULL COMMENT '商家ID',
    `content` TEXT NOT NULL COMMENT '回复内容',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED' COMMENT '状态：PUBLISHED-已发布，DELETED-已删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`reply_id`),
    KEY `idx_review_id` (`review_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_review_replies_review` FOREIGN KEY (`review_id`) REFERENCES `product_reviews` (`review_id`),
    CONSTRAINT `fk_review_replies_user` FOREIGN KEY (`user_id`) REFERENCES `user_profiles` (`user_id`),
    CONSTRAINT `fk_review_replies_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant_profiles` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论回复表';