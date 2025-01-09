package com.spring.back.controller.api;

import com.spring.back.model.Order;
import com.spring.back.model.Result;
import com.spring.back.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result<Order> create(@RequestBody Order order, HttpServletRequest request) {
        String userIdStr = (String) request.getAttribute("userId");
        if (userIdStr == null) {
            log.error("创建订单失败：用户未登录");
            return Result.error("请先登录");
        }
        
        Integer userId = Integer.parseInt(userIdStr);
        order.setUserId(userId);
        log.info("创建订单: {}", order);
        return orderService.create(order);
    }

    @GetMapping("/{id}")
    public Result<Order> findById(@PathVariable Integer id, HttpServletRequest request) {
        String userIdStr = (String) request.getAttribute("userId");
        if (userIdStr == null) {
            log.error("查询订单详情失败：用户未登录");
            return Result.error("请先登录");
        }
        
        log.info("查询订单详情: {}", id);
        Result<Order> result = orderService.findById(id);
        if (result.isSuccess()) {
            // 验证订单所属用户
            Integer userId = Integer.parseInt(userIdStr);
            if (!userId.equals(result.getData().getUserId())) {
                return Result.error("无权访问此订单");
            }
        }
        return result;
    }

    @GetMapping("/my")
    public Result<List<Order>> findMyOrders(HttpServletRequest request) {
        String userIdStr = (String) request.getAttribute("userId");
        if (userIdStr == null) {
            log.error("查询订单列表失败：用户未登录");
            return Result.error("请先登录");
        }
        
        Integer userId = Integer.parseInt(userIdStr);
        log.info("查询我的订单列表: userId={}", userId);
        return orderService.findByUserId(userId);
    }

    @PostMapping("/{id}/cancel")
    public Result<Boolean> cancelOrder(@PathVariable Integer id, HttpServletRequest request) {
        String userIdStr = (String) request.getAttribute("userId");
        if (userIdStr == null) {
            log.error("取消订单失败：用户未登录");
            return Result.error("请先登录");
        }

        Integer userId = Integer.parseInt(userIdStr);
        log.info("取消订单: orderId={}, userId={}", id, userId);
        
        // 验证订单所属用户
        Result<Order> orderResult = orderService.findById(id);
        if (orderResult.isSuccess()) {
            if (!userId.equals(orderResult.getData().getUserId())) {
                return Result.error("无权取消此订单");
            }
            if (!"pending".equals(orderResult.getData().getStatus())) {
                return Result.error("只能取消待支付的订单");
            }
        } else {
            return Result.error("订单不存在");
        }
        
        return orderService.updateStatus(id, "cancelled");
    }

    @PostMapping("/{id}/pay")
    public Result<Boolean> payOrder(@PathVariable Integer id, HttpServletRequest request) {
        String userIdStr = (String) request.getAttribute("userId");
        if (userIdStr == null) {
            log.error("支付订单失败：用户未登录");
            return Result.error("请先登录");
        }

        Integer userId = Integer.parseInt(userIdStr);
        log.info("支付订单: orderId={}, userId={}", id, userId);
        
        // 验证订单所属用户
        Result<Order> orderResult = orderService.findById(id);
        if (orderResult.isSuccess()) {
            if (!userId.equals(orderResult.getData().getUserId())) {
                return Result.error("无权支付此订单");
            }
            if (!"pending".equals(orderResult.getData().getStatus())) {
                return Result.error("只能支付待支付的订单");
            }
        } else {
            return Result.error("订单不存在");
        }
        
        return orderService.updateStatus(id, "paid");
    }
} 