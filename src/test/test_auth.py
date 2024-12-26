import requests
import unittest

class TestAuth(unittest.TestCase):
    BASE_URL = "http://localhost:8080/api/auth"
    
    def test_register(self):
        data = {
            "username": "testuser",
            "password": "123456",
            "email": "test@example.com"
        }
        response = requests.post(f"{self.BASE_URL}/register", json=data)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()["code"], 200)
    
    def test_login(self):
        data = {
            "username": "testuser",
            "password": "123456"
        }
        response = requests.post(f"{self.BASE_URL}/login", json=data)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()["code"], 200)
        self.assertIsNotNone(response.json()["data"])  # 验证返回token 