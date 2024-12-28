让我帮您分析项目的功能结构。从代码和API测试集合来看，这是一个电商后端项目，我将按模块分析：

1. **用户认证模块 (Auth)**
   - 用户注册 `/api/auth/register`
   - 用户登录 `/api/auth/login`
   - 支持普通用户和商家两种角色
   - 使用JWT token认证

2. **用户管理模块 (User)**
   - 用户资料管理 `/api/users/profile`
   - 收货地址管理 `/api/users/addresses`
   - 收藏夹功能 `/api/users/favorites`
   - 用户评价管理 `/api/users/reviews`

3. **商品管理模块 (Product)**
   - 商品CRUD操作
   - 商品分类管理
   - 商品搜索功能
   - 商品评价系统
   - 库存管理

4. **购物车模块 (Cart)**
   - 添加商品到购物车
   - 查看购物车列表
   - 修改购物车商品数量
   - 删除购物车商品

5. **订单管理模块 (Order)**
   - 订单创建
   - 订单状态流转：
     - pending_payment (待付款)
     - paid (已付款)
     - shipped (已发货)
     - completed (已完成)
     - canceled (已取消)
   - 订单详情查询
   - 订单列表查询
   - 订单取消
   - 确认收货

6. **商家管理模块 (Merchant)**
   - 商家资料管理
   - 商品管理
   - 订单管理
   - 评价管理

7. **评价系统 (Review)**
   - 创建商品评价
   - 查看商品评价
   - 查看用户评价
   - 查看商家评价
   - 删除评价

8. **退货管理模块 (Return)**
   - 申请退货
   - 退货订单管理
   - 退货状态跟踪
   - 退款处理

9. **发票管理模块 (Invoice)**
   - 发票申请
   - 发票信息管理
   - 发票列表查询

10. **系统管理模块 (System)**
    - 系统日志记录
    - 权限管理
    - 角色管理

技术特点：
1. 使用Spring Boot框架
2. MyBatis-Plus作为ORM框架
3. 基于JWT的认证授权
4. RESTful API设计
5. 统一的响应格式
6. 全局异常处理
7. 事务管理
8. 数据验证

安全特性：
1. 基于角色的访问控制(RBAC)
2. JWT token认证
3. 密码加密存储
4. 接口权限控制

数据结构设计：
1. 用户相关：
   - auth_users (用户认证)
   - user_profiles (用户资料)
   - user_addresses (收货地址)
   - user_favorites (收藏夹)

2. 商品相关：
   - products (商品信息)
   - categories (商品分类)
   - product_images (商品图片)
   - product_reviews (商品评价)

3. 订单相关：
   - orders (订单主表)
   - order_items (订单项)
   - return_orders (退货订单)
   - invoices (发票信息)

4. 系统相关：
   - roles (角色表)
   - permissions (权限表)
   - role_permissions (角色权限关联)
   - system_logs (系统日志)

