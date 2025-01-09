package com.spring.back.mapper;

import com.spring.back.model.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {
    @Select("SELECT p.*, c.name as category_name FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id " +
            "WHERE p.status = 1")
    List<Product> findAll();
    
    @Select("SELECT p.*, c.name as category_name FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id " +
            "WHERE p.category_id = #{categoryId} AND p.status = 1")
    List<Product> findByCategory(Integer categoryId);
    
    @Select("SELECT p.*, c.name as category_name FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id " +
            "WHERE p.id = #{id}")
    Product findById(Integer id);
    
    @Insert("INSERT INTO products(category_id, name, description, price, image_url, stock, status) " +
            "VALUES(#{categoryId}, #{name}, #{description}, #{price}, #{imageUrl}, #{stock}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);
    
    @Update("UPDATE products SET category_id = #{categoryId}, name = #{name}, " +
            "description = #{description}, price = #{price}, image_url = #{imageUrl}, " +
            "stock = #{stock}, status = #{status} WHERE id = #{id}")
    int update(Product product);
    
    @Delete("DELETE FROM products WHERE id = #{id}")
    int delete(Integer id);
    
    @Select("SELECT COUNT(*) FROM products WHERE category_id = #{categoryId}")
    int countByCategoryId(Integer categoryId);
} 