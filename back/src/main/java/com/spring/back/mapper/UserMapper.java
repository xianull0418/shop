package com.spring.back.mapper;

import com.spring.back.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);
    
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Integer id);
    
    @Insert("INSERT INTO users(username, password, phone, role, status, avatar) " +
            "VALUES(#{username}, #{password}, #{phone}, #{role}, #{status}, " +
            "COALESCE(#{avatar}, '/uploads/default-avatar.png'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Select("SELECT id, username, phone, role, status, avatar, created_at " +
            "FROM users " +
            "WHERE username LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY created_at DESC " +
            "LIMIT #{offset}, #{size}")
    List<User> findPage(@Param("keyword") String keyword,
                       @Param("offset") Integer offset,
                       @Param("size") Integer size);
    
    @Select("SELECT COUNT(*) FROM users " +
            "WHERE username LIKE CONCAT('%', #{keyword}, '%')")
    Long count(@Param("keyword") String keyword);
    
    @Update("UPDATE users SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Boolean status);
    
    @Delete("DELETE FROM users WHERE id = #{id}")
    int delete(Integer id);
    
    @Update("UPDATE users SET avatar = #{avatar} WHERE id = #{id}")
    int updateAvatar(@Param("id") Integer id, @Param("avatar") String avatar);
} 