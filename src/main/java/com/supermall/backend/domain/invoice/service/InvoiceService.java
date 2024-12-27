package com.supermall.backend.domain.invoice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.invoice.dto.InvoiceRequest;
import com.supermall.backend.domain.invoice.entity.Invoice;

public interface InvoiceService {
    /**
     * 申请发票
     */
    Invoice applyInvoice(InvoiceRequest request, Integer userId);
    
    /**
     * 开具发票
     */
    Invoice issueInvoice(Integer invoiceId);
    
    /**
     * 作废发票
     */
    void invalidateInvoice(Integer invoiceId);
    
    /**
     * 获取发票详情
     */
    Invoice getInvoiceById(Integer invoiceId);
    
    /**
     * 获取用户发票列表
     */
    Page<Invoice> getUserInvoices(Integer userId, Integer page, Integer size);
    
    /**
     * 获取订单发票
     */
    Invoice getInvoiceByOrderId(Integer orderId);
} 