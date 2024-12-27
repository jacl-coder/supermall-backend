import time
import requests
from typing import Dict, Any, Optional
from functools import wraps
from test_config import get_config, ASSERT_CONFIG

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

# 创建工具类实例
test_utils = TestUtils() 