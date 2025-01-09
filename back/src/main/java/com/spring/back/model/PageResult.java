package com.spring.back.model;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> list;
    private Long total;
    private Integer pages;
    
    public static <T> PageResult<T> of(List<T> list, Long total, Integer size) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPages((int) Math.ceil((double) total / size));
        return result;
    }
} 