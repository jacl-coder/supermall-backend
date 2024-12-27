import pytest
from test_common import BASE_URL, print_response, get_admin_token, get_merchant_token, get_user_token
import requests

@pytest.fixture(scope="session")
def test_data():
    """创建测试数据"""
    print("\n准备测试数据...")
    
    # 1. 获取各角色token
    admin_token = get_admin_token()
    merchant_token = get_merchant_token()
    user_token = get_user_token()
    
    # 2. 管理员创建分类
    headers = {"Authorization": f"Bearer {admin_token}"}
    category_data = {
        "name": "测试分类",
        "parentId": 0,
        "level": 1,
        "sort": 1,
        "status": 1
    }
    response = requests.post(f"{BASE_URL}/api/categories", headers=headers, json=category_data)
    assert response.status_code == 200
    category_id = response.json()["data"]["id"]
    
    # 3. 管理员创建品牌
    brand_data = {
        "name": "测试品牌",
        "logo": "test-logo.jpg",
        "description": "这是一个测试品牌",
        "sort": 1,
        "status": 1
    }
    response = requests.post(f"{BASE_URL}/api/brands", headers=headers, json=brand_data)
    assert response.status_code == 200
    brand_id = response.json()["data"]["id"]
    
    return {
        "admin_token": admin_token,
        "merchant_token": merchant_token,
        "user_token": user_token,
        "category_id": category_id,
        "brand_id": brand_id
    }

def test_merchant_create_product(test_data):
    """测试商家创建商品"""
    headers = {"Authorization": f"Bearer {test_data['merchant_token']}"}
    data = {
        "name": "测试商品",
        "subtitle": "测试商品副标题",
        "categoryId": test_data["category_id"],
        "brandId": test_data["brand_id"],
        "mainImage": "test-image.jpg",
        "subImages": "image1.jpg,image2.jpg",
        "detail": "这是一个测试商品",
        "price": 99.99,
        "stock": 100,
        "status": 1
    }
    response = requests.post(f"{BASE_URL}/api/products", headers=headers, json=data)
    print_response(response)
    assert response.status_code == 200
    assert response.json()["code"] == 200
    return response.json()["data"]

def test_merchant_update_product(test_data):
    """测试商家更新商品"""
    product = test_merchant_create_product(test_data)
    headers = {"Authorization": f"Bearer {test_data['merchant_token']}"}
    data = {
        "price": 199.99,
        "stock": 50
    }
    response = requests.put(f"{BASE_URL}/api/products/{product['id']}", headers=headers, json=data)
    print_response(response)
    assert response.status_code == 200
    assert response.json()["code"] == 200

def test_merchant_view_products(test_data):
    """测试商家查看自己的商品"""
    headers = {"Authorization": f"Bearer {test_data['merchant_token']}"}
    response = requests.get(f"{BASE_URL}/api/products/merchant", headers=headers)
    print_response(response)
    assert response.status_code == 200
    assert response.json()["code"] == 200

def test_user_view_products(test_data):
    """测试用户浏览商品"""
    headers = {"Authorization": f"Bearer {test_data['user_token']}"}
    response = requests.get(f"{BASE_URL}/api/products", headers=headers)
    print_response(response)
    assert response.status_code == 200
    assert response.json()["code"] == 200

def test_admin_manage_products(test_data):
    """测试管理员管理商品"""
    headers = {"Authorization": f"Bearer {test_data['admin_token']}"}
    
    # 查看所有商品
    response = requests.get(f"{BASE_URL}/api/products/admin", headers=headers)
    assert response.status_code == 200
    
    # 审核商品
    product = test_merchant_create_product(test_data)
    response = requests.put(
        f"{BASE_URL}/api/products/{product['id']}/audit",
        headers=headers,
        json={"status": 1, "reason": "审核通过"}
    )
    assert response.status_code == 200 