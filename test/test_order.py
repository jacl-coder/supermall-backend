import pytest
from test_common import BASE_URL, print_response, get_admin_token, get_merchant_token, get_user_token
import requests
from test_utils import test_utils, get_config

@pytest.fixture(scope="session")
def test_data():
    """创建测试数据"""
    print("\n准备测试数据...")
    
    # 1. 获取各角色token
    admin_token = test_utils.make_request(
        "POST", 
        "/api/auth/login",
        data=get_config()["admin"]
    ).json()["data"]["token"]
    
    merchant_token = test_utils.make_request(
        "POST", 
        "/api/auth/login",
        data=get_config()["merchant"]
    ).json()["data"]["token"]
    
    user_token = test_utils.make_request(
        "POST", 
        "/api/auth/login",
        data=get_config()["user"]
    ).json()["data"]["token"]
    
    # 2. 管理员创建分类
    response = test_utils.make_request(
        "POST",
        "/api/categories",
        token=admin_token,
        data=TEST_DATA["category"]
    )
    test_utils.assert_response(response)
    category_id = response.json()["data"]["id"]
    
    # 3. 管理员创建品牌
    response = test_utils.make_request(
        "POST",
        "/api/brands",
        token=admin_token,
        data=TEST_DATA["brand"]
    )
    test_utils.assert_response(response)
    brand_id = response.json()["data"]["id"]
    
    # 4. 商家创建商品
    product_data = TEST_DATA["product"].copy()
    product_data.update({
        "categoryId": category_id,
        "brandId": brand_id
    })
    response = test_utils.make_request(
        "POST",
        "/api/products",
        token=merchant_token,
        data=product_data
    )
    test_utils.assert_response(response)
    product_id = response.json()["data"]["id"]
    
    return {
        "admin_token": admin_token,
        "merchant_token": merchant_token,
        "user_token": user_token,
        "product_id": product_id
    }

def test_user_create_order(test_data):
    """测试用户创建订单"""
    headers = {"Authorization": f"Bearer {test_data['user_token']}"}
    data = {
        "orderItems": [{
            "productId": test_data["product_id"],
            "quantity": 1
        }],
        "receiverName": "测试用户",
        "receiverPhone": "13800138000",
        "receiverAddress": "测试地址"
    }
    response = requests.post(f"{BASE_URL}/api/orders", headers=headers, json=data)
    print_response(response)
    assert response.status_code == 200
    assert response.json()["code"] == 200
    return response.json()["data"]

def test_user_view_orders(test_data):
    """测试用户查看自己的订单"""
    headers = {"Authorization": f"Bearer {test_data['user_token']}"}
    response = requests.get(f"{BASE_URL}/api/orders", headers=headers)
    print_response(response)
    assert response.status_code == 200
    assert response.json()["code"] == 200

def test_merchant_view_orders(test_data):
    """测试商家查看订单"""
    headers = {"Authorization": f"Bearer {test_data['merchant_token']}"}
    response = requests.get(f"{BASE_URL}/api/orders/merchant", headers=headers)
    print_response(response)
    assert response.status_code == 200
    assert response.json()["code"] == 200

def test_admin_view_all_orders(test_data):
    """测试管理员查看所有订单"""
    headers = {"Authorization": f"Bearer {test_data['admin_token']}"}
    response = requests.get(f"{BASE_URL}/api/orders/admin", headers=headers)
    print_response(response)
    assert response.status_code == 200
    assert response.json()["code"] == 200

def test_order_flow(test_data):
    """测试完整订单流程"""
    # 1. 用户创建订单
    order = test_user_create_order(test_data)
    headers = {"Authorization": f"Bearer {test_data['user_token']}"}
    
    # 2. 用户支付订单
    response = requests.put(
        f"{BASE_URL}/api/orders/{order['id']}/pay",
        headers=headers
    )
    assert response.status_code == 200
    
    # 3. 商家发货
    headers = {"Authorization": f"Bearer {test_data['merchant_token']}"}
    response = requests.put(
        f"{BASE_URL}/api/orders/{order['id']}/ship",
        headers=headers,
        json={"trackingNo": "SF1234567890"}
    )
    assert response.status_code == 200
    
    # 4. 用户确认收货
    headers = {"Authorization": f"Bearer {test_data['user_token']}"}
    response = requests.put(
        f"{BASE_URL}/api/orders/{order['id']}/receive",
        headers=headers
    )
    assert response.status_code == 200 