"""测试配置"""

import os

# 测试配置
ASSERT_CONFIG = {
    "retry": 3,        # 重试次数
    "interval": 1,     # 重试间隔（秒）
    "timeout": 10      # 请求超时时间（秒）
}

# 数据库配置
DB_CONFIG = {
    "host": "localhost",
    "port": 3306,
    "database": "super_mall",
    "user": "root",
    "password": "lai20031024"
}

# 测试数据
TEST_DATA = {
    "admin": {
        "username": "admin",
        "password": "123456"
    },
    "merchant": {
        "username": "merchant",
        "password": "123456"
    },
    "user": {
        "username": "testuser",
        "password": "123456"
    }
}

def get_config():
    """获取测试配置"""
    return {
        "base_url": "http://localhost:8080",
        "admin": TEST_DATA["admin"],
        "merchant": TEST_DATA["merchant"],
        "user": TEST_DATA["user"],
        "db": DB_CONFIG
    } 