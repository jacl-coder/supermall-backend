import pytest
from test_common import BASE_URL, print_response, get_admin_token, get_merchant_token, get_user_token
import requests
import json

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
    
    # 4. 商家创建商品
    headers = {"Authorization": f"Bearer {merchant_token}"}
    product_data = {
        "name": "测试商品",
        "subtitle": "测试商品副标题",
        "categoryId": category_id,
        "brandId": brand_id,
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
    
    # 5. 用户创建订单
    headers = {"Authorization": f"Bearer {user_token}"}
    order_data = {
        "orderItems": [{
            "productId": product_id,
            "quantity": 1
        }],
        "receiverName": "测试用户",
        "receiverPhone": "13800138000",
        "receiverAddress": "测试地址"
    }
    response = requests.post(f"{BASE_URL}/api/orders", headers=headers, json=order_data)
    assert response.status_code == 200
    order = response.json()["data"]
    
    return {
        "admin_token": admin_token,
        "merchant_token": merchant_token,
        "user_token": user_token,
        "product_id": product_id,
        "order_no": order["orderNo"],
        "total_amount": order["totalAmount"]
    }

def test_user_create_payment(test_data):
    """测试用户创建支付记录"""
    headers = {"Authorization": f"Bearer {test_data['user_token']}"}
    data = {
        "orderNo": test_data["order_no"],
        "paymentMethod": "ALIPAY",
        "paymentAmount": test_data["total_amount"]
    }
    response = requests.post(f"{BASE_URL}/api/payments", headers=headers, json=data)
    print_response(response)
    assert response.status_code == 200
    assert response.json()["code"] == 200

def test_merchant_view_payments(test_data):
    """测试商家查看支付记录"""
    headers = {"Authorization": f"Bearer {test_data['merchant_token']}"}
    response = requests.get(f"{BASE_URL}/api/payments/merchant", headers=headers)
    print_response(response)
    assert response.status_code == 200
    assert response.json()["code"] == 200

def test_admin_view_all_payments(test_data):
    """测试管理员查看所有支付记录"""
    headers = {"Authorization": f"Bearer {test_data['admin_token']}"}
    response = requests.get(f"{BASE_URL}/api/payments/admin", headers=headers)
    print_response(response)
    assert response.status_code == 200
    assert response.json()["code"] == 200

def test_payment_flow(test_data):
    """测试完整支付流程"""
    # 1. 用户创建支付
    headers = {"Authorization": f"Bearer {test_data['user_token']}"}
    payment_data = {
        "orderNo": test_data["order_no"],
        "paymentMethod": "ALIPAY",
        "paymentAmount": test_data["total_amount"]
    }
    response = requests.post(f"{BASE_URL}/api/payments", headers=headers, json=payment_data)
    assert response.status_code == 200
    payment = response.json()["data"]
    
    # 2. 用户确认支付
    response = requests.put(
        f"{BASE_URL}/api/payments/{payment['id']}/status",
        headers=headers,
        json={"status": "PAID"}
    )
    assert response.status_code == 200
    
    # 3. 商家确认收款
    headers = {"Authorization": f"Bearer {test_data['merchant_token']}"}
    response = requests.put(
        f"{BASE_URL}/api/payments/{payment['id']}/confirm",
        headers=headers
    )
    assert response.status_code == 200 