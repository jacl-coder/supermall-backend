import requests
import unittest

class TestCategory(unittest.TestCase):
    BASE_URL = "http://localhost:8080/api/categories"
    
    def setUp(self):
        # 登录获取token
        response = requests.post("http://localhost:8080/api/auth/login", 
            json={"username": "admin", "password": "123456"})
        self.token = response.json()["data"]
        self.headers = {"Authorization": f"Bearer {self.token}"}
    
    def test_create_category(self):
        data = {
            "name": "测试分类",
            "parentId": 0,
            "sort": 0,
            "icon": "http://example.com/icon.png"
        }
        response = requests.post(self.BASE_URL, json=data, headers=self.headers)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()["code"], 200)
    
    def test_list_categories(self):
        response = requests.get(self.BASE_URL, headers=self.headers)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()["code"], 200)
        self.assertIsInstance(response.json()["data"], list) 