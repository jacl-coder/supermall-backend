from test_common import BASE_URL, print_response, get_admin_token
import requests
import json

def test_create_category():
    """测试创建分类"""
    print("\n测试创建分类...")
    url = f"{BASE_URL}/api/categories"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "name": "测试分类",
        "parentId": 0,  # 顶级分类
        "sort": 1,
        "icon": "http://example.com/icon.png"
    }
    try:
        response = requests.post(url, headers=headers, json=data)
        print_response(response)
        return response.status_code == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return False

def test_list_categories():
    """测试获取分类列表"""
    print("\n测试获取分类列表...")
    url = f"{BASE_URL}/api/categories"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    try:
        response = requests.get(url, headers=headers)
        print_response(response)
        if response.status_code == 200:
            return json.loads(response.text)["data"]
        return None
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return None

def test_update_category(category_id):
    """测试更新分类"""
    print("\n测试更新分类...")
    url = f"{BASE_URL}/api/categories/{category_id}"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "name": "更新后的分类",
        "sort": 2,
        "icon": "http://example.com/new-icon.png"
    }
    try:
        response = requests.put(url, headers=headers, json=data)
        print_response(response)
        return response.status_code == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return False

def test_update_category_status(category_id):
    """测试更新分类状态"""
    print("\n测试更新分类状态...")
    url = f"{BASE_URL}/api/categories/{category_id}/status"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "status": 0  # 0-禁用 1-启用
    }
    try:
        response = requests.put(url, headers=headers, json=data)
        print_response(response)
        return response.status_code == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return False

def test_delete_category(category_id):
    """测试删��分类"""
    print("\n测试删除分类...")
    url = f"{BASE_URL}/api/categories/{category_id}"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    try:
        response = requests.delete(url, headers=headers)
        print_response(response)
        return response.status_code == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return False

def main():
    """主测试函数"""
    print("开始分类管理功能测试...")
    
    # 测试创建分类
    if test_create_category():
        print("创建分类测试通过")
    else:
        print("创建分类测试失败")
        return

    # 获取分类列表
    categories = test_list_categories()
    if categories is not None:
        print("获取分类列表测试通过")
        if len(categories) > 0:
            category_id = categories[0]["id"]
            
            # 测试更新分类
            if test_update_category(category_id):
                print("更新分类测试通过")
            else:
                print("更新分类测试失败")

            # 测试更新分类状态
            if test_update_category_status(category_id):
                print("更新分类状态测试通过")
            else:
                print("更新分类状态���试失败")

            # 测试删除分类
            if test_delete_category(category_id):
                print("删除分类测试通过")
            else:
                print("删除分类测试失败")
        else:
            print("分类列表为空")
    else:
        print("获取分类列表测试失败")

if __name__ == "__main__":
    main() 