import pytest
from test_common import BASE_URL, print_response, get_admin_token
import requests
import json

@pytest.fixture(scope="session", autouse=True)
def setup_test_data():
    """设置测试数据"""
    print("\n设置测试数据...")
    
    # 创建测试分类和品牌
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    
    # 创建分类
    category_data = {
        "name": "测试分类",
        "parentId": 0,
        "level": 1,
        "sort": 1,
        "status": 1
    }
    requests.post(f"{BASE_URL}/api/categories", headers=headers, json=category_data)
    
    # 创建品牌
    brand_data = {
        "name": "测试品牌",
        "logo": "test-logo.png",
        "description": "这是一个测试品牌",
        "sort": 1,
        "status": 1
    }
    requests.post(f"{BASE_URL}/api/brands", headers=headers, json=brand_data)

@pytest.fixture
def new_product():
    """创建一个测试商品并返回商品数据"""
    url = f"{BASE_URL}/api/products"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
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
    try:
        response = requests.post(url, headers=headers, json=data)
        assert response.status_code == 200
        response_data = json.loads(response.text)
        assert response_data["code"] == 200
        assert response_data["data"] is not None
        return response_data["data"]
    except Exception as e:
        pytest.fail(f"创建测试商品失败: {str(e)}")

def ensure_category_exists(category_id):
    """确保分类存在"""
    url = f"{BASE_URL}/api/categories/{category_id}"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    response = requests.get(url)
    if response.status_code == 404:
        # 创建分类
        create_url = f"{BASE_URL}/api/categories"
        data = {
            "name": "测试分类",
            "parentId": 0,
            "level": 1,
            "sort": 1,
            "status": 1
        }
        response = requests.post(create_url, headers=headers, json=data)
        assert response.status_code == 200

def ensure_brand_exists(brand_id):
    """确保品牌存在"""
    url = f"{BASE_URL}/api/brands/{brand_id}"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    response = requests.get(url)
    if response.status_code == 404:
        # 创建品牌
        create_url = f"{BASE_URL}/api/brands"
        data = {
            "name": "测试品牌",
            "logo": "test-logo.png",
            "description": "这是一个测试品牌",
            "sort": 1,
            "status": 1
        }
        response = requests.post(create_url, headers=headers, json=data)
        assert response.status_code == 200

def test_create_product():
    """测试创建商品"""
    print("\n测试创建商品...")
    # 确保分类和品牌存在
    ensure_category_exists(1)
    ensure_brand_exists(1)
    
    url = f"{BASE_URL}/api/products"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
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

def test_get_product_list():
    """测试获取商品列表"""
    print("\n测试获取商品列表...")
    url = f"{BASE_URL}/api/products"
    try:
        response = requests.get(url)
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

def test_get_product_detail(new_product):
    """测���获取商品详情"""
    print("\n测试获取商品详情...")
    url = f"{BASE_URL}/api/products/{new_product['id']}"
    try:
        response = requests.get(url)
        print_response(response)
        assert response.status_code == 200
        response_data = json.loads(response.text)
        print(f"响应数据: {response_data}")
        assert response_data["code"] == 200
        assert response_data["data"] is not None
        
        # 验证返回的数据
        data = response_data["data"]
        assert data["id"] == new_product["id"]
        assert data["name"] == new_product["name"]
        assert "categoryName" in data
        assert "brandName" in data
        
        # 验证子图片
        if new_product.get("subImages"):
            assert isinstance(data["subImages"], list)
            assert len(data["subImages"]) > 0
    except Exception as e:
        print(f"发生错误: {str(e)}")
        pytest.fail(str(e))

def test_update_product(new_product):
    """测试更新商品"""
    print("\n测试更新商品...")
    url = f"{BASE_URL}/api/products/{new_product['id']}"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "name": "更新后的商品",
        "subtitle": "更新后的副标题",
        "categoryId": 1,
        "brandId": 1,
        "mainImage": "updated-image.jpg",
        "subImages": "updated1.jpg,updated2.jpg",
        "detail": "这是更新后的商品",
        "price": 199.99,
        "stock": 200,
        "status": 1
    }
    try:
        response = requests.put(url, headers=headers, json=data)
        print_response(response)
        assert response.status_code == 200
        response_data = json.loads(response.text)
        print(f"响应数据: {response_data}")
        assert response_data["code"] == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        pytest.fail(str(e))

def test_update_product_status(new_product):
    """测试更新商品状态"""
    print("\n测试更新商品状态...")
    url = f"{BASE_URL}/api/products/{new_product['id']}/status"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    params = {"status": 0}
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

def test_delete_product(new_product):
    """测试删除商品"""
    print("\n测试删除商品...")
    url = f"{BASE_URL}/api/products/{new_product['id']}"
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

def test_batch_delete_products(new_product):
    """测试批量删除商品"""
    print("\n测试批量删除商品...")
    url = f"{BASE_URL}/api/products/batch"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = [new_product["id"]]
    try:
        response = requests.delete(url, headers=headers, json=data)
        print_response(response)
        assert response.status_code == 200
        response_data = json.loads(response.text)
        print(f"响应数据: {response_data}")
        assert response_data["code"] == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        pytest.fail(str(e))

def create_test_product():
    """创建一个测试商品并返回商品数据"""
    url = f"{BASE_URL}/api/products"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
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
    try:
        response = requests.post(url, headers=headers, json=data)
        assert response.status_code == 200
        response_data = json.loads(response.text)
        assert response_data["code"] == 200
        assert response_data["data"] is not None
        return response_data["data"]
    except Exception as e:
        raise Exception(f"创建测试商品失败: {str(e)}")

def test_product_flow():
    """测试商品完整流程"""
    # 创建商品
    test_create_product()
    
    # 获取商品列表
    test_get_product_list()
    
    # 创建一个商品用于详情和更新测试
    product = create_test_product()
    
    # 获取商品详情
    test_get_product_detail(product)
    
    # 更新商品
    test_update_product(product)
    
    # 更新商品状态
    test_update_product_status(product)
    
    # 删除商品
    test_delete_product(product)
    
    # 创建一个商品用于批量删除测试
    product = create_test_product()
    
    # 批量删除商品
    test_batch_delete_products(product) 