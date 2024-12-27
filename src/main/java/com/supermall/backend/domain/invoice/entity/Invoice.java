package com.supermall.backend.domain.invoice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("invoice")
@EqualsAndHashCode(callSuper = false)
public class Invoice {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer orderId;
    private Integer userId;
    
    // 发票类型：1-普通发票，2-增值税专用发票
    private Integer type;
    
    // 发票抬头类型：1-个人，2-企业
    private Integer titleType;
    
    // 发票抬头
    private String title;
    
    // 纳税人识别号（企业发票必填）
    private String taxNumber;
    
    // 发票金额
    private BigDecimal amount;
    
    // 发票内容
    private String content;
    
    // 发票状态：0-待开具，1-已开具，2-已作废
    private Integer status;
    
    // 企业地址（增值税专用发票必填）
    private String companyAddress;
    
    // 企业电话（增值税专用发票必填）
    private String companyPhone;
    
    // 开户银行（增值税专用发票必填）
    private String bankName;
    
    // 银行账号（增值税专用发票必填）
    private String bankAccount;
    
    // 发票号码
    private String invoiceNumber;
    
    // 开票时间
    private LocalDateTime issuedTime;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 备注
    private String remark;
} 