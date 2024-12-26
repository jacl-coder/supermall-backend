import requests
import unittest

class TestCart(unittest.TestCase):
    BASE_URL = "http://localhost:8080/api/cart"
    
    def setUp(self):
        # 登录获取token
        response = requests.post("http://localhost:8080/api/auth/login", 
            json={"username": "testuser", "password": "123456"})
        self.token = response.json()["data"]
        self.headers = {"Authorization": f"Bearer {self.token}"}
    
    def test_add_to_cart(self):
        data = {
            "productId": 1,
            "quantity": 1
        }
        response = requests.post(self.BASE_URL, json=data, headers=self.headers)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()["code"], 200)
    
    def test_list_cart(self):
        response = requests.get(self.BASE_URL, headers=self.headers)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()["code"], 200)
        self.assertIsInstance(response.json()["data"], list) 