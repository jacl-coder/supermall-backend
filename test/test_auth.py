import pytest
from test_utils import test_utils
from test_config import TEST_DATA, get_config

def test_register():
    """测试用户注册"""
    # 清理测试数据
    test_utils.cleanup_test_user(TEST_DATA["admin"]["username"])
    
    # 准备测试数据
    data = TEST_DATA["admin"]
    
    # 发送请求
    response = test_utils.make_request("POST", "/api/auth/register", data=data)
    
    # 断言结果
    result = test_utils.assert_response(response)
    assert "data" in result
    assert result["data"]["username"] == data["username"]

@test_utils.retry_on_failure()
def test_login():
    """测试用户登录"""
    # 准备测试数据
    config = get_config()
    data = {
        "username": config["user"]["username"],
        "password": config["user"]["password"]
    }
    
    # 发送请求
    response = test_utils.make_request("POST", "/api/auth/login", data=data)
    
    # 断言结果
    result = test_utils.assert_response(response)
    assert "data" in result
    assert "token" in result["data"]
    return result["data"]["token"]

def test_refresh_token():
    """测试刷新token"""
    # 获取token
    token = test_login()
    
    # 发送请求
    response = test_utils.make_request(
        "POST", 
        "/api/auth/refresh",
        token=token,
        data={"token": token}
    )
    
    # 断言结果
    result = test_utils.assert_response(response)
    assert "data" in result
    assert "token" in result["data"] 