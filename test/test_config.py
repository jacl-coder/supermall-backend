"""测试配置"""

# 测试环境配置
TEST_ENV = {
    "dev": {
        "base_url": "http://localhost:8080",
        "admin": {
            "username": "admin",
            "password": "123456"
        },
        "merchant": {
            "username": "testmerchant",
            "password": "123456"
        },
        "user": {
            "username": "testuser",
            "password": "123456"
        }
    },
    "test": {
        "base_url": "http://test-api.example.com",
        "admin": {
            "username": "admin",
            "password": "test123"
        },
        "merchant": {
            "username": "testmerchant",
            "password": "test123"
        },
        "user": {
            "username": "testuser",
            "password": "test123"
        }
    },
    "prod": {
        "base_url": "https://api.example.com",
        "admin": {
            "username": "admin",
            "password": "prod123"
        },
        "merchant": {
            "username": "merchant",
            "password": "prod123"
        },
        "user": {
            "username": "user",
            "password": "prod123"
        }
    }
}

# 默认使用开发环境
CURRENT_ENV = "dev"

# 获取当前环境配置
def get_config():
    return TEST_ENV[CURRENT_ENV]

# 测试数据配置
TEST_DATA = {
    "category": {
        "name": "测试分类",
        "parentId": 0,
        "level": 1,
        "sort": 1,
        "status": 1
    },
    "brand": {
        "name": "测试品牌",
        "logo": "test-logo.jpg",
        "description": "这是一个测试品牌",
        "sort": 1,
        "status": 1
    },
    "product": {
        "name": "测试商品",
        "subtitle": "测试商品副标题",
        "mainImage": "test-image.jpg",
        "subImages": "image1.jpg,image2.jpg",
        "detail": "这是一个测试商品",
        "price": 99.99,
        "stock": 100,
        "status": 1
    },
    "order": {
        "receiverName": "测试用户",
        "receiverPhone": "13800138000",
        "receiverAddress": "测试地址"
    }
}

# 测试断言配置
ASSERT_CONFIG = {
    "timeout": 10,  # 请求超时时间（秒）
    "retry": 3,     # 失败重试次数
    "interval": 1   # 重试间隔（秒）
} 