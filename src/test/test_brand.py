import requests
import unittest

class TestBrand(unittest.TestCase):
    BASE_URL = "http://localhost:8080/api/brands"
    
    def setUp(self):
        # 登录获取token
        response = requests.post("http://localhost:8080/api/auth/login", 
            json={"username": "admin", "password": "123456"})
        self.token = response.json()["data"]
        self.headers = {"Authorization": f"Bearer {self.token}"}
    
    def test_create_brand(self):
        data = {
            "name": "测试品牌",
            "logo": "http://example.com/logo.png",
            "description": "测试品牌描述",
            "sort": 0
        }
        response = requests.post(self.BASE_URL, json=data, headers=self.headers)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()["code"], 200)
    
    def test_list_brands(self):
        response = requests.get(self.BASE_URL, headers=self.headers)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()["code"], 200)
        self.assertIsInstance(response.json()["data"], list) 