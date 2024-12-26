import requests
import json
import time

BASE_URL = 'http://localhost:8081'

def get_admin_token():
    """获取管理员令牌"""
    url = f"{BASE_URL}/api/auth/login"
    data = {
        "username": "admin",
        "password": "123456"
    }
    try:
        response = requests.post(url, json=data)
        if response.status_code == 200:
            return json.loads(response.text)["data"]
        return None
    except Exception as e:
        print(f"获取管理员令牌失败: {str(e)}")
        return None

def get_user_token():
    """获取普通用户令牌"""
    url = f"{BASE_URL}/api/auth/login"
    data = {
        "username": "testuser2",
        "password": "123456"
    }
    try:
        response = requests.post(url, json=data)
        if response.status_code == 200:
            return json.loads(response.text)["data"]
        return None
    except Exception as e:
        print(f"获取用户令牌失败: {str(e)}")
        return None

def print_response(response):
    """打印响应结果"""
    print(f"状态码: {response.status_code}")
    print(f"响应内容: {response.text}") 