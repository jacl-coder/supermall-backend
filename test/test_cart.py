import pytest
from test_common import BASE_URL, print_response, get_admin_token
import requests
import json

@pytest.fixture(scope="session", autouse=True)
def setup_test_data():
    """设置测试数据"""
    print("\n设置测试数据...")
    
    try:
        # 创建测试商品
        headers = {"Authorization": f"Bearer {get_admin_token()}"}
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
        print(f"创建测试商品响应: {response.text}")
        assert response.status_code == 200
        response_data = json.loads(response.text)
        assert response_data["code"] == 200
        
        # 确保商品创建成功
        product_id = response_data["data"]["id"]
        response = requests.get(f"{BASE_URL}/api/products/{product_id}")
        assert response.status_code == 200
        print(f"获取测试商品响应: {response.text}")
        
    except Exception as e:
        print(f"设置测试数据失败: {str(e)}")
        pytest.fail(str(e))

@pytest.fixture
def new_cart_item():
    """创建一个购物车项并返回数据"""
    url = f"{BASE_URL}/api/cart"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "productId": 1,
        "quantity": 2
    }
    try:
        response = requests.post(url, headers=headers, json=data)
        assert response.status_code == 200
        response_data = json.loads(response.text)
        assert response_data["code"] == 200
        assert response_data["data"] is not None
        return response_data["data"]
    except Exception as e:
        pytest.fail(f"创建购物车项失败: {str(e)}")

def test_add_to_cart():
    """测试添加商品到购物车"""
    print("\n测试添加商品到购物车...")
    url = f"{BASE_URL}/api/cart"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "productId": 1,
        "quantity": 2
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

def test_get_cart_list():
    """测试获取购物车列表"""
    print("\n测试获取购物车列表...")
    url = f"{BASE_URL}/api/cart"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    try:
        response = requests.get(url, headers=headers)
        print_response(response)
        assert response.status_code == 200
        response_data = json.loads(response.text)
        print(f"响应数据: {response_data}")
        assert response_data["code"] == 200
        assert isinstance(response_data["data"], list)
    except Exception as e:
        print(f"发生错误: {str(e)}")
        pytest.fail(str(e))

def test_update_cart_quantity(new_cart_item):
    """测试更新购物车商品数量"""
    print("\n测试更新购物车商品数量...")
    url = f"{BASE_URL}/api/cart/{new_cart_item['id']}/quantity"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    params = {"quantity": 3}
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

def test_update_cart_checked(new_cart_item):
    """测试更新购物车商品选中状态"""
    print("\n测试更新购物车商品选中状态...")
    url = f"{BASE_URL}/api/cart/{new_cart_item['id']}/checked"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    params = {"checked": False}
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

def test_delete_cart_item(new_cart_item):
    """测试删除购物车商品"""
    print("\n测试删除购物车商品...")
    url = f"{BASE_URL}/api/cart/{new_cart_item['id']}"
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

def test_clear_cart():
    """测试清空购物车"""
    print("\n测试清空购物车...")
    url = f"{BASE_URL}/api/cart/clear"
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

def test_cart_flow():
    """测试购物车完整流程"""
    # 添加商品到购物车
    test_add_to_cart()
    
    # 获取购物车列表
    test_get_cart_list()
    
    # 创建一个购物车项用于测试
    cart_item = new_cart_item()
    
    # 更新购物车商品数量
    test_update_cart_quantity(cart_item)
    
    # 更新购物车商品选中状态
    test_update_cart_checked(cart_item)
    
    # 删除购物车商品
    test_delete_cart_item(cart_item)
    
    # 清空购物车
    test_clear_cart() 