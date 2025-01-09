package com.spring.back.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity {
    
    private Integer userId;
    
    @NotNull(message = "订单总金额不能为空")
    private BigDecimal totalAmount;
    
    private String remark;
    
    @NotEmpty(message = "订单项不能为空")
    private List<OrderItem> items;
    
    private String status; // pending（待支付）, paid（已支付）, completed（已完成）, cancelled（已取消）
    
    private String address;
    
    private String phone;
    
    // 非数据库字段
    private String username;
    
    private String statusText;
    
    public String getStatusText() {
        if (status == null) return "";
        switch (status) {
            case "pending": return "待支付";
            case "paid": return "已支付";
            case "completed": return "已完成";
            case "cancelled": return "已取消";
            default: return status;
        }
    }
} 