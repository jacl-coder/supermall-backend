from test_common import BASE_URL, print_response
import requests
import json
import time

def test_register():
    """测试用户注册"""
    print("\n测试注册功能...")
    url = f"{BASE_URL}/api/auth/register"
    data = {
        "username": "testuser" + str(int(time.time())),
        "password": "123456",
        "email": "test" + str(int(time.time())) + "@example.com"
    }
    try:
        response = requests.post(url, json=data)
        print_response(response)
        return response.status_code == 200
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return False

def test_login(username="admin", password="123456"):
    """测试用户登录"""
    print(f"\n测试{username}登录...")
    url = f"{BASE_URL}/api/auth/login"
    data = {
        "username": username,
        "password": password
    }
    try:
        response = requests.post(url, json=data)
        print_response(response)
        if response.status_code == 200:
            return json.loads(response.text)["data"]
        return None
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return None

def test_refresh_token(token):
    """测试刷新令牌"""
    print("\n测试刷新令牌...")
    url = f"{BASE_URL}/api/auth/refresh"
    headers = {"Authorization": f"Bearer {token}"}
    data = {"token": token}
    try:
        response = requests.post(url, headers=headers, json=data)
        print_response(response)
        if response.status_code == 200:
            return json.loads(response.text)["data"]
        return None
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return None

def main():
    """主测试函数"""
    print("开始认证功能测试...")
    
    # 测试注册
    if test_register():
        print("注册测试通过")
    else:
        print("注册测试失败")
        return

    # 测试管理员登录
    admin_token = test_login("admin", "123456")
    if admin_token:
        print("管理员登录测试通过")
        print(f"获取到的令牌: {admin_token}")
    else:
        print("管理员登录测试失败")
        return

    # 测试用户登录
    user_token = test_login("testuser2", "123456")
    if user_token:
        print("用户登录测试通过")
        print(f"获取到的令牌: {user_token}")
    else:
        print("用户登录测试失败")
        return

    # 等待一秒,确保令牌已经生成
    time.sleep(1)

    # 测试刷新令牌
    new_user_token = test_refresh_token(user_token)
    if new_user_token:
        print("用户刷新令牌测试通过")
        print(f"新的令牌: {new_user_token}")
    else:
        print("用户刷新令牌测试失败")

    new_admin_token = test_refresh_token(admin_token)
    if new_admin_token:
        print("管理员刷新令牌测试通过")
        print(f"新的令牌: {new_admin_token}")
    else:
        print("管理员刷新令牌测试失败")

if __name__ == "__main__":
    main() 