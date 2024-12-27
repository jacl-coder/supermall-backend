package com.supermall.backend.domain.invoice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.invoice.dto.InvoiceRequest;
import com.supermall.backend.domain.invoice.entity.Invoice;
import com.supermall.backend.domain.invoice.mapper.InvoiceMapper;
import com.supermall.backend.domain.invoice.service.InvoiceService;
import com.supermall.backend.domain.order.entity.Order;
import com.supermall.backend.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl extends ServiceImpl<InvoiceMapper, Invoice> implements InvoiceService {

    private final OrderService orderService;

    @Override
    @Transactional
    public Invoice applyInvoice(InvoiceRequest request, Integer userId) {
        // 检查订单是否存在
        Order order = orderService.getOrder(request.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 检查订单是否属于当前用户
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        
        // 检查是否已经申请过发票
        Invoice existingInvoice = getInvoiceByOrderId(request.getOrderId());
        if (existingInvoice != null) {
            throw new BusinessException("该订单已申请过发票");
        }
        
        // 创建发票记录
        Invoice invoice = new Invoice();
        invoice.setOrderId(request.getOrderId());
        invoice.setUserId(userId);
        invoice.setType(request.getType());
        invoice.setTitleType(request.getTitleType());
        invoice.setTitle(request.getTitle());
        invoice.setTaxNumber(request.getTaxNumber());
        invoice.setAmount(order.getTotalAmount());
        invoice.setContent("商品销售");
        invoice.setStatus(0); // 待开具
        invoice.setCompanyAddress(request.getCompanyAddress());
        invoice.setCompanyPhone(request.getCompanyPhone());
        invoice.setBankName(request.getBankName());
        invoice.setBankAccount(request.getBankAccount());
        invoice.setCreateTime(LocalDateTime.now());
        invoice.setUpdateTime(LocalDateTime.now());
        invoice.setRemark(request.getRemark());
        
        // 保存发票信息
        save(invoice);
        
        return invoice;
    }

    @Override
    @Transactional
    public Invoice issueInvoice(Integer invoiceId) {
        Invoice invoice = getById(invoiceId);
        if (invoice == null) {
            throw new BusinessException("发票不存在");
        }
        
        if (invoice.getStatus() != 0) {
            throw new BusinessException("发票状态不正确");
        }
        
        // 生成发票号码（实际项目中可能需要调用专门的发票服务）
        String invoiceNumber = generateInvoiceNumber();
        
        invoice.setStatus(1); // 已开具
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setIssuedTime(LocalDateTime.now());
        invoice.setUpdateTime(LocalDateTime.now());
        
        updateById(invoice);
        
        return invoice;
    }

    @Override
    @Transactional
    public void invalidateInvoice(Integer invoiceId) {
        Invoice invoice = getById(invoiceId);
        if (invoice == null) {
            throw new BusinessException("发票不存在");
        }
        
        if (invoice.getStatus() != 1) {
            throw new BusinessException("只能作废已开具的发票");
        }
        
        invoice.setStatus(2); // 已作废
        invoice.setUpdateTime(LocalDateTime.now());
        
        updateById(invoice);
    }

    @Override
    public Invoice getInvoiceById(Integer invoiceId) {
        return getById(invoiceId);
    }

    @Override
    public Page<Invoice> getUserInvoices(Integer userId, Integer page, Integer size) {
        Page<Invoice> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Invoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Invoice::getUserId, userId)
                .orderByDesc(Invoice::getCreateTime);
        
        return page(pageParam, wrapper);
    }

    @Override
    public Invoice getInvoiceByOrderId(Integer orderId) {
        LambdaQueryWrapper<Invoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Invoice::getOrderId, orderId);
        return getOne(wrapper);
    }

    // 生成发票号码（示例实现，实际项目中需要对接专门的发票服务）
    private String generateInvoiceNumber() {
        return String.format("FP%s%06d",
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")),
                (int) (Math.random() * 1000000));
    }
} 