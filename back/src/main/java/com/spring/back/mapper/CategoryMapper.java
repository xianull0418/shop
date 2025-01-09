package com.spring.back.mapper;

import com.spring.back.model.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {
    @Select("SELECT * FROM categories WHERE status = 1 ORDER BY sort_order ASC")
    List<Category> findAll();
    
    @Select("SELECT * FROM categories WHERE id = #{id}")
    Category findById(Integer id);
    
    @Insert("INSERT INTO categories(name, sort_order, status) VALUES(#{name}, #{sortOrder}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);
    
    @Update("UPDATE categories SET name = #{name}, sort_order = #{sortOrder}, status = #{status} WHERE id = #{id}")
    int update(Category category);
    
    @Delete("DELETE FROM categories WHERE id = #{id}")
    int delete(Integer id);
    
    @Select("SELECT * FROM categories " +
            "WHERE name LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY sort_order " +
            "LIMIT #{offset}, #{size}")
    List<Category> findPage(@Param("keyword") String keyword, 
                          @Param("offset") Integer offset, 
                          @Param("size") Integer size);
    
    @Select("SELECT COUNT(*) FROM categories " +
            "WHERE name LIKE CONCAT('%', #{keyword}, '%')")
    Long count(@Param("keyword") String keyword);
    
    @Select("SELECT * FROM categories WHERE name = #{name}")
    Category findByName(String name);
} 