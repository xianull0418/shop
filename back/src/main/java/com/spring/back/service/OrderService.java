package com.spring.back.service;

import com.spring.back.model.Order;
import com.spring.back.model.Result;
import java.util.List;

public interface OrderService {
    Result<Order> create(Order order);
    Result<Order> findById(Integer id);
    Result<List<Order>> findByUserId(Integer userId);
    Result<Boolean> updateStatus(Integer id, String status);
    Result<List<Order>> findAll();
} 