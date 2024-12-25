# supermall-backend
企业云业务超市系统后端

## 项目介绍
`supermall-backend`是一套完整的电商系统的后端部分，基于 Spring Boot 3.x + JDK 17 实现。提供稳定、高性能的后端服务支持。

### 项目架构
后端项目采用经典三层架构（Controller-Service-Repository），基于 Spring Boot 技术栈

#### 后端技术栈
| 技术 | 说明 | 官网 |
| --- | --- | --- |
| Spring Boot | 容器+MVC框架 | https://spring.io/projects/spring-boot |
| Spring Security | 认证和授权框架 | https://spring.io/projects/spring-security |
| MyBatis-Plus | ORM框架 | https://baomidou.com/ |
| MySQL | 关系型数据库 | https://www.mysql.com/ |
| JWT | JWT登录支持 | https://github.com/jwtk/jjwt |
| Lombok | 简化对象封装工具 | https://projectlombok.org/ |
| Hibernate Validator | 验证框架 | http://hibernate.org/validator/ |

### 项目结构
```
supermall-backend/
  ├── config/                # 配置类
  ├── controller/            # 控制器层
  ├── service/              # 服务层
      ├── impl/             # 服务实现类
  ├── repository/           # 数据访问层
  ├── entity/              # 实体类
  ├── dto/                 # 数据传输对象
  ├── vo/                  # 视图对象
  ├── common/              # 公共类
      ├── exception/       # 异常处理
      ├── response/        # 统一响应
      └── utils/           # 工具类
  └── security/            # 安全相关
```

### 功能模块

#### 商品模块
- 商品管理
  - ✅ 商品增删改查
  - ✅ 商品分类管理
  - ❌ 品牌管理
  - ❌ 商品规格管理
  - ❌ 商品参数管理

#### 订单模块
- 订单管理
  - ✅ 订单创建与处理
  - ✅ 订单状态流转
  - ✅ 订单自动取消
  - ✅ 订单发货
    - ✅ 确认收货

#### 用户模块
- 用户管理
  - ✅ 用户注册登录
  - ✅ 修改密码
  - ✅ 更新头像
  - ❌ 会员等级管理
  - ❌ 积分系统

#### 购物车模块
- 购物车管理
  - ✅ 添加商品
  - ✅ 更新数量
  - ✅ 删除商品
  - ✅ 清空购物车
  - ✅ 全选/取消全选

#### 评论模块
- 评论管理
  - ✅ 商品评论
  - ✅ 评论列表
  - ✅ 评论权限控制

#### 支付模块
- 支付功能
  - ✅ 支付宝支付
  - ❌ 微信支付
  - ❌ 余额支付

#### 营销模块
- 营销功能
  - ❌ 秒杀系统
  - ❌ 优惠券系统
  - ❌ 促销活动
  - ❌ 广告系统

## 开发规范
1. 命名规范
   - 类名：大驼峰（如：ProductService）
   - 方法名：小驼峰（如：getProductList）
   - 变量名：小驼峰（如：productInfo）
   - 常量：大写下划线（如：MAX_COUNT）
   - 数据库表名：小写下划线（如：product_info）

2. Git提交规范
   - feat：新功能
   - fix：修复bug
   - docs：文档更新
   - style：代码格式
   - refactor：重构
   - test：测试用例
   - chore：构建过程或辅助工具的变动

## 环境搭建

### 开发工具

| 工具 | 说明 | 官网 |
| --- | --- | --- |
| IDEA | 开发IDE | https://www.jetbrains.com/idea/ |
| Navicat | 数据库管理 | http://www.navicat.com/ |
| Postman | API测试 | https://www.postman.com/ |

### 开发环境

| 环境 | 版本 | 下载 |
| --- | --- | --- |
| JDK | 17 | https://www.oracle.com/java/technologies/downloads/ |
| Maven | 3.8.x | https://maven.apache.org/ |
| MySQL | 8.0 | https://www.mysql.com/ |

## 项目运行
```bash
# 克隆项目
git clone https://github.com/jacl-coder/supermall-backend.git

# 进入项目目录
cd supermall-backend

# 编译项目
mvn clean install

# 运行项目
java -jar target/supermall-backend-0.0.1-SNAPSHOT.jar
```

## 注意事项
1. 遵循代码规范
2. 保持代码整洁
3. 编写完整的注释
4. 注意数据安全性
5. 编写单元测试
6. 做好性能优化
7. 保证接口幂等性

## 相关链接
- [项目文档](docs/index.md)
- [接口文档](docs/api.md)
- [常见问题](docs/faq.md)

## 许可证
[MIT](LICENSE)
