package com.spring.back.service.impl;

import com.spring.back.mapper.OrderMapper;
import com.spring.back.mapper.ProductMapper;
import com.spring.back.model.Order;
import com.spring.back.model.OrderItem;
import com.spring.back.model.Product;
import com.spring.back.model.Result;
import com.spring.back.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ProductMapper productMapper;

    @Override
    public Result<List<Order>> findAll() {
        List<Order> orders = orderMapper.findAll();
        // 获取每个订单的详细信息
        for (Order order : orders) {
            order.setItems(orderMapper.findOrderItems(order.getId()));
        }
        return Result.success(orders);
    }

    @Override
    @Transactional
    public Result<Order> create(Order order) {
        order.setStatus("pending");
        orderMapper.insert(order);
        
        for (OrderItem item : order.getItems()) {
            item.setOrderId(order.getId());
            
            // 查询商品信息
            Product product = productMapper.findById(item.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在：" + item.getProductId());
            }
            if (!product.getStatus()) {
                throw new RuntimeException("商品已下架：" + product.getName());
            }
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品库存不足：" + product.getName());
            }
            
            // 设置商品信息
            item.setProductName(product.getName());
            item.setProductImage(product.getImageUrl());
            item.setPrice(product.getPrice());
            
            // 更新库存
            product.setStock(product.getStock() - item.getQuantity());
            productMapper.update(product);
            
            orderMapper.insertOrderItem(item);
        }
        
        return Result.success(order);
    }

    @Override
    public Result<Order> findById(Integer id) {
        Order order = orderMapper.findById(id);
        if (order != null) {
            order.setItems(orderMapper.findOrderItems(id));
            return Result.success(order);
        }
        return Result.error("订单不存在");
    }

    @Override
    public Result<List<Order>> findByUserId(Integer userId) {
        List<Order> orders = orderMapper.findByUserId(userId);
        for (Order order : orders) {
            order.setItems(orderMapper.findOrderItems(order.getId()));
        }
        return Result.success(orders);
    }

    @Override
    public Result<Boolean> updateStatus(Integer id, String status) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        orderMapper.updateStatus(id, status);
        return Result.success(true);
    }
} 