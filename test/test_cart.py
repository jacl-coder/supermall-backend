import pytest
import requests
from test_common import BASE_URL, login_and_get_token, print_response

admin_token = None
user_token = None

def setup_module():
    """测试模块初始化"""
    global admin_token, user_token
    # 管理员登录
    admin_token = login_and_get_token('admin', '123456')
    if not admin_token:
        pytest.fail("管理员登录失败")
    # 普通用户登录
    user_token = login_and_get_token('testuser', '123456')
    if not user_token:
        pytest.fail("用户登录失败")

def create_test_product():
    """创建测试商品"""
    headers = {'Authorization': f'Bearer {admin_token}'}
    
    product_data = {
        'name': '测试商品',
        'subtitle': '测试商品副标题',
        'categoryId': 1,
        'brandId': 1,
        'mainImage': 'test.jpg',
        'subImages': '["test1.jpg", "test2.jpg"]',
        'detail': '测试商品详情',
        'price': 99.99,
        'stock': 100
    }
    
    print(f"创建商品请求数据: {product_data}")
    print(f"请求头: {headers}")
    
    response = requests.post(f'{BASE_URL}/api/products', json=product_data, headers=headers)
    print_response(response)
    
    if response.status_code != 200:
        print(f"创建商品失败，状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
        return None
        
    try:
        response_data = response.json()
        print(f"创建商品响应数据: {response_data}")
        if 'data' not in response_data:
            print(f"响应格式错误: {response_data}")
            return None
        return response_data['data']
    except Exception as e:
        print(f"解析响应数据失败: {str(e)}")
        return None

def test_add_to_cart():
    """测试添加商品到购物车"""
    try:
        # 1. 创建测试商品
        product = create_test_product()
        if not product:
            pytest.fail("创建商品失败")
            
        print(f"创建的商品信息: {product}")
        
        # 2. 添加商品到购物车
        headers = {'Authorization': f'Bearer {user_token}'}
        cart_data = {
            'productId': product['id'],
            'quantity': 2
        }
        print(f"添加购物车请求数据: {cart_data}")
        
        response = requests.post(f'{BASE_URL}/api/cart', json=cart_data, headers=headers)
        print_response(response)
        
        if response.status_code != 200:
            print(f"添加购物车失败，响应内容: {response.text}")
            pytest.fail(f"添加购物车失败: {response.text}")
            
        # 3. 验证购物车列表
        response = requests.get(f'{BASE_URL}/api/cart', headers=headers)
        print(f"获取购物车列表响应: {response.text}")
        
        assert response.status_code == 200, f"获取购物车列表失败: {response.text}"
        cart_list = response.json()['data']
        assert len(cart_list) > 0, "购物车为空"
        
        cart_item = next((item for item in cart_list if item['productId'] == product['id']), None)
        assert cart_item is not None, "未找到添加的商品"
        assert cart_item['quantity'] == 2, "商品数量不正确"
        
    except Exception as e:
        pytest.fail(f"测试失败: {str(e)}")

def test_update_cart_quantity():
    """测试更新购物车商品数量"""
    headers = {'Authorization': f'Bearer {user_token}'}
    
    # 1. 获取购物车列表
    response = requests.get(f'{BASE_URL}/api/cart', headers=headers)
    assert response.status_code == 200
    cart_list = response.json()['data']
    assert len(cart_list) > 0
    cart_item = cart_list[0]
    
    # 2. 更新数量
    quantity_data = {'quantity': 3}
    response = requests.put(
        f'{BASE_URL}/api/cart/{cart_item["productId"]}/quantity',
        json=quantity_data,
        headers=headers
    )
    assert response.status_code == 200
    
    # 3. 验证更新结果
    response = requests.get(f'{BASE_URL}/api/cart', headers=headers)
    assert response.status_code == 200
    cart_list = response.json()['data']
    updated_item = next(item for item in cart_list if item['productId'] == cart_item['productId'])
    assert updated_item['quantity'] == 3

def test_clear_cart():
    """测试清空购物车"""
    headers = {'Authorization': f'Bearer {user_token}'}
    
    # 1. 清空购物车
    response = requests.delete(f'{BASE_URL}/api/cart', headers=headers)
    assert response.status_code == 200
    
    # 2. 验证清空结果
    response = requests.get(f'{BASE_URL}/api/cart', headers=headers)
    assert response.status_code == 200
    cart_list = response.json()['data']
    assert len(cart_list) == 0

def test_add_invalid_product():
    """测试添加不存在的商品到购物车"""
    headers = {'Authorization': f'Bearer {user_token}'}
    cart_data = {
        'productId': 99999,  # 不存在的商品ID
        'quantity': 1
    }
    response = requests.post(f'{BASE_URL}/api/cart', json=cart_data, headers=headers)
    assert response.status_code == 500
    assert '商品不存在' in response.json()['message']

def test_add_out_of_stock():
    """测试添加库存不足的商品到购物车"""
    # 1. 创建库存为1的测试商品
    headers = {'Authorization': f'Bearer {admin_token}'}
    product_data = {
        'name': '库存测试商品',
        'subtitle': '库存测试',
        'categoryId': 1,
        'brandId': 1,
        'mainImage': 'test.jpg',
        'price': 99.99,
        'stock': 1
    }
    response = requests.post(f'{BASE_URL}/api/products', json=product_data, headers=headers)
    print_response(response)
    assert response.status_code == 200
    product = response.json()['data']
    
    # 2. 尝试添加超过库存数量的商品
    headers = {'Authorization': f'Bearer {user_token}'}
    cart_data = {
        'productId': product['id'],
        'quantity': 2
    }
    response = requests.post(f'{BASE_URL}/api/cart', json=cart_data, headers=headers)
    assert response.status_code == 500
    assert '商品库存不足' in response.json()['message'] 