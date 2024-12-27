"""商品测试"""

from test_utils import test_utils
from test_category import test_create_category
from test_brand import test_create_brand

def test_add_product():
    """测试添加商品"""
    # 获取商家token
    token = test_utils.test_login("merchant")
    
    # 先创建分类和品牌
    category_id = test_create_category()
    brand_id = test_create_brand()
    
    # 准备测试数据
    data = {
        "name": "测试商品",
        "categoryId": category_id,
        "brandId": brand_id,
        "price": 99.99,
        "stock": 100,
        "description": "这是一个测试商品",
        "status": 1
    }
    
    # 发送请求
    response = test_utils.make_request(
        "POST",
        "/api/products",
        token=token,
        data=data
    )
    
    # 断言结果
    result = test_utils.assert_response(response)
    return result["data"]["id"]

def test_get_product():
    """测试获取商品详情"""
    # 获取商家token
    token = test_utils.test_login("merchant")
    
    # 先添加一个商品
    product_id = test_add_product()
    
    # 发送请求
    response = test_utils.make_request(
        "GET",
        f"/api/products/{product_id}",
        token=token
    )
    
    # 断言结果
    test_utils.assert_response(response)

def test_update_product():
    """测试更新商品"""
    # 获取商家token
    token = test_utils.test_login("merchant")
    
    # 先添加一个商品
    product_id = test_add_product()
    
    # 准备测试数据
    data = {
        "name": "更新后的商品",
        "price": 199.99,
        "stock": 50,
        "description": "这是更新后的商品",
        "status": 1
    }
    
    # 发送请求
    response = test_utils.make_request(
        "PUT",
        f"/api/products/{product_id}",
        token=token,
        data=data
    )
    
    # 断言结果
    test_utils.assert_response(response)

def test_delete_product():
    """测试删除商品"""
    # 获取商家token
    token = test_utils.test_login("merchant")
    
    # 先添加一个商品
    product_id = test_add_product()
    
    # 发送请求
    response = test_utils.make_request(
        "DELETE",
        f"/api/products/{product_id}",
        token=token
    )
    
    # 断言结果
    test_utils.assert_response(response)

def test_list_products():
    """测试获取商品列表"""
    # 获取商家token
    token = test_utils.test_login("merchant")
    
    # 先添加一个商品
    test_add_product()
    
    # 发送请求
    response = test_utils.make_request(
        "GET",
        "/api/products",
        token=token
    )
    
    # 断言结果
    test_utils.assert_response(response) 