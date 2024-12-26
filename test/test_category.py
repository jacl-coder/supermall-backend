import pytest
from test_common import BASE_URL, print_response, get_admin_token
import requests
import json

@pytest.fixture
def new_category():
    """创建一个测试分类并返回分类数据"""
    url = f"{BASE_URL}/api/categories"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "name": "测试分类",
        "parentId": 0,
        "level": 1,
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
        pytest.fail(f"创建测试分类失败: {str(e)}")

def test_create_category():
    """测试创建分类"""
    print("\n测试创建分类...")
    url = f"{BASE_URL}/api/categories"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "name": "测试分类",
        "parentId": 0,
        "level": 1,
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

def test_get_category_list():
    """测试获取分类列表"""
    print("\n测试获取分类列表...")
    url = f"{BASE_URL}/api/categories"
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

def test_get_category_detail(new_category):
    """测试获取分类详情"""
    print("\n测试获取分类详情...")
    url = f"{BASE_URL}/api/categories/{new_category['id']}"
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
        assert data["id"] == new_category["id"]
        assert data["name"] == new_category["name"]
        assert data["level"] == new_category["level"]
    except Exception as e:
        print(f"发生错误: {str(e)}")
        pytest.fail(str(e))

def test_update_category(new_category):
    """测试更新分类"""
    print("\n测试更新分类...")
    url = f"{BASE_URL}/api/categories/{new_category['id']}"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "name": "更新后的分类",
        "parentId": 0,
        "level": 1,
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

def test_update_category_status(new_category):
    """测试更新分类状态"""
    print("\n测试更新分类状态...")
    url = f"{BASE_URL}/api/categories/{new_category['id']}/status"
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

def test_delete_category(new_category):
    """测试删除分类"""
    print("\n测试删除分类...")
    url = f"{BASE_URL}/api/categories/{new_category['id']}"
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

def create_test_category():
    """创建一个测试分类并返回分类数据"""
    url = f"{BASE_URL}/api/categories"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "name": "测试分类",
        "parentId": 0,
        "level": 1,
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
        raise Exception(f"创建测试分类失败: {str(e)}")

def test_category_flow():
    """测试分类完整流程"""
    # 创建分类
    test_create_category()
    
    # 获取分类列表
    test_get_category_list()
    
    # 创建一个分类用于详情和更新测试
    category = create_test_category()
    
    # 获取分类详情
    test_get_category_detail(category)
    
    # 更新分类
    test_update_category(category)
    
    # 更新分类状态
    test_update_category_status(category)
    
    # 删除分类
    test_delete_category(category) 