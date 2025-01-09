package com.spring.back.mapper;

import com.spring.back.model.Order;
import com.spring.back.model.OrderItem;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface OrderMapper {
    @Insert("INSERT INTO orders(user_id, total_amount, status, remark, address, phone) " +
            "VALUES(#{userId}, #{totalAmount}, #{status}, #{remark}, #{address}, #{phone})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Insert("INSERT INTO order_items(order_id, product_id, product_name, product_image, quantity, price) " +
            "VALUES(#{orderId}, #{productId}, #{productName}, #{productImage}, #{quantity}, #{price})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrderItem(OrderItem item);

    @Select("SELECT o.*, u.username FROM orders o " +
            "LEFT JOIN users u ON o.user_id = u.id " +
            "WHERE o.id = #{id}")
    Order findById(Integer id);

    @Select("SELECT o.*, u.username FROM orders o " +
            "LEFT JOIN users u ON o.user_id = u.id " +
            "WHERE o.user_id = #{userId} " +
            "ORDER BY o.created_at DESC")
    List<Order> findByUserId(Integer userId);

    @Select("SELECT oi.*, p.name as product_name, p.image_url as product_image " +
            "FROM order_items oi " +
            "LEFT JOIN products p ON oi.product_id = p.id " +
            "WHERE oi.order_id = #{orderId}")
    List<OrderItem> findOrderItems(Integer orderId);

    @Update("UPDATE orders SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);
    
    @Update("UPDATE orders SET " +
            "total_amount = #{totalAmount}, " +
            "remark = #{remark}, " +
            "status = #{status}, " +
            "address = #{address}, " +
            "phone = #{phone} " +
            "WHERE id = #{id}")
    int update(Order order);

    @Select("SELECT o.*, u.username as username FROM orders o " +
            "LEFT JOIN users u ON o.user_id = u.id " +
            "ORDER BY o.created_at DESC")
    List<Order> findAll();
} 