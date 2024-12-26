import pytest
from test_common import BASE_URL, print_response, get_admin_token
import requests
import json
from test_config import DB_CONFIG

def test_cleanup():
    """清理测试数据"""
    print("\n开始清理测试数据...")
    
    # 1. 清理购物车数据
    cleanup_cart()
    
    # 2. 清理商品数据
    cleanup_products()
    
    # 3. 清理分类数据
    cleanup_categories()
    
    # 4. 清理品牌数据
    cleanup_brands()
    
    # 5. 清理订单数据
    cleanup_orders()
    
    print("测试数据清理完成")

def execute_sql(sql, description=""):
    """执行 SQL 语句"""
    try:
        import mysql.connector
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        for statement in sql.split(';'):
            if statement.strip():
                cursor.execute(statement)
        
        conn.commit()
        if description:
            print(f"{description}已清理完成")
    finally:
        if 'cursor' in locals():
            cursor.close()
        if 'conn' in locals():
            conn.close()

def cleanup_cart():
    """清理购物车数据"""
    print("清理购物车数据...")
    sql = """
    DELETE FROM cart;
    ALTER TABLE cart AUTO_INCREMENT = 1;
    """
    execute_sql(sql, "购物车数据")

def cleanup_products():
    """清理商品数据"""
    print("清理商品数据...")
    sql = """
    DELETE FROM product;
    ALTER TABLE product AUTO_INCREMENT = 1;
    """
    execute_sql(sql, "商品数据")

def cleanup_categories():
    """清理分类数据"""
    print("清理分类数据...")
    sql = """
    DELETE FROM category;
    ALTER TABLE category AUTO_INCREMENT = 1;
    """
    execute_sql(sql, "分类数据")

def cleanup_brands():
    """清理品牌数据"""
    print("清理品牌数据...")
    sql = """
    DELETE FROM brand;
    ALTER TABLE brand AUTO_INCREMENT = 1;
    """
    execute_sql(sql, "品牌数据")

def cleanup_orders():
    """清理订单数据"""
    print("清理订单数据...")
    sql = """
    DELETE FROM `order`;
    ALTER TABLE `order` AUTO_INCREMENT = 1;
    DELETE FROM order_item;
    ALTER TABLE order_item AUTO_INCREMENT = 1;
    """
    execute_sql(sql, "订单数据")

if __name__ == "__main__":
    test_cleanup() 