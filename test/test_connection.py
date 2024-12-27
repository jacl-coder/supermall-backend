import requests
import pytest

def test_server_connection():
    """测试服务器连接"""
    try:
        response = requests.get('http://localhost:8080/doc.html')
        print(f"连接状态: {response.status_code}")
        assert response.status_code == 200
    except requests.exceptions.ConnectionError as e:
        pytest.fail(f"无法连接到服务器: {str(e)}") 