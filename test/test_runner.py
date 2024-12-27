import pytest

def run_all_tests():
    """运行所有测试"""
    # 按照业务流程顺序执行测试
    test_files = [
        "test_auth.py",        # 先测试认证功能
        "test_product.py",     # 然后测试商品管理
        "test_cart.py",        # 接着测试购物车
        "test_order.py",       # 再测试订单
        "test_payment.py"      # 最后测试支付
    ]
    
    for test_file in test_files:
        print(f"\n{'='*20} 运行 {test_file} {'='*20}")
        pytest.main(["-v", test_file])

if __name__ == "__main__":
    run_all_tests() 