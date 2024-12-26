import pytest
import requests
from test_common import BASE_URL, login_and_get_token

def setup_module():
    """测试模块初始化"""
    global admin_token, user_token
    # 管理员登录
    admin_token = login_and_get_token('admin', 'admin123')
    # 普通用户登录
    user_token = login_and_get_token('user01', 'user123')

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
    response = requests.post(f'{BASE_URL}/api/products', json=product_data, headers=headers)
    assert response.status_code == 200
    return response.json()['data']

def test_add_to_cart():
    """测试添加商品到购物车"""
    # 创建测试商品
    product = create_test_product()
    
    # 添加商品到购物车
    headers = {'Authorization': f'Bearer {user_token}'}
    cart_data = {
        'productId': product['id'],
        'quantity': 2
    }
    response = requests.post(f'{BASE_URL}/api/cart', json=cart_data, headers=headers)
    assert response.status_code == 200
    
    # 验证购物车列表
    response = requests.get(f'{BASE_URL}/api/cart', headers=headers)
    assert response.status_code == 200
    cart_list = response.json()['data']
    assert len(cart_list) > 0
    cart_item = next(item for item in cart_list if item['productId'] == product['id'])
    assert cart_item['quantity'] == 2
    assert cart_item['checked'] == True
    assert float(cart_item['price']) == 99.99
    assert float(cart_item['totalAmount']) == 199.98

def test_update_cart_quantity():
    """测试更新购物车商品数量"""
    headers = {'Authorization': f'Bearer {user_token}'}
    
    # 获取购物车列表
    response = requests.get(f'{BASE_URL}/api/cart', headers=headers)
    assert response.status_code == 200
    cart_list = response.json()['data']
    assert len(cart_list) > 0
    cart_item = cart_list[0]
    
    # 更新数量
    quantity_data = {'quantity': 3}
    response = requests.put(
        f'{BASE_URL}/api/cart/{cart_item["productId"]}/quantity',
        json=quantity_data,
        headers=headers
    )
    assert response.status_code == 200
    
    # 验证更新结果
    response = requests.get(f'{BASE_URL}/api/cart', headers=headers)
    assert response.status_code == 200
    cart_list = response.json()['data']
    updated_item = next(item for item in cart_list if item['productId'] == cart_item['productId'])
    assert updated_item['quantity'] == 3

def test_update_cart_checked():
    """测试更新购物车商品选中状态"""
    headers = {'Authorization': f'Bearer {user_token}'}
    
    # 获取购物车列表
    response = requests.get(f'{BASE_URL}/api/cart', headers=headers)
    assert response.status_code == 200
    cart_list = response.json()['data']
    assert len(cart_list) > 0
    cart_item = cart_list[0]
    
    # 更新选中状态
    checked_data = {'checked': 0}
    response = requests.put(
        f'{BASE_URL}/api/cart/{cart_item["productId"]}/checked',
        json=checked_data,
        headers=headers
    )
    assert response.status_code == 200
    
    # 验证更新结果
    response = requests.get(f'{BASE_URL}/api/cart', headers=headers)
    assert response.status_code == 200
    cart_list = response.json()['data']
    updated_item = next(item for item in cart_list if item['productId'] == cart_item['productId'])
    assert updated_item['checked'] == False

def test_update_all_checked():
    """测试批量更新购物车商品选中状态"""
    headers = {'Authorization': f'Bearer {user_token}'}
    
    # 批量更新选中状态
    checked_data = {'checked': 1}
    response = requests.put(f'{BASE_URL}/api/cart/checked', json=checked_data, headers=headers)
    assert response.status_code == 200
    
    # 验证更新结果
    response = requests.get(f'{BASE_URL}/api/cart', headers=headers)
    assert response.status_code == 200
    cart_list = response.json()['data']
    assert all(item['checked'] for item in cart_list)

def test_get_cart_count():
    """测试获取购物车商品数量"""
    headers = {'Authorization': f'Bearer {user_token}'}
    
    # 获取购物车商品总数
    response = requests.get(f'{BASE_URL}/api/cart/count', headers=headers)
    assert response.status_code == 200
    count = response.json()['data']
    assert isinstance(count, int)
    assert count > 0

def test_delete_cart_item():
    """测试删除购物车商品"""
    headers = {'Authorization': f'Bearer {user_token}'}
    
    # 获取购物车列表
    response = requests.get(f'{BASE_URL}/api/cart', headers=headers)
    assert response.status_code == 200
    cart_list = response.json()['data']
    assert len(cart_list) > 0
    cart_item = cart_list[0]
    
    # 删除商品
    response = requests.delete(f'{BASE_URL}/api/cart/{cart_item["productId"]}', headers=headers)
    assert response.status_code == 200
    
    # 验证删除结果
    response = requests.get(f'{BASE_URL}/api/cart', headers=headers)
    assert response.status_code == 200
    new_cart_list = response.json()['data']
    assert len(new_cart_list) == len(cart_list) - 1
    assert not any(item['productId'] == cart_item['productId'] for item in new_cart_list)

def test_clear_cart():
    """测试清空购物车"""
    headers = {'Authorization': f'Bearer {user_token}'}
    
    # 清空购物车
    response = requests.delete(f'{BASE_URL}/api/cart', headers=headers)
    assert response.status_code == 200
    
    # 验证清空结果
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
    # 创建库存为1的测试商品
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
    assert response.status_code == 200
    product = response.json()['data']
    
    # 尝试添加超过库存数量的商品
    headers = {'Authorization': f'Bearer {user_token}'}
    cart_data = {
        'productId': product['id'],
        'quantity': 2
    }
    response = requests.post(f'{BASE_URL}/api/cart', json=cart_data, headers=headers)
    assert response.status_code == 500
    assert '商品库存不足' in response.json()['message'] 