from test_common import BASE_URL, print_response, get_user_token
import requests
import json

def test_add_to_cart():
    """测试添加商品到购物车"""
    print("\n测试添加商品到购物车...")
    url = f"{BASE_URL}/api/cart"
    headers = {"Authorization": f"Bearer {get_user_token()}"}
    data = {
        "productId": 4,
        "quantity": 2
    }
    try:
        response = requests.post(url, headers=headers, json=data)
        print_response(response)
        return response.status_code == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return False

def test_list_cart():
    """测试获取购物车列表"""
    print("\n测试获取购物车列表...")
    url = f"{BASE_URL}/api/cart"
    headers = {"Authorization": f"Bearer {get_user_token()}"}
    try:
        response = requests.get(url, headers=headers)
        print_response(response)
        if response.status_code == 200:
            return json.loads(response.text)["data"]
        return None
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return None

def test_update_quantity(product_id):
    """测试更新购物车商品数量"""
    print("\n测试更新购物车商品数量...")
    url = f"{BASE_URL}/api/cart/{product_id}/quantity"
    headers = {"Authorization": f"Bearer {get_user_token()}"}
    data = {
        "quantity": 3
    }
    try:
        response = requests.put(url, headers=headers, json=data)
        print_response(response)
        return response.status_code == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return False

def test_update_checked(product_id):
    """测试更新商品选中状态"""
    print("\n测试更新商品选中状态...")
    url = f"{BASE_URL}/api/cart/{product_id}/checked"
    headers = {"Authorization": f"Bearer {get_user_token()}"}
    data = {
        "checked": 0
    }
    try:
        response = requests.put(url, headers=headers, json=data)
        print_response(response)
        return response.status_code == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return False

def test_update_checked_all():
    """测试批量更新商品选中状态"""
    print("\n测试批量更新商品选中状态...")
    url = f"{BASE_URL}/api/cart/checked"
    headers = {"Authorization": f"Bearer {get_user_token()}"}
    data = {
        "checked": 1
    }
    try:
        response = requests.put(url, headers=headers, json=data)
        print_response(response)
        return response.status_code == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return False

def test_get_cart_count():
    """测试获取购物车商品数量"""
    print("\n测试获取购物车商品数量...")
    url = f"{BASE_URL}/api/cart/count"
    headers = {"Authorization": f"Bearer {get_user_token()}"}
    try:
        response = requests.get(url, headers=headers)
        print_response(response)
        if response.status_code == 200:
            return json.loads(response.text)["data"]
        return None
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return None

def test_delete_from_cart(product_id):
    """测试删除购物车商品"""
    print("\n测试删除购物车商品...")
    url = f"{BASE_URL}/api/cart/{product_id}"
    headers = {"Authorization": f"Bearer {get_user_token()}"}
    try:
        response = requests.delete(url, headers=headers)
        print_response(response)
        return response.status_code == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return False

def test_clear_cart():
    """测试清空购物车"""
    print("\n测试清空购物车...")
    url = f"{BASE_URL}/api/cart"
    headers = {"Authorization": f"Bearer {get_user_token()}"}
    try:
        response = requests.delete(url, headers=headers)
        print_response(response)
        return response.status_code == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return False

def main():
    """主测试函数"""
    print("开始购物车功能测试...")
    
    # 测试添加商品到购物车
    if test_add_to_cart():
        print("添加商品到购物车测试通过")
    else:
        print("添加商品到购物车测试失败")
        return

    # 获取购物车列表
    cart_items = test_list_cart()
    if cart_items is not None:
        print("获取购物车列表测试通过")
        if len(cart_items) > 0:
            product_id = cart_items[0]["productId"]
            
            # 测试更新购物车商品数量
            if test_update_quantity(product_id):
                print("更新购物车商品数量测试通过")
            else:
                print("更新购物车商品数量测试失败")

            # 测试更新商品选中状态
            if test_update_checked(product_id):
                print("更新商品选中状态测试通过")
            else:
                print("更新商品选中状态测试失败")

            # 测试批量更新商品选中状态
            if test_update_checked_all():
                print("批量更新商品选中状态测试通过")
            else:
                print("批量更新商品选中状态测试失败")

            # 测试获取购物车商品数量
            if test_get_cart_count() is not None:
                print("获取购物车商品数量测试通过")
            else:
                print("获取购物车商品数量测试失败")

            # 测试删除购物车商品
            if test_delete_from_cart(product_id):
                print("删除购物车商品测试通过")
            else:
                print("删除购物车商品测试失败")

            # 测试清空购物车
            if test_clear_cart():
                print("清空购物车测试通过")
            else:
                print("清空购物车测试失败")
        else:
            print("购物车为空")
    else:
        print("获取购物车列表测试失败")

if __name__ == "__main__":
    main() 