package com.spring.back.service;

import com.spring.back.model.User;
import com.spring.back.model.Result;
import com.spring.back.model.PageRequest;
import com.spring.back.model.PageResult;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    Result<User> register(User user);
    Result<User> login(String username, String password);
    Result<User> findById(Integer id);
    Result<PageResult<User>> findPage(PageRequest pageRequest);
    Result<Boolean> updateStatus(Integer id, Boolean status);
    Result<Boolean> delete(Integer id);
    String updateAvatar(Integer userId, MultipartFile file) throws Exception;
} 