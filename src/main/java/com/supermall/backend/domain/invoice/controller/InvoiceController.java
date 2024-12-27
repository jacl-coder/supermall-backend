package com.supermall.backend.domain.invoice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.security.util.SecurityUtil;
import com.supermall.backend.domain.invoice.dto.InvoiceRequest;
import com.supermall.backend.domain.invoice.entity.Invoice;
import com.supermall.backend.domain.invoice.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "发票管理", description = "发票相关接口")
@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final SecurityUtil securityUtil;

    @Operation(summary = "申请发票")
    @PostMapping
    public ResponseEntity<Invoice> applyInvoice(@Valid @RequestBody InvoiceRequest request) {
        Integer userId = securityUtil.getCurrentUserId();
        return ResponseEntity.ok(invoiceService.applyInvoice(request, userId));
    }

    @Operation(summary = "开具发票")
    @PostMapping("/{id}/issue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Invoice> issueInvoice(@PathVariable("id") Integer invoiceId) {
        return ResponseEntity.ok(invoiceService.issueInvoice(invoiceId));
    }

    @Operation(summary = "作废发票")
    @PostMapping("/{id}/invalidate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> invalidateInvoice(@PathVariable("id") Integer invoiceId) {
        invoiceService.invalidateInvoice(invoiceId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "获取发票详情")
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable("id") Integer invoiceId) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
    }

    @Operation(summary = "获取用户发票列表")
    @GetMapping
    public ResponseEntity<Page<Invoice>> getUserInvoices(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Integer userId = securityUtil.getCurrentUserId();
        return ResponseEntity.ok(invoiceService.getUserInvoices(userId, page, size));
    }

    @Operation(summary = "获取订单发票")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Invoice> getOrderInvoice(@PathVariable Integer orderId) {
        return ResponseEntity.ok(invoiceService.getInvoiceByOrderId(orderId));
    }
} 