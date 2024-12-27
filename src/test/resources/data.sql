-- 创建测试用户
INSERT INTO user (username, password, email, status, created_time, updated_time)
VALUES ('testadmin', '$2a$10$XXXXXX', 'admin@example.com', 1, NOW(), NOW());

-- 创建测试角色
INSERT INTO role (code, name, status)
VALUES ('ADMIN', '管理员', 1),
       ('MERCHANT', '商家', 1),
       ('USER', '用户', 1);

-- 创建用户角色关系
INSERT INTO user_role (user_id, role_id)
VALUES (1, 1);

-- 创建测试分类
INSERT INTO category (name, parent_id, level, status, sort)
VALUES ('电子产品', 0, 1, 1, 1),
       ('手机', 1, 2, 1, 1);

-- 创建测试商品
INSERT INTO product (name, category_id, price, stock, status)
VALUES ('测试商品1', 2, 999.00, 100, 1); 