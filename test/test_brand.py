from test_common import BASE_URL, print_response, get_admin_token
import requests
import json

def test_create_brand():
    """测试创建品牌"""
    print("\n测试创建品牌...")
    url = f"{BASE_URL}/api/brands"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "name": "测试品牌",
        "logo": "http://example.com/logo.png",
        "description": "这是一个测试品牌",
        "sort": 1
    }
    try:
        response = requests.post(url, headers=headers, json=data)
        print_response(response)
        return response.status_code == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return False

def test_list_brands():
    """测试获取品牌列表"""
    print("\n测试获取品牌列表...")
    url = f"{BASE_URL}/api/brands"
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

def test_update_brand(brand_id):
    """测试更新品牌"""
    print("\n测试更新品牌...")
    url = f"{BASE_URL}/api/brands/{brand_id}"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "name": "更新后的品牌",
        "logo": "http://example.com/new-logo.png",
        "description": "这是更新后的品牌描述",
        "sort": 2
    }
    try:
        response = requests.put(url, headers=headers, json=data)
        print_response(response)
        return response.status_code == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return False

def test_update_brand_status(brand_id):
    """测试更新品牌状态"""
    print("\n测试更新品牌状态...")
    url = f"{BASE_URL}/api/brands/{brand_id}/status"
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

def test_delete_brand(brand_id):
    """测试删除品牌"""
    print("\n测试删除品牌...")
    url = f"{BASE_URL}/api/brands/{brand_id}"
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
    print("开始品牌管理功能测试...")
    
    # 测试创建品牌
    if test_create_brand():
        print("创建品牌测试通过")
    else:
        print("创建品牌测试失败")
        return

    # 获取品牌列表
    brands = test_list_brands()
    if brands is not None:
        print("获取品牌列表测试通过")
        if len(brands) > 0:
            brand_id = brands[0]["id"]
            
            # 测试更新品牌
            if test_update_brand(brand_id):
                print("更新品牌测试通过")
            else:
                print("更新品牌测试失败")

            # 测试更新品牌状态
            if test_update_brand_status(brand_id):
                print("更新品牌状态测试通过")
            else:
                print("更新品牌状态测试失败")

            # 测试删除品牌
            if test_delete_brand(brand_id):
                print("删除品牌测试通过")
            else:
                print("删除品牌测试失败")
        else:
            print("品牌列表为空")
    else:
        print("获取品牌列表测试失败")

if __name__ == "__main__":
    main() 