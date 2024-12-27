package com.supermall.backend.controller;

import com.supermall.backend.common.api.CommonResult;
import com.supermall.backend.common.exception.ApiException;
import com.supermall.backend.domain.payment.service.PaymentService;
import com.supermall.backend.domain.payment.vo.PaymentVO;
import com.supermall.backend.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "支付管理", description = "支付管理接口")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @Operation(summary = "创建支付记录")
    @PostMapping
    public CommonResult<PaymentVO> createPayment(@RequestBody PaymentVO paymentVO) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            paymentVO.setUserId(userId);
            PaymentVO payment = paymentService.createPayment(paymentVO);
            return CommonResult.success(payment);
        } catch (ApiException e) {
            log.error("创建支付记录失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("创建支付记录失败", e);
            return CommonResult.failed("创建支付记录失败");
        }
    }
    
    @Operation(summary = "获取支付记录列表")
    @GetMapping
    public CommonResult<List<PaymentVO>> list() {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            List<PaymentVO> payments = paymentService.listUserPayments(userId);
            return CommonResult.success(payments);
        } catch (ApiException e) {
            log.error("获取支付记录列表失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("获取支付记录列表失败", e);
            return CommonResult.failed("获取支付记录列表失败");
        }
    }
    
    @Operation(summary = "获取支付记录详情")
    @GetMapping("/{id}")
    public CommonResult<PaymentVO> detail(@PathVariable Long id) {
        try {
            PaymentVO payment = paymentService.getPaymentDetail(id);
            return CommonResult.success(payment);
        } catch (ApiException e) {
            log.error("获取支付记录详情失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("获取支付记录详情失败", e);
            return CommonResult.failed("获取支付记录详情失败");
        }
    }
    
    @Operation(summary = "更新支付记录状态")
    @PutMapping("/{id}/status")
    public CommonResult<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            paymentService.updatePaymentStatus(id, status);
            return CommonResult.success(null);
        } catch (ApiException e) {
            log.error("更新支付记录状态失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("更新支付记录状态失败", e);
            return CommonResult.failed("更新支付记录状态失败");
        }
    }
} 