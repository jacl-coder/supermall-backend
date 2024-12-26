package com.supermall.backend.cart;

import com.supermall.backend.domain.cart.dto.CartDTO;
import com.supermall.backend.domain.cart.entity.Cart;
import com.supermall.backend.domain.cart.mapper.CartMapper;
import com.supermall.backend.domain.cart.service.CartService;
import com.supermall.backend.domain.cart.vo.CartVO;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductService productService;

    private Long userId;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        userId = 1L;
        
        // 创建测试商品
        testProduct = new Product();
        testProduct.setName("测试商品");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStock(100);
        testProduct.setStatus(1);
        testProduct.setMainImage("test.jpg");
        productService.save(testProduct);
    }

    @Test
    void testAddToCart() {
        // 准备数据
        CartDTO cartDTO = new CartDTO();
        cartDTO.setProductId(testProduct.getId());
        cartDTO.setQuantity(2);
        cartDTO.setChecked(true);

        // 添加到购物车
        cartService.add(userId, cartDTO);

        // 验证
        List<CartVO> cartList = cartService.list(userId);
        assertEquals(1, cartList.size());
        CartVO cartVO = cartList.get(0);
        assertEquals(testProduct.getId(), cartVO.getProductId());
        assertEquals(2, cartVO.getQuantity());
        assertEquals(testProduct.getPrice().multiply(new BigDecimal("2")), cartVO.getTotalAmount());
    }

    @Test
    void testUpdateQuantity() {
        // 先添加商品到购物车
        CartDTO cartDTO = new CartDTO();
        cartDTO.setProductId(testProduct.getId());
        cartDTO.setQuantity(1);
        cartService.add(userId, cartDTO);

        // 获取购物车ID
        List<CartVO> cartList = cartService.list(userId);
        Long cartId = cartList.get(0).getId();

        // 更新数量
        cartService.updateQuantity(userId, cartId, 3);

        // 验证
        cartList = cartService.list(userId);
        assertEquals(1, cartList.size());
        assertEquals(3, cartList.get(0).getQuantity());
    }

    @Test
    void testUpdateChecked() {
        // 先添加商品到购物车
        CartDTO cartDTO = new CartDTO();
        cartDTO.setProductId(testProduct.getId());
        cartDTO.setQuantity(1);
        cartDTO.setChecked(true);
        cartService.add(userId, cartDTO);

        // 获取购物车ID
        List<CartVO> cartList = cartService.list(userId);
        Long cartId = cartList.get(0).getId();

        // 更新选中状态
        cartService.updateChecked(userId, cartId, false);

        // 验证
        cartList = cartService.list(userId);
        assertEquals(1, cartList.size());
        assertFalse(cartList.get(0).getChecked());
    }

    @Test
    void testDelete() {
        // 先添加商品到购物车
        CartDTO cartDTO = new CartDTO();
        cartDTO.setProductId(testProduct.getId());
        cartDTO.setQuantity(1);
        cartService.add(userId, cartDTO);

        // 获取购物车ID
        List<CartVO> cartList = cartService.list(userId);
        Long cartId = cartList.get(0).getId();

        // 删除
        cartService.delete(userId, cartId);

        // 验证
        cartList = cartService.list(userId);
        assertTrue(cartList.isEmpty());
    }

    @Test
    void testClear() {
        // 添加多个商品到购物车
        CartDTO cartDTO1 = new CartDTO();
        cartDTO1.setProductId(testProduct.getId());
        cartDTO1.setQuantity(1);
        cartService.add(userId, cartDTO1);

        CartDTO cartDTO2 = new CartDTO();
        cartDTO2.setProductId(testProduct.getId());
        cartDTO2.setQuantity(2);
        cartService.add(userId, cartDTO2);

        // 清空购物车
        cartService.clear(userId);

        // 验证
        List<CartVO> cartList = cartService.list(userId);
        assertTrue(cartList.isEmpty());
    }
} 