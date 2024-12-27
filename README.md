# SuperMall 超级商城系统

一个基于Spring Boot的电商系统，支持商家入驻、商品管理、订单处理等核心电商功能。

## 技术栈

- 核心框架：Spring Boot 3.x
- 安全框架：Spring Security 6.x + JWT
- ORM框架：MyBatis-Plus
- 数据库：MySQL 8.0
- 缓存：Redis 7.x
- 接口文档：SpringDoc OpenAPI 2.x
- 开发语言：Java 17

## 项目结构 

```
src/main/java/com/supermall/backend
├── domain/ # 业务领域
│ ├── user/ # 用户模块
│ ├── product/ # 商品模块
│ ├── order/ # 订单模块
│ └── merchant/ # 商家模块
│ ├── controller/ # 控制器
│ ├── service/ # 服务层
│ ├── mapper/ # MyBatis-Plus接口
│ └── entity/ # 实体类
│
├── common/ # 通用组件
│ ├── config/ # 配置类
│ ├── exception/ # 异常处理
│ └── util/ # 工具类
│
└── SupermallBackendApplication.java
```


## 核心功能

### 用户中心
- 用户注册登录
- 个人信息管理
- 收货地址管理
- 权限角色控制

### 商品系统
- 商品分类管理
- 商品信息管理
- 商品库存管理
- 商品搜索功能

### 订单系统
- 购物车管理
- 订单创建处理
- 订单状态流转
- 支付功能集成

### 商家系统
- 商家入驻
- 店铺管理
- 商品管理
- 订单处理

## 数据库设计

主要包含以下模块的表结构：
- 权限模块：roles, permissions, role_permissions
- 用户模块：auth_users, user_profiles, merchant_profiles
- 商品模块：categories, products, product_specs
- 订单模块：orders, order_items, payments
- 购物车模块：shopping_cart_items
- 评价模块：product_reviews

详细的数据库结构见：`src/main/resources/db/super_mall.sql`

## 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Redis 7.0+
- Maven 3.8+

### 开发环境搭建

1. 克隆项目
```bash
git clone https://github.com/jacl-coder/supermall-backend.git
```

2. 导入数据库
```bash
mysql -u root -p < src/main/resources/db/super_mall.sql
```

## API文档

启动应用后访问：http://localhost:8080/swagger-ui.html

## 开发规范

1. 代码规范
   - 遵循阿里巴巴Java开发规范
   - 每个模块按controller/service/mapper/entity组织代码
   - 保持代码简洁清晰

2. 提交规范
   - feat: 新功能
   - fix: 修复问题
   - docs: 文档修改
   - style: 代码格式化
   - refactor: 代码重构
   - test: 测试相关
   - chore: 其他修改

## 许可证

[MIT License](LICENSE)