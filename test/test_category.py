"""分类测试"""

from test_utils import test_utils

def test_create_category():
    """测试创建分类"""
    # 获取管理员token
    token = test_utils.test_login("admin")
    
    # 准备测试数据
    data = {
        "name": "测试分类",
        "parentId": 0,
        "level": 1,
        "sort": 1,
        "status": 1
    }
    
    # 发送请求
    response = test_utils.make_request(
        "POST",
        "/api/categories",
        token=token,
        data=data
    )
    
    # 断言结果
    result = test_utils.assert_response(response)
    return result["data"]["id"]

def test_get_category():
    """测试获取分类详情"""
    # 获取管理员token
    token = test_utils.test_login("admin")
    
    # 先创建一个分类
    category_id = test_create_category()
    
    # 发送请求
    response = test_utils.make_request(
        "GET",
        f"/api/categories/{category_id}",
        token=token
    )
    
    # 断言结果
    test_utils.assert_response(response)

def test_list_categories():
    """测试获取分类列表"""
    # 获取管理员token
    token = test_utils.test_login("admin")
    
    # 发送请求
    response = test_utils.make_request(
        "GET",
        "/api/categories",
        token=token
    )
    
    # 断言结果
    test_utils.assert_response(response) 