-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(64) COMMENT '昵称',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(64) COMMENT '邮箱',
    `avatar` VARCHAR(255) COMMENT '头像',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0->禁用；1->启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0->否；1->是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name` VARCHAR(128) NOT NULL COMMENT '商品名称',
    `description` TEXT COMMENT '商品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商���价格',
    `stock` INT NOT NULL COMMENT '库存',
    `sales` INT NOT NULL DEFAULT 0 COMMENT '销量',
    `main_image` VARCHAR(255) COMMENT '主图',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0->下架；1->上架',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0->否；1->是',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `freight_amount` DECIMAL(10,2) NOT NULL COMMENT '运费',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单',
    `pay_type` TINYINT NOT NULL DEFAULT 0 COMMENT '支付方式：0->未支付；1->支付宝；2->微信',
    `pay_time` DATETIME COMMENT '支付时间',
    `delivery_time` DATETIME COMMENT '发货时间',
    `receive_time` DATETIME COMMENT '确认收货时间',
    `comment_time` DATETIME COMMENT '评价时间',
    `cancel_time` DATETIME COMMENT '取消时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0->否；1->是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单项表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(128) NOT NULL COMMENT '商品名称',
    `product_image` VARCHAR(255) COMMENT '商品图片',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    `quantity` INT NOT NULL COMMENT '购买数量',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '商品总价',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0->否；1->是',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- 订单物流表
CREATE TABLE IF NOT EXISTS `order_delivery` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '物流ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `receiver_name` VARCHAR(64) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `receiver_province` VARCHAR(64) NOT NULL COMMENT '省份',
    `receiver_city` VARCHAR(64) NOT NULL COMMENT '城市',
    `receiver_district` VARCHAR(64) NOT NULL COMMENT '区',
    `receiver_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
    `delivery_company` VARCHAR(64) COMMENT '物流公司',
    `delivery_sn` VARCHAR(64) COMMENT '物流单号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0->否；1->是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单物流表';

-- 订单日志表
CREATE TABLE IF NOT EXISTS `order_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `user_id` BIGINT NOT NULL COMMENT '操作人ID',
    `order_status` TINYINT NOT NULL COMMENT '订单状态',
    `action` VARCHAR(64) NOT NULL COMMENT '操作行为',
    `note` VARCHAR(255) COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单日志表';

-- 插入测试数据
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `email`) VALUES
('test', '$2a$10$LvP3lR6JXwP0.L5QXPx8l.U0moH.ZYfN4R.vD3eZ3VexVjlR.u3Zu', '测试用户', '13800138000', 'test@example.com');

INSERT INTO `product` (`name`, `description`, `price`, `stock`, `main_image`) VALUES
('iPhone 15 Pro Max', '苹果最新旗舰手机', 9999.00, 100, 'iphone15.jpg'),
('MacBook Pro M3', '搭载M3芯片的MacBook Pro', 14999.00, 50, 'macbook.jpg'); 