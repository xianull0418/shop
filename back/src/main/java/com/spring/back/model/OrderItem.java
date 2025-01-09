package com.spring.back.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderItem extends BaseEntity {
    @NotNull(message = "订单ID不能为空")
    private Integer orderId;
    
    @NotNull(message = "商品ID不能为空")
    private Integer productId;
    
    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量必须大于0")
    private Integer quantity;
    
    @NotNull(message = "商品价格不能为空")
    private BigDecimal price;
    
    @NotBlank(message = "商品名称不能为空")
    private String productName;
    
    private String productImage;
} 