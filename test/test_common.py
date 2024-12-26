import requests
import json
import yaml
import os

def load_config():
    """加载测试配置"""
    config_path = os.path.join(os.path.dirname(__file__), 'config.yml')
    with open(config_path, 'r', encoding='utf-8') as f:
        return yaml.safe_load(f)['test']

CONFIG = load_config()
BASE_URL = CONFIG['base_url']

def login_and_get_token(username, password):
    """登录并获取token"""
    login_data = {
        'username': username,
        'password': password
    }
    try:
        response = requests.post(f'{BASE_URL}/api/auth/login', json=login_data)
        print(f"登录请求URL: {BASE_URL}/api/auth/login")
        print(f"登录请求数据: {login_data}")
        print(f"登录响应状态码: {response.status_code}")
        print(f"登录响应头: {response.headers}")
        print(f"登录响应内容: {response.text}")
        
        if response.status_code == 200:
            response_data = response.json()
            if 'data' in response_data and 'token' in response_data['data']:
                return response_data['data']['token']
            print(f"响应数据格式不正确: {response_data}")
        else:
            print(f"登录失败，状态码: {response.status_code}")
        return None
    except Exception as e:
        print(f"登录异常: {str(e)}")
        return None

def get_admin_token():
    """获取管理员token"""
    url = f"{BASE_URL}/api/auth/login"
    data = {
        "username": "admin",
        "password": "123456"
    }
    try:
        response = requests.post(url, json=data)
        if response.status_code == 200:
            return json.loads(response.text)["data"]["token"]
        return None
    except Exception as e:
        print(f"获取管理员token失败: {str(e)}")
        return None

def get_user_token():
    """获取普通用户令牌"""
    return login_and_get_token(CONFIG['user']['username'], CONFIG['user']['password'])

def print_response(response):
    """打印响应结果"""
    print(f"状态码: {response.status_code}")
    print(f"响应头: {response.headers}")
    print(f"响应内容: {response.text}") 