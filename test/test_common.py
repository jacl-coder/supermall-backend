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
    url = f"{BASE_URL}/api/auth/login"
    data = {
        "username": username,
        "password": password
    }
    try:
        response = requests.post(url, json=data)
        if response.status_code == 200:
            return json.loads(response.text)["data"]
        return None
    except Exception as e:
        print(f"登录失败: {str(e)}")
        return None

def get_admin_token():
    """获取管理员令牌"""
    return login_and_get_token(CONFIG['admin']['username'], CONFIG['admin']['password'])

def get_user_token():
    """获取普通用户令牌"""
    return login_and_get_token(CONFIG['user']['username'], CONFIG['user']['password'])

def print_response(response):
    """打印响应结果"""
    print(f"状态码: {response.status_code}")
    print(f"响应头: {response.headers}")
    print(f"响应内容: {response.text}") 