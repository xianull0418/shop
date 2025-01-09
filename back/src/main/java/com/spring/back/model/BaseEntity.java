package com.spring.back.model;

import lombok.Data;
import java.util.Date;

@Data
public class BaseEntity {
    private Integer id;
    private Date createdAt;
    private Date updatedAt;
} 