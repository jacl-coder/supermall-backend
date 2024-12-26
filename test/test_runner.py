import pytest
import os
import yaml

def load_config():
    """加载测试配置"""
    config_path = os.path.join(os.path.dirname(__file__), 'config.yml')
    with open(config_path, 'r', encoding='utf-8') as f:
        return yaml.safe_load(f)

def run_tests():
    """运行所有测试并生成报告"""
    # 创建报告目录
    report_dir = os.path.join(os.path.dirname(__file__), 'report')
    if not os.path.exists(report_dir):
        os.makedirs(report_dir)
        
    # 运行测试
    pytest.main([
        '--html=report/report.html',
        '--self-contained-html',
        'test_auth.py',
        'test_brand.py',
        'test_cart.py',
        'test_category.py',
        'test_product.py'
    ])

if __name__ == '__main__':
    run_tests() 