import os
import requests
from typing import Dict

# 基础配置
BASE_URL = os.getenv('API_BASE_URL', 'http://localhost:8080')

class TestUsers:
    """测试用户配置"""
    ADMIN = {
        "username": "admin",
        "password": "123456",
        "email": "admin@test.com",
        "roleCode": "ADMIN"
    }
    
    MERCHANT = {
        "username": "testmerchant",
        "password": "123456",
        "email": "merchant@test.com",
        "roleCode": "MERCHANT"
    }
    
    USER = {
        "username": "testuser",
        "password": "123456",
        "email": "user@test.com",
        "roleCode": "USER"
    }

def get_token(user_data: Dict[str, str]) -> str:
    """获取用户token"""
    # 1. 尝试注册（如果用户不存在）
    try:
        response = requests.post(f"{BASE_URL}/api/auth/register", json=user_data)
        if response.status_code != 200:
            print(f"注册失败或用户已存在: {response.text}")
    except Exception as e:
        print(f"注册过程出错: {str(e)}")
    
    # 2. 登录获取token
    login_data = {
        "username": user_data["username"],
        "password": user_data["password"]
    }
    response = requests.post(f"{BASE_URL}/api/auth/login", json=login_data)
    assert response.status_code == 200, f"登录失败: {response.text}"
    return response.json()["data"]["token"]

def get_admin_token() -> str:
    """获取管理员token"""
    return get_token(TestUsers.ADMIN)

def get_merchant_token() -> str:
    """获取商家token"""
    return get_token(TestUsers.MERCHANT)

def get_user_token() -> str:
    """获取普通用户token"""
    return get_token(TestUsers.USER)

def print_response(response: requests.Response) -> None:
    """打印响应内容"""
    print(f"\n状态码: {response.status_code}")
    print(f"响应头: {dict(response.headers)}")
    print(f"响应体: {response.text}") 