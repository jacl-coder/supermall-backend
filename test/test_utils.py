import time
import requests
from typing import Dict, Any, Optional
from functools import wraps
from test_config import get_config, ASSERT_CONFIG, DB_CONFIG
import mysql.connector
import os
import json

class TestUtils:
    """测试工具类"""
    
    @staticmethod
    def retry_on_failure(max_retries: int = ASSERT_CONFIG["retry"], 
                        interval: int = ASSERT_CONFIG["interval"]):
        """失败重试装饰器"""
        def decorator(func):
            @wraps(func)
            def wrapper(*args, **kwargs):
                for i in range(max_retries):
                    try:
                        return func(*args, **kwargs)
                    except AssertionError as e:
                        if i == max_retries - 1:
                            raise
                        print(f"断言失败，{interval}秒后重试: {str(e)}")
                        time.sleep(interval)
            return wrapper
        return decorator

    @staticmethod
    def make_request(method: str, 
                    endpoint: str, 
                    token: Optional[str] = None, 
                    data: Optional[Dict] = None,
                    timeout: int = ASSERT_CONFIG["timeout"]) -> requests.Response:
        """发送HTTP请求"""
        url = f"{get_config()['base_url']}{endpoint}"
        headers = {}
        if token:
            headers["Authorization"] = f"Bearer {token}"
        
        print(f"\n发送请求:")
        print(f"URL: {url}")
        print(f"Method: {method}")
        print(f"Headers: {headers}")
        print(f"Data: {data}")
        
        try:
            response = requests.request(
                method=method,
                url=url,
                headers=headers,
                json=data,
                timeout=timeout
            )
            
            print(f"响应状态码: {response.status_code}")
            print(f"响应头: {dict(response.headers)}")
            print(f"响应体: {response.text}")
            
            return response
        except requests.exceptions.Timeout:
            raise Exception(f"请求超时: {url}")
        except requests.exceptions.RequestException as e:
            raise Exception(f"请求失败: {url}, 错误: {str(e)}")

    @staticmethod
    def assert_response(response: requests.Response, 
                       expected_status: int = 200,
                       expected_code: int = 200) -> Dict[str, Any]:
        """断言响应结果"""
        # 打印响应信息，方便调试
        print(f"\n状态码: {response.status_code}")
        print(f"响应头: {dict(response.headers)}")
        print(f"响应体: {response.text}")
        
        # 断言状态码
        assert response.status_code == expected_status, \
            f"HTTP状态码错误: 期望 {expected_status}, 实际 {response.status_code}"
        
        # 解析响应数据
        try:
            data = response.json()
        except ValueError:
            raise AssertionError("响应不是有效的JSON格式")
        
        # 断言业务状态码
        assert data["code"] == expected_code, \
            f"业务状态码错误: 期望 {expected_code}, 实际 {data.get('code')}"
        
        return data

    @staticmethod
    def clean_test_data():
        """清理测试数据"""
        # TODO: 实现测试数据清理逻辑
        pass

    @staticmethod
    def cleanup_test_user(username: str):
        """清理测试用户数据"""
        try:
            conn = mysql.connector.connect(
                host="localhost",
                user="root",
                password="lai20031024",
                database="super_mall"
            )
            cursor = conn.cursor()
            
            # 删除用户角色关联
            cursor.execute("DELETE FROM user_role WHERE user_id IN (SELECT id FROM user WHERE username = %s)", (username,))
            
            # 删除用户
            cursor.execute("DELETE FROM user WHERE username = %s", (username,))
            
            conn.commit()
        except Exception as e:
            print(f"清理测试用户数据失败: {e}")
        finally:
            if 'cursor' in locals():
                cursor.close()
            if 'conn' in locals():
                conn.close()

    @staticmethod
    def test_login(role: str = "user") -> str:
        """测试登录
        Args:
            role: 用户角色，可选值：admin, merchant, user
        Returns:
            str: token
        """
        config = get_config()
        
        # 根据角色选择用户
        if role.lower() == "admin":
            user = config["admin"]
        elif role.lower() == "merchant":
            user = config["merchant"]
        else:
            user = config["user"]
        
        # 发送请求
        response = TestUtils.make_request(
            "POST",
            "/api/auth/login",
            data={
                "username": user["username"],
                "password": user["password"]
            }
        )
        
        # 断言结果
        result = TestUtils.assert_response(response)
        return result["data"]["token"]

    @staticmethod
    def init_test_data():
        """初始化测试数据"""
        try:
            conn = mysql.connector.connect(
                host=DB_CONFIG["host"],
                user=DB_CONFIG["user"],
                password=DB_CONFIG["password"],
                database=DB_CONFIG["database"]
            )
            cursor = conn.cursor()
            
            # 1. 清理旧数据
            cursor.execute("DELETE FROM user_role")
            cursor.execute("ALTER TABLE user_role AUTO_INCREMENT = 1")
            cursor.execute("DELETE FROM role")
            cursor.execute("ALTER TABLE role AUTO_INCREMENT = 1")
            cursor.execute("DELETE FROM user")
            cursor.execute("ALTER TABLE user AUTO_INCREMENT = 1")
            
            # 2. 创建角色
            roles = [
                ("ADMIN", "管理员"),
                ("MERCHANT", "商家"),
                ("USER", "用户")
            ]
            for role in roles:
                cursor.execute(
                    "INSERT INTO role (code, name, status) VALUES (%s, %s, 1)",
                    role
                )
            
            # 3. 创建用户
            users = [
                ("admin", "$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG", "admin@example.com"),
                ("merchant", "$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG", "merchant@example.com"),
                ("testuser", "$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG", "user@example.com")
            ]
            for user in users:
                cursor.execute(
                    "INSERT INTO user (username, password, email, status) VALUES (%s, %s, %s, 1)",
                    user
                )
            
            # 4. 关联用户和角色
            cursor.execute("SELECT id, code FROM role")
            roles = {role[1]: role[0] for role in cursor.fetchall()}
            
            cursor.execute("SELECT id, username FROM user")
            users = {user[1]: user[0] for user in cursor.fetchall()}
            
            user_roles = [
                (users["admin"], roles["ADMIN"]),
                (users["merchant"], roles["MERCHANT"]),
                (users["testuser"], roles["USER"])
            ]
            for user_role in user_roles:
                cursor.execute(
                    "INSERT INTO user_role (user_id, role_id) VALUES (%s, %s)",
                    user_role
                )
            
            conn.commit()
            print("测试数据初始化成功")
            
        except Exception as e:
            print(f"初始化测试数据失败: {e}")
            if 'conn' in locals():
                conn.rollback()
        finally:
            if 'cursor' in locals():
                cursor.close()
            if 'conn' in locals():
                conn.close()

# 创建工具实例
test_utils = TestUtils() 