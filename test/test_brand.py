"""品牌测试"""

from test_utils import test_utils

def test_create_brand():
    """测试创建品牌"""
    # 获取管理员token
    token = test_utils.test_login("admin")
    
    # 准备测试数据
    data = {
        "name": "测试品牌",
        "logo": "test-logo.jpg",
        "description": "这是一个测试品牌",
        "sort": 1,
        "status": 1
    }
    
    # 发送请求
    response = test_utils.make_request(
        "POST",
        "/api/brands",
        token=token,
        data=data
    )
    
    # 断言结果
    result = test_utils.assert_response(response)
    return result["data"]["id"]

def test_get_brand():
    """测试获取品牌详情"""
    # 获取管理员token
    token = test_utils.test_login("admin")
    
    # 先创建一个品牌
    brand_id = test_create_brand()
    
    # 发送请求
    response = test_utils.make_request(
        "GET",
        f"/api/brands/{brand_id}",
        token=token
    )
    
    # 断言结果
    test_utils.assert_response(response)

def test_list_brands():
    """测试获取品牌列表"""
    # 获取管理员token
    token = test_utils.test_login("admin")
    
    # 发送请求
    response = test_utils.make_request(
        "GET",
        "/api/brands",
        token=token
    )
    
    # 断言结果
    test_utils.assert_response(response) 