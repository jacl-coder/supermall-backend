import pytest
from test_common import BASE_URL, print_response, get_admin_token
import requests
import json

@pytest.fixture
def new_order():
    """创建一个测试订单并返回订单数据"""
    url = f"{BASE_URL}/api/orders"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "userId": 1,
        "receiverName": "张三",
        "receiverPhone": "13800138000",
        "receiverAddress": "北京市朝阳区xxx街道",
        "totalAmount": 299.99,
        "payAmount": 299.99,
        "payType": 1,  # 1-支付宝 2-微信
        "status": 0,   # 0-待付款
        "orderItems": [
            {
                "productId": 1,
                "productName": "测试商品",
                "productImage": "test.jpg",
                "quantity": 2,
                "price": 149.99
            }
        ]
    }
    try:
        response = requests.post(url, headers=headers, json=data)
        assert response.status_code == 200
        response_data = json.loads(response.text)
        assert response_data["code"] == 200
        assert response_data["data"] is not None
        return response_data["data"]
    except Exception as e:
        pytest.fail(f"创建测试订单失败: {str(e)}")

def test_create_order():
    """测试创建订单"""
    print("\n测试创建订单...")
    url = f"{BASE_URL}/api/orders"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "userId": 1,
        "receiverName": "张三",
        "receiverPhone": "13800138000",
        "receiverAddress": "北京市朝阳区xxx街道",
        "totalAmount": 299.99,
        "payAmount": 299.99,
        "payType": 1,
        "status": 0,
        "orderItems": [
            {
                "productId": 1,
                "productName": "测试商品",
                "productImage": "test.jpg",
                "quantity": 2,
                "price": 149.99
            }
        ]
    }
    try:
        response = requests.post(url, headers=headers, json=data)
        print_response(response)
        assert response.status_code == 200
        response_data = json.loads(response.text)
        print(f"响应数据: {response_data}")
        assert response_data["code"] == 200
        assert response_data["data"] is not None
    except Exception as e:
        print(f"发生错误: {str(e)}")
        pytest.fail(str(e))

def test_get_order_list():
    """测试获取订单列表"""
    print("\n测试获取订单列表...")
    url = f"{BASE_URL}/api/orders"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    try:
        response = requests.get(url, headers=headers)
        print_response(response)
        assert response.status_code == 200
        response_data = json.loads(response.text)
        print(f"响应数据: {response_data}")
        assert response_data["code"] == 200
        assert isinstance(response_data["data"], dict)
        # 验证分页字段
        data = response_data["data"]
        assert "records" in data
        assert "total" in data
        assert "size" in data
        assert "current" in data
    except Exception as e:
        print(f"发生错误: {str(e)}")
        pytest.fail(str(e))

def test_get_order_detail(new_order):
    """测试获取订单详情"""
    print("\n测试获取订单详情...")
    url = f"{BASE_URL}/api/orders/{new_order['id']}"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    try:
        response = requests.get(url, headers=headers)
        print_response(response)
        assert response.status_code == 200
        response_data = json.loads(response.text)
        print(f"响应数据: {response_data}")
        assert response_data["code"] == 200
        assert response_data["data"] is not None
        
        # 验证返回的数据
        data = response_data["data"]
        assert data["id"] == new_order["id"]
        assert data["userId"] == new_order["userId"]
        assert data["receiverName"] == new_order["receiverName"]
        assert len(data["orderItems"]) > 0
    except Exception as e:
        print(f"发生错误: {str(e)}")
        pytest.fail(str(e))

def test_update_order_status(new_order):
    """测试更新订单状态"""
    print("\n测试更新订单状态...")
    url = f"{BASE_URL}/api/orders/{new_order['id']}/status"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    params = {"status": 1}  # 1-已付款
    try:
        response = requests.put(url, headers=headers, params=params)
        print_response(response)
        assert response.status_code == 200
        response_data = json.loads(response.text)
        print(f"响应数据: {response_data}")
        assert response_data["code"] == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        pytest.fail(str(e))

def test_delete_order(new_order):
    """测试删除订单"""
    print("\n测试删除订单...")
    url = f"{BASE_URL}/api/orders/{new_order['id']}"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    try:
        response = requests.delete(url, headers=headers)
        print_response(response)
        assert response.status_code == 200
        response_data = json.loads(response.text)
        print(f"响应数据: {response_data}")
        assert response_data["code"] == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        pytest.fail(str(e))

def create_test_order():
    """创建一个测试订单并返回订单数据"""
    url = f"{BASE_URL}/api/orders"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "userId": 1,
        "receiverName": "张三",
        "receiverPhone": "13800138000",
        "receiverAddress": "北京市朝阳区xxx街道",
        "totalAmount": 299.99,
        "payAmount": 299.99,
        "payType": 1,
        "status": 0,
        "orderItems": [
            {
                "productId": 1,
                "productName": "测试商品",
                "productImage": "test.jpg",
                "quantity": 2,
                "price": 149.99
            }
        ]
    }
    try:
        response = requests.post(url, headers=headers, json=data)
        assert response.status_code == 200
        response_data = json.loads(response.text)
        assert response_data["code"] == 200
        assert response_data["data"] is not None
        return response_data["data"]
    except Exception as e:
        raise Exception(f"创建测试订单失败: {str(e)}")

def test_order_flow():
    """测试订单完整流程"""
    # 创建订单
    test_create_order()
    
    # 获取订单列表
    test_get_order_list()
    
    # 创建一个订单用于详情和更新测试
    order = create_test_order()
    
    # 获取订单详情
    test_get_order_detail(order)
    
    # 更新订单状态
    test_update_order_status(order)
    
    # 删除订单
    test_delete_order(order) 