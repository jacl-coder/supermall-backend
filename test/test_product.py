from test_common import BASE_URL, print_response, get_admin_token
import requests
import json

def test_create_product():
    """测试创建商品"""
    print("\n测试创建商品...")
    url = f"{BASE_URL}/api/products"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "name": "测试商品",
        "subtitle": "测试商品副标题",
        "categoryId": 1,
        "brandId": 1,
        "mainImage": "http://example.com/main.jpg",
        "subImages": '["http://example.com/sub1.jpg", "http://example.com/sub2.jpg"]',
        "detail": "这是一个测试商品的详细描述",
        "price": 99.99,
        "stock": 100
    }
    try:
        response = requests.post(url, headers=headers, json=data)
        print_response(response)
        return response.status_code == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return False

def test_list_products():
    """测试获取商品列表"""
    print("\n测试获取商品列表...")
    url = f"{BASE_URL}/api/products"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    try:
        response = requests.get(url, headers=headers)
        print_response(response)
        if response.status_code == 200:
            return json.loads(response.text)["data"]["records"]
        return None
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return None

def test_update_product(product_id):
    """测试更新商品"""
    print("\n测试更新商品...")
    url = f"{BASE_URL}/api/products/{product_id}"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "name": "更新后的商品",
        "subtitle": "更新后的副标题",
        "mainImage": "http://example.com/new-main.jpg",
        "subImages": '["http://example.com/new-sub1.jpg", "http://example.com/new-sub2.jpg"]',
        "detail": "这是更新后的商品详细描述",
        "price": 199.99,
        "stock": 200
    }
    try:
        response = requests.put(url, headers=headers, json=data)
        print_response(response)
        return response.status_code == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return False

def test_update_product_status(product_id):
    """测试更新商品状态"""
    print("\n测试更新商品状态...")
    url = f"{BASE_URL}/api/products/{product_id}/status"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    data = {
        "status": 0  # 0-下架 1-上架
    }
    try:
        response = requests.put(url, headers=headers, json=data)
        print_response(response)
        return response.status_code == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return False

def test_search_products():
    """测试搜索商品"""
    print("\n测试搜索商品...")
    url = f"{BASE_URL}/api/products/search"
    headers = {"Authorization": f"Bearer {get_admin_token()}"}
    params = {
        "keyword": "测试"
    }
    try:
        response = requests.get(url, headers=headers, params=params)
        print_response(response)
        if response.status_code == 200:
            return json.loads(response.text)["data"]
        return None
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return None

def test_list_by_category(category_id):
    """测试根据分类获取商品"""
    print("\n测试根据分类获取商品...")
    url = f"{BASE_URL}/api/products/category/{category_id}"
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

def test_list_by_brand(brand_id):
    """测试根据品牌获取商品"""
    print("\n测试根据品牌获取商品...")
    url = f"{BASE_URL}/api/products/brand/{brand_id}"
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

def test_delete_product(product_id):
    """测试删除商品"""
    print("\n测试删除商品...")
    url = f"{BASE_URL}/api/products/{product_id}"
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
    print("开始商品管理功能测试...")
    
    # 测试创建商品
    if test_create_product():
        print("创建商品测试通过")
    else:
        print("创建商品测试失败")
        return

    # 获取商品列表
    products = test_list_products()
    if products is not None:
        print("获取商品列表测试通过")
        if len(products) > 0:
            product_id = products[0]["id"]
            
            # 测试更新商品
            if test_update_product(product_id):
                print("更新商品测试通过")
            else:
                print("更新商品测试失败")

            # 测试更新商品状态
            if test_update_product_status(product_id):
                print("更新商品状态测试通过")
            else:
                print("更新商品状态测试失败")

            # 测试搜索商品
            if test_search_products() is not None:
                print("搜索商品测试通过")
            else:
                print("搜索商品测试失败")

            # 测试根据分类获取商品
            if test_list_by_category(1) is not None:
                print("根据分类获取商品测试通过")
            else:
                print("根据分类获取商品测试失败")

            # 测试根据品牌获取商品
            if test_list_by_brand(1) is not None:
                print("根据品牌获取商品测试通过")
            else:
                print("根据品牌获取商品测试失败")

            # # 测试删除商品
            # if test_delete_product(product_id):
            #     print("删除商品测试通过")
            # else:
            #     print("删除商品测试失败")
        else:
            print("商品列表为空")
    else:
        print("获取商品列表测试失败")

if __name__ == "__main__":
    main() 