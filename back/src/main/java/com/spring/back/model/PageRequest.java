package com.spring.back.model;

import lombok.Data;

@Data
public class PageRequest {
    private Integer page = 1;
    private Integer size = 10;
    private String keyword;
    
    public Integer getOffset() {
        return (page - 1) * size;
    }
} 