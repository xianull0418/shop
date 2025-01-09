package com.spring.back.service.impl;

import com.spring.back.mapper.UserMapper;
import com.spring.back.model.User;
import com.spring.back.model.Result;
import com.spring.back.model.PageRequest;
import com.spring.back.model.PageResult;
import com.spring.back.service.UserService;
import com.spring.back.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public Result<User> register(User user) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        
        // 密码加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setRole("user");
        user.setStatus(true);
        
        userMapper.insert(user);
        return Result.success(user);
    }
    
    @Override
    public Result<User> login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if (!user.getStatus()) {
            return Result.error("账号已被禁用");
        }
        
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!user.getPassword().equals(encryptedPassword)) {
            return Result.error("密码错误");
        }
        
        return Result.success(user);
    }
    
    @Override
    public Result<User> findById(Integer id) {
        User user = userMapper.findById(id);
        return user != null ? Result.success(user) : Result.error("用户不存在");
    }
    
    @Override
    public Result<PageResult<User>> findPage(PageRequest pageRequest) {
        List<User> list = userMapper.findPage(
            pageRequest.getKeyword(),
            pageRequest.getOffset(),
            pageRequest.getSize()
        );
        Long total = userMapper.count(pageRequest.getKeyword());
        
        return Result.success(PageResult.of(list, total, pageRequest.getSize()));
    }
    
    @Override
    public Result<Boolean> updateStatus(Integer id, Boolean status) {
        User user = userMapper.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if ("admin".equals(user.getRole())) {
            return Result.error("不能修改管理员状态");
        }
        
        userMapper.updateStatus(id, status);
        return Result.success(true);
    }
    
    @Override
    public Result<Boolean> delete(Integer id) {
        User user = userMapper.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if ("admin".equals(user.getRole())) {
            return Result.error("不能删除管理员账号");
        }
        
        userMapper.delete(id);
        return Result.success(true);
    }
    
    @Override
    public String updateAvatar(Integer userId, MultipartFile file) throws Exception {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new Exception("用户不存在");
        }

        // 获取上传目录的绝对路径
        Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();

        // 确保上传目录存在
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 更新数据库
        String avatarUrl = "/uploads/" + filename;
        userMapper.updateAvatar(userId, avatarUrl);

        return avatarUrl;
    }
} 