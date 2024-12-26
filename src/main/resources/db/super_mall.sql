-- 创建数据库
CREATE DATABASE IF NOT EXISTS super_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE super_mall;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(64) NOT NULL COMMENT '用户名',
    `password` varchar(128) NOT NULL COMMENT '密码',
    `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
    `status` tinyint(4) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `role` varchar(20) DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS `category` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` varchar(64) NOT NULL COMMENT '分类名称',
    `parent_id` bigint(20) DEFAULT 0 COMMENT '父分类ID',
    `level` int(11) DEFAULT 1 COMMENT '层级',
    `sort` int(11) DEFAULT 0 COMMENT '排序',
    `icon` varchar(255) DEFAULT NULL COMMENT '图标',
    `status` tinyint(4) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 品牌表
CREATE TABLE IF NOT EXISTS `brand` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '品牌ID',
    `name` varchar(64) NOT NULL COMMENT '品牌名称',
    `logo` varchar(255) DEFAULT NULL COMMENT '品牌logo',
    `description` text DEFAULT NULL COMMENT '品牌描述',
    `status` tinyint(4) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `sort` int(11) DEFAULT 0 COMMENT '排序',
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='品牌表';

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name` varchar(100) NOT NULL COMMENT '商品名称',
    `subtitle` varchar(200) DEFAULT NULL COMMENT '副标题',
    `category_id` bigint NOT NULL COMMENT '分类ID',
    `brand_id` bigint NOT NULL COMMENT '品牌ID',
    `main_image` varchar(500) NOT NULL COMMENT '主图',
    `sub_images` text COMMENT '子图（JSON数组）',
    `detail` text COMMENT '商品详情',
    `price` decimal(10,2) NOT NULL COMMENT '价格',
    `stock` int NOT NULL COMMENT '库存',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-下架，1-上架',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_brand_id` (`brand_id`),
    KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表';

-- 购物车表
CREATE TABLE IF NOT EXISTS `cart` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `product_id` bigint(20) NOT NULL COMMENT '商品ID',
    `quantity` int(11) NOT NULL COMMENT '数量',
    `checked` tinyint(1) DEFAULT 1 COMMENT '是否选中：0-未选中，1-已选中',
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` varchar(64) NOT NULL COMMENT '订单编号',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
    `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
    `freight_amount` decimal(10,2) DEFAULT 0.00 COMMENT '运费',
    `status` tinyint(4) DEFAULT 0 COMMENT '订单状态：0-待付款，1-待发货，2-已发货，3-已完成，4-已取消',
    `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
    `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
    `receive_time` datetime DEFAULT NULL COMMENT '确认收货时间',
    `comment_time` datetime DEFAULT NULL COMMENT '评价时间',
    `receiver_name` varchar(64) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` varchar(20) NOT NULL COMMENT '收货人电话',
    `receiver_address` varchar(255) NOT NULL COMMENT '收货地址',
    `note` varchar(500) DEFAULT NULL COMMENT '订单备注',
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单项表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
    `order_id` bigint(20) NOT NULL COMMENT '订单ID',
    `order_no` varchar(64) NOT NULL COMMENT '订单编号',
    `product_id` bigint(20) NOT NULL COMMENT '商品ID',
    `product_name` varchar(128) NOT NULL COMMENT '商品名称',
    `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
    `price` decimal(10,2) NOT NULL COMMENT '商品单价',
    `quantity` int(11) NOT NULL COMMENT '购买数量',
    `total_amount` decimal(10,2) NOT NULL COMMENT '商品总价',
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order` (`order_id`),
    KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- 支付信息表
CREATE TABLE IF NOT EXISTS `payment` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '支付ID',
    `order_no` varchar(64) NOT NULL COMMENT '订单编号',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `payment_no` varchar(64) DEFAULT NULL COMMENT '支付流水号',
    `payment_method` tinyint(4) DEFAULT NULL COMMENT '支付方式：1-支付宝，2-微信',
    `payment_amount` decimal(10,2) NOT NULL COMMENT '支付金额',
    `status` tinyint(4) DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付，2-支付失败',
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付信息表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `system_config` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_key` varchar(64) NOT NULL COMMENT '配置键',
    `config_value` text NOT NULL COMMENT '配置值',
    `description` varchar(255) DEFAULT NULL COMMENT '配置描述',
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 初始化管理员账号
INSERT INTO `user` (`username`, `password`, `email`, `role`, `status`)
VALUES ('admin', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'admin@example.com', 'ADMIN', 1); 