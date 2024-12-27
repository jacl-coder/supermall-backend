package com.supermall.backend.domain.invoice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.domain.invoice.entity.Invoice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InvoiceMapper extends BaseMapper<Invoice> {
} 