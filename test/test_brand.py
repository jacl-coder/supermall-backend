import pytest
from test_common import BASE_URL, print_response, get_admin_token
import requests
import json

@pytest.fixture
def new_brand():
    """创建一个测试品牌并返回品牌数据"""
    url = f"{BASE_URL}/api/brands"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "name": "测试品牌",
        "logo": "test-logo.png",
        "description": "这是一个测试品牌",
        "sort": 1,
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
        pytest.fail(f"创建测试品牌失败: {str(e)}")

def test_create_brand():
    """测试创建品牌"""
    print("\n测试创建品牌...")
    url = f"{BASE_URL}/api/brands"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "name": "测试品牌",
        "logo": "test-logo.png",
        "description": "这是一个测试品牌",
        "sort": 1,
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

def test_get_brand_list():
    """测试获取品牌列表"""
    print("\n测试获取品牌列表...")
    url = f"{BASE_URL}/api/brands"
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

def test_update_brand(new_brand):
    """测试更新品牌"""
    print("\n测试更新品牌...")
    url = f"{BASE_URL}/api/brands/{new_brand['id']}"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "name": "更新后的品牌",
        "logo": "updated-logo.png",
        "description": "这是更新后的测试品牌",
        "sort": 2,
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

def test_update_brand_status(new_brand):
    """测试更新品牌状态"""
    print("\n测试更新品牌状态...")
    url = f"{BASE_URL}/api/brands/{new_brand['id']}/status"
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

def test_delete_brand(new_brand):
    """测试删除品牌"""
    print("\n测试删除品牌...")
    url = f"{BASE_URL}/api/brands/{new_brand['id']}"
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

def test_batch_delete_brands(new_brand):
    """测试批量删除品牌"""
    print("\n测试批量删除品牌...")
    url = f"{BASE_URL}/api/brands/batch"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = [new_brand["id"]]
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

def test_get_all_brands():
    """测试获取所有品牌"""
    print("\n测试获取所有品牌...")
    url = f"{BASE_URL}/api/brands/all"
    try:
        response = requests.get(url)
        print_response(response)
        assert response.status_code == 200
        response_data = json.loads(response.text)
        print(f"响应数据: {response_data}")
        assert response_data["code"] == 200
        assert isinstance(response_data["data"], list)
    except Exception as e:
        print(f"发生错误: {str(e)}")
        pytest.fail(str(e))

def create_test_brand():
    """创建一个测试品牌并返回品牌数据"""
    url = f"{BASE_URL}/api/brands"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "name": "测试品牌",
        "logo": "test-logo.png",
        "description": "这是一个测试品牌",
        "sort": 1,
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
        raise Exception(f"创建测试品牌失败: {str(e)}")

def test_brand_flow():
    """测试品牌完整流程"""
    # 创建品牌
    test_create_brand()
    
    # 获取品牌列表
    test_get_brand_list()
    
    # 获取所有品牌
    test_get_all_brands()
    
    # 创建一个品牌用于更新和删除测试
    brand = create_test_brand()
    
    # 更新品牌
    test_update_brand(brand)
    
    # 更新品牌状态
    test_update_brand_status(brand)
    
    # 删除品牌
    test_delete_brand(brand)
    
    # 创建一个品牌用于批量删除测试
    brand = create_test_brand()
    
    # 批量删除品牌
    test_batch_delete_brands(brand) 