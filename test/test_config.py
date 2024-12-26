# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'lai20031024',
    'database': 'super_mall'
}

# API基础URL
BASE_URL = 'http://localhost:8081'

# 测试用户配置
TEST_USER = {
    'username': 'admin',
    'password': '123456'
}

# 测试数据配置
TEST_DATA = {
    'product': {
        'name': '测试商品',
        'subtitle': '测试商品副标题',
        'categoryId': 1,
        'brandId': 1,
        'mainImage': 'test-image.jpg',
        'subImages': 'image1.jpg,image2.jpg',
        'detail': '这是一个测试商品',
        'price': 99.99,
        'stock': 100,
        'status': 1
    },
    'category': {
        'name': '测试分类',
        'parentId': 0,
        'level': 1,
        'sort': 1,
        'status': 1
    },
    'brand': {
        'name': '测试品牌',
        'logo': 'test-logo.png',
        'description': '这是一个测试品牌',
        'sort': 1,
        'status': 1
    }
} 