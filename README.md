# SuperMall 电商系统后端

SuperMall是一个基于Spring Boot的现代电商系统后端，提供完整的电商功能支持。

## 技术栈

- **核心框架：** Spring Boot 3.1.5
- **安全框架：** Spring Security + JWT
- **数据库：** MySQL 8.0
- **ORM：** MyBatis-Plus 3.5.5
- **API文档：** SpringDoc OpenAPI 2.3.0
- **其他工具：** Lombok, Spring AOP

## 系统功能

### 1. 用户中心
- 用户注册/登录
- 个人信息管理
- 地址管理
- 安全认证

### 2. 商品系统
- 商品管理
- 分类管理
- 库存管理
- 商品搜索
- 商品评价

### 3. 订单系统
- 购物车管理
- 订单创建与管理
- 支付集成
- 订单状态追踪
- 退货/退款处理

### 4. 商家系统
- 商家入驻
- 商品上架/下架
- 订单处理
- 销售统计

### 5. 收藏系统
- 商品收藏
- 收藏列表管理
- 收藏状态查询
- 收藏数量统计

### 6. 评价系统
- 商品评价
- 评价管理
- 评分统计
- 图片评价

### 7. 通知系统
- 系统通知
- 订单通知
- 促销通知
- 消息管理

### 8. 系统功能
- 日志记录
- 权限控制
- 数据统计
- 系统监控

### 9. 营销系统
- 优惠券管理
- 秒杀活动
- 拼团活动
- 满减促销
- 积分系统
- 会员等级

### 10. 搜索系统
- 商品搜索
- 搜索历史
- 热门搜索
- 相关推荐
- 搜索过滤
- 分类筛选

### 11. 数据统计系统
- 销售统计
- 用户分析
- 商品分析
- 订单分析
- 流量分析
- 实时监控

### 12. 物流系统
- 物流跟踪
- 配送管理
- 运费模板
- 快递公司管理
- 发货管理
- 退货管理

### 13. 客服系统
- 在线客服
- 工单系统
- 常见问题
- 反馈管理
- 客服评价

## API 接口

### 用户接口
- POST `/api/v1/auth/register` - 用户注册
- POST `/api/v1/auth/login` - 用户登录
- GET `/api/v1/users/profile` - 获取用户信息
- PUT `/api/v1/users/profile` - 更新用户信息

### 商品接口
- GET `/api/v1/products` - 获取商品列表
- GET `/api/v1/products/{id}` - 获取商品详情
- POST `/api/v1/products` - 创建商品
- PUT `/api/v1/products/{id}` - 更新商品
- DELETE `/api/v1/products/{id}` - 删除商品

### 订单接口
- POST `/api/v1/orders` - 创建订单
- GET `/api/v1/orders` - 获取订单列表
- GET `/api/v1/orders/{id}` - 获取订单详情
- PUT `/api/v1/orders/{id}/status` - 更新订单状态
- POST `/api/v1/orders/{id}/pay` - 订单支付

### 购物车接口
- POST `/api/v1/cart` - 添加购物车
- GET `/api/v1/cart` - 获取购物车列表
- PUT `/api/v1/cart/{id}` - 更新购物车
- DELETE `/api/v1/cart/{id}` - 删除购物车项

### 收藏接口
- POST `/api/v1/favorites/{productId}` - 添加收藏
- DELETE `/api/v1/favorites/{productId}` - 取消收藏
- GET `/api/v1/favorites` - 获取收藏列表
- GET `/api/v1/favorites/{productId}/check` - 检查是否已收藏
- GET `/api/v1/favorites/count` - 获取收藏数量

### 评价接口
- POST `/api/v1/reviews` - 发表评价
- GET `/api/v1/reviews/product/{productId}` - 获取商品评价
- GET `/api/v1/reviews/user` - 获取用户评价
- DELETE `/api/v1/reviews/{id}` - 删除评价

### 通知接口
- GET `/api/v1/notifications` - 获取通知列表
- PUT `/api/v1/notifications/{id}/read` - 标记通知已读
- DELETE `/api/v1/notifications/{id}` - 删除通知



## 数据库设计

系统包含以下主要数据表：

- `user` - 用户表
- `merchant` - 商家表
- `category` - 商品分类表
- `product` - 商品表
- `product_image` - 商品图片表
- `cart` - 购物车表
- `order` - 订单表
- `order_item` - 订单明细表
- `product_review` - 商品评价表
- `favorite` - 收藏表
- `payment` - 支付记录表
- `notification` - 系统通知表
- `system_logs` - 系统日志表

## 项目结构

```
src/main/java/com/supermall/backend/
├── common/                 # 公共组件
│   ├── config/            # 配置类
│   ├── exception/         # 异常处理
│   ├── security/          # 安全相关
│   └── util/              # 工具类
├── domain/                # 业务领域模块
│   ├── user/             # 用户模块
│   ├── product/          # 商品模块
│   ├── order/            # 订单模块
│   ├── cart/             # 购物车模块
│   ├── favorite/         # 收藏模块
│   ├── review/           # 评价模块
│   ├── payment/          # 支付模块
│   ├── notification/     # 通知模块
│   ├── marketing/        # 营销模块
│   ├── search/           # 搜索模块
│   ├── statistics/       # 统计模块
│   ├── logistics/        # 物流模块
│   └── support/          # 客服模块
└── SupermallBackendApplication.java
```

## 安装部署

1. 环境要求
   - JDK 17+
   - MySQL 8.0+
   - Redis 6.0+
   - Maven 3.6+

2. 配置数据库
   ```sql
   source /src/main/resources/db/super_mall.sql
   ```

3. 修改配置
   - 配置文件位置：`src/main/resources/application.yml`
   - 修改数据库连接信息
   - 配置JWT密钥

4. 编译运行
   ```bash
   mvn clean package
   java -jar target/supermall-backend-0.0.1-SNAPSHOT.jar
   ```

## 安全说明

- 使用Spring Security进行安全控制
- JWT用于用户认证
- 密码加密存储
- 接口权限控制
- 防SQL注入
- XSS防护
- CORS配置

## 性能优化

- Redis缓存
- 数据库索引优化
- 连接池配置
- 分页查询
- 延迟加载

## 开发团队

- 开发人员：[团队成员]
- 联系方式：[联系信息]

## 版权说明

Copyright © 2024 SuperMall Team