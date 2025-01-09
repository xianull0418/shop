package com.spring.back.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {
    @NotBlank(message = "分类名称不能为空")
    private String name;
    
    @NotNull(message = "排序不能为空")
    @Min(value = 0, message = "排序必须大于等于0")
    private Integer sortOrder;
    
    @NotNull(message = "状态不能为空")
    private Boolean status;
    
    private String icon;
} 