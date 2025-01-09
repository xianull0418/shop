package com.spring.back.controller.admin;

import com.spring.back.model.Order;
import com.spring.back.model.Result;
import com.spring.back.service.OrderService;
import com.spring.back.annotation.RequireAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequireAdmin
public class AdminOrderController {
    
    @Autowired
    private OrderService orderService;

    @GetMapping
    public Result<List<Order>> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public Result<Order> findById(@PathVariable Integer id) {
        return orderService.findById(id);
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        return orderService.updateStatus(id, status);
    }
} 