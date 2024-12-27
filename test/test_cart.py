import pytest
from test_common import BASE_URL, print_response, get_merchant_token, get_user_token
import requests

@pytest.fixture(scope="session")
def test_data():
    """创建测试数据"""
    print("\n准备测试数据...")
    
    # 1. 获取商家和用户token
    merchant_token = get_merchant_token()
    user_token = get_user_token()
    
    # 2. 商家创建商品
    headers = {"Authorization": f"Bearer {merchant_token}"}
    product_data = {
        "name": "测试商品",
        "subtitle": "测试商品副标题",
        "categoryId": 1,
        "brandId": 1,
        "mainImage": "test-image.jpg",
        "subImages": "image1.jpg,image2.jpg",
        "detail": "这是一个测试商品",
        "price": 99.99,
        "stock": 100,
        "status": 1
    }
    response = requests.post(f"{BASE_URL}/api/products", headers=headers, json=product_data)
    assert response.status_code == 200
    product_id = response.json()["data"]["id"]
    
    return {
        "user_token": user_token,
        "product_id": product_id
    }

def test_add_to_cart(test_data):
    """测试添加商品到购物车"""
    headers = {"Authorization": f"Bearer {test_data['user_token']}"}
    data = {
        "productId": test_data["product_id"],
        "quantity": 1
    }
    response = requests.post(f"{BASE_URL}/api/cart", headers=headers, json=data)
    print_response(response)
    assert response.status_code == 200
    assert response.json()["code"] == 200

def test_update_cart(test_data):
    """测试更新购物车商品数量"""
    headers = {"Authorization": f"Bearer {test_data['user_token']}"}
    data = {
        "productId": test_data["product_id"],
        "quantity": 2
    }
    response = requests.put(f"{BASE_URL}/api/cart", headers=headers, json=data)
    print_response(response)
    assert response.status_code == 200
    assert response.json()["code"] == 200

def test_view_cart(test_data):
    """测试查看购物车"""
    headers = {"Authorization": f"Bearer {test_data['user_token']}"}
    response = requests.get(f"{BASE_URL}/api/cart", headers=headers)
    print_response(response)
    assert response.status_code == 200
    assert response.json()["code"] == 200

def test_delete_from_cart(test_data):
    """测试从购物车删除商品"""
    headers = {"Authorization": f"Bearer {test_data['user_token']}"}
    response = requests.delete(f"{BASE_URL}/api/cart/{test_data['product_id']}", headers=headers)
    print_response(response)
    assert response.status_code == 200
    assert response.json()["code"] == 200

def test_cart_to_order(test_data):
    """测试购物车下单"""
    # 1. 添加商品到购物车
    test_add_to_cart(test_data)
    
    # 2. 从购物车创建订单
    headers = {"Authorization": f"Bearer {test_data['user_token']}"}
    data = {
        "cartIds": [1],  # 购物车项ID列表
        "receiverName": "测试用户",
        "receiverPhone": "13800138000",
        "receiverAddress": "测试地址"
    }
    response = requests.post(f"{BASE_URL}/api/orders/cart", headers=headers, json=data)
    print_response(response)
    assert response.status_code == 200
    assert response.json()["code"] == 200 