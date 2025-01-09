package com.spring.back.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {
    @NotNull(message = "分类ID不能为空")
    private Integer categoryId;
    
    @NotBlank(message = "商品名称不能为空")
    private String name;
    
    private String description;
    
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;
    
    private String imageUrl;
    
    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存必须大于等于0")
    private Integer stock;
    
    @NotNull(message = "状态不能为空")
    private Boolean status;
    
    // 非数据库字段
    private String categoryName;
} 